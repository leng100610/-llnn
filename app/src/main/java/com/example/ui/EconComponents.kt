package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AnalysisNoteEntity
import com.example.data.EconomicIndicatorEntity
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

// 歷史趨勢數據結構
data class ChartPoint(val label: String, val value: Double)

// 解析歷史 JSON 到繪圖點
fun parseDetailJson(jsonStr: String?): List<ChartPoint> {
    if (jsonStr.isNullOrBlank()) return emptyList()
    val points = mutableListOf<ChartPoint>()
    try {
        val arr = JSONArray(jsonStr)
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            points.add(ChartPoint(obj.getString("date"), obj.getDouble("val")))
        }
    } catch (e: Exception) {
        // fallback empty or simple parsing
    }
    return points
}

@Composable
fun EconomicLineChart(
    indicatorName: String,
    detailJson: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    val points = remember(detailJson) { parseDetailJson(detailJson) }
    if (points.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("暫無足夠歷史數據", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    val maxVal = points.maxOf { it.value }
    val minVal = points.minOf { it.value }
    val range = (maxVal - minVal).let { if (it == 0.0) 1.0 else it }

    // 軸距微調
    val upperLimit = maxVal + (range * 0.15)
    val lowerLimit = minVal - (range * 0.15)
    val finalRange = upperLimit - lowerLimit

    val primaryColor = MaterialTheme.colorScheme.primary
    val primaryAlpha = primaryColor.copy(alpha = 0.15f)
    val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    val textPaintColor = MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$indicatorName 趨勢圖",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "單位: $unit",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 繪圖 Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .testTag("historical_chart_canvas")
            ) {
                val width = size.width
                val height = size.height

                // 留白邊距
                val paddingLeft = 45.dp.toPx()
                val paddingBottom = 25.dp.toPx()
                val paddingTop = 15.dp.toPx()
                val paddingRight = 15.dp.toPx()

                val chartWidth = width - paddingLeft - paddingRight
                val chartHeight = height - paddingTop - paddingBottom

                // 1. 繪製橫向背景網格與 Y 軸標籤
                val steps = 3
                for (i in 0..steps) {
                    val ratio = i.toFloat() / steps
                    val y = paddingTop + chartHeight * (1f - ratio)
                    val gridValue = lowerLimit + finalRange * ratio

                    // 網格線
                    drawLine(
                        color = gridColor,
                        start = Offset(paddingLeft, y),
                        end = Offset(width - paddingRight, y),
                        strokeWidth = 1.dp.toPx()
                    )

                    // Y軸數值標籤 (手寫文字)
                    drawContext.canvas.nativeCanvas.drawText(
                        String.format("%.2f", gridValue),
                        10f,
                        y + 4.dp.toPx(),
                        android.graphics.Paint().apply {
                            color = textPaintColor.toArgb()
                            textSize = 10.sp.toPx()
                            typeface = android.graphics.Typeface.SANS_SERIF
                        }
                    )
                }

                // 2. 計算並投影點坐標
                val pointsCount = points.size
                val xCoords = FloatArray(pointsCount)
                val yCoords = FloatArray(pointsCount)

                for (idx in 0 until pointsCount) {
                    val xRatio = if (pointsCount > 1) idx.toFloat() / (pointsCount - 1) else 0.5f
                    val yRatio = ((points[idx].value - lowerLimit) / finalRange).toFloat()

                    xCoords[idx] = paddingLeft + chartWidth * xRatio
                    yCoords[idx] = paddingTop + chartHeight * (1f - yRatio)
                }

                // 3. 繪製漸層陰影填充
                if (pointsCount > 1) {
                    val fillPath = Path().apply {
                        moveTo(xCoords[0], yCoords[0])
                        for (idx in 1 until pointsCount) {
                            lineTo(xCoords[idx], yCoords[idx])
                        }
                        lineTo(xCoords[pointsCount - 1], paddingTop + chartHeight)
                        lineTo(xCoords[0], paddingTop + chartHeight)
                        close()
                    }
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(primaryColor.copy(alpha = 0.35f), Color.Transparent),
                            startY = paddingTop,
                            endY = paddingTop + chartHeight
                        )
                    )
                }

                // 4. 繪製主要連線
                if (pointsCount > 1) {
                    val linePath = Path().apply {
                        moveTo(xCoords[0], yCoords[0])
                        for (idx in 1 until pointsCount) {
                            // 使用貝氏擬合實現流暢弧線
                            val prevX = xCoords[idx - 1]
                            val prevY = yCoords[idx - 1]
                            val currX = xCoords[idx]
                            val currY = yCoords[idx]
                            val controlX = (prevX + currX) / 2
                            cubicTo(controlX, prevY, controlX, currY, currX, currY)
                        }
                    }
                    drawPath(
                        path = linePath,
                        color = primaryColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // 5. 繪製數據關鍵結點與 X 軸標籤
                for (idx in 0 until pointsCount) {
                    val cx = xCoords[idx]
                    val cy = yCoords[idx]

                    // 畫大圓盤外圈
                    drawCircle(
                        color = primaryColor,
                        radius = 4.dp.toPx(),
                        center = Offset(cx, cy)
                    )
                    // 畫小圓白點心
                    drawCircle(
                        color = Color.White,
                        radius = 2.dp.toPx(),
                        center = Offset(cx, cy)
                    )

                    // X 軸標籤
                    drawContext.canvas.nativeCanvas.drawText(
                        points[idx].label,
                        cx - 14.dp.toPx(),
                        height - 4.dp.toPx(),
                        android.graphics.Paint().apply {
                            color = textPaintColor.toArgb()
                            textSize = 10.sp.toPx()
                            textAlign = android.graphics.Paint.Align.LEFT
                            typeface = android.graphics.Typeface.create(android.graphics.Typeface.SANS_SERIF, android.graphics.Typeface.BOLD)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EconomicIndicatorCard(
    indicator: EconomicIndicatorEntity,
    isSelected: Boolean,
    onClick: () -> Unit,
    onBookmarkToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 根據正負數與指標類別決定趨勢色澤 (通膨與失業率下降往往是綠色，其餘數據上漲多數為好現象)
    val isBullishColor = remember(indicator) {
        val trendUp = indicator.trend == "up"
        if (indicator.id == "CPI" || indicator.id == "UNEMPLOYMENT") {
            !trendUp // CPI 與失業率越下降越是好消息 / 綠色
        } else {
            trendUp // GDP、非農、PMI 等越往上升越是好消息
        }
    }

    val themeGreen = Color(0xFF2ECC71)
    val themeRed = Color(0xFFE74C3C)
    val trendColor = if (isBullishColor) themeGreen else themeRed

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .testTag("indicator_card_${indicator.id}")
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 熱度符號
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(trendColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (indicator.trend == "up") Icons.Filled.TrendingUp else Icons.Filled.TrendingDown,
                    contentDescription = null,
                    tint = trendColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 標題及類別說明
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = indicator.name.substringBefore(" ("),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    SuggestionChip(
                        onClick = {},
                        label = { Text(indicator.category, style = MaterialTheme.typography.bodySmall, fontSize = 9.sp) },
                        modifier = Modifier.height(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "公佈期: ${indicator.releaseDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 數值區間
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = "${indicator.currentValue}${indicator.unit}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = trendColor
                )
                Text(
                    text = "前值: ${indicator.previousValue}${indicator.unit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // 收藏圖示 (提供 touch 熱區 48dp 最低標準保障)
            IconButton(
                onClick = onBookmarkToggle,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("bookmark_${indicator.id}")
            ) {
                Icon(
                    imageVector = if (indicator.isBookmarked) Icons.Filled.Star else Icons.Filled.StarBorder,
                    contentDescription = "加入最愛看板",
                    tint = if (indicator.isBookmarked) Color(0xFFF39C12) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EconomicEventRow(
    title: String,
    date: String,
    description: String,
    impact: String,
    modifier: Modifier = Modifier
) {
    val impactColor = when (impact) {
        "高" -> Color(0xFFE74C3C)
        "中" -> Color(0xFFF39C12)
        else -> Color(0xFF3498DB)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(impactColor)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "波動 $impact",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = date,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCommentDialog(
    indicatorName: String,
    onDismiss: () -> Unit,
    onSave: (title: String, content: String, expectation: Double?) -> Unit
) {
    var title by remember { mutableStateFlowOf("") }
    var content by remember { mutableStateFlowOf("") }
    var expectationStr by remember { mutableStateFlowOf("") }
    var hasError by remember { mutableStateFlowOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "撰寫 $indicatorName 分析筆記",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = expectationStr,
                    onValueChange = {
                        expectationStr = it
                        hasError = false
                    },
                    label = { Text("我預期的下期數據 (非必填)") },
                    placeholder = { Text("例如 2.4") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("forecast_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                if (hasError) {
                    Text(
                        "請輸入有效的數字表示預期值",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("筆記重點摘要 / 主題") },
                    placeholder = { Text("輸入一句話總結核心看法") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("note_title_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("多維分析說明與交易策略") },
                    placeholder = { Text("在此處記錄您對當前美國數據與資產配置的研究成果...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("note_content_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val exp = expectationStr.toDoubleOrNull()
                    if (expectationStr.isNotBlank() && exp == null) {
                        hasError = true
                    } else {
                        val t = title.ifBlank { "我的研判筆記" }
                        val c = content.ifBlank { "個人總經觀察，維持中性佈置。" }
                        onSave(t, c, exp)
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("save_note_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("存入資料庫")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dismiss_note_button")
            ) {
                Text("取消")
            }
        }
    )
}

// 輔助 StateFlow 快速配置
fun <T> mutableStateFlowOf(value: T) = mutableStateOf(value)

@Composable
fun NoteItemRow(
    note: AnalysisNoteEntity,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateString = remember(note.timestamp) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        sdf.format(Date(note.timestamp))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "撰寫時間: $dateString",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "刪除此筆記",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            if (note.userExpectation != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "個人特選下期期待值: ${note.userExpectation}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
