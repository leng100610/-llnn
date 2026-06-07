package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.BorderStroke
import com.example.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EconHomeScreen(
    viewModel: EconViewModel,
    modifier: Modifier = Modifier
) {
    val indicators by viewModel.indicators.collectAsStateWithLifecycle()
    val selectedId by viewModel.selectedIndicatorId.collectAsStateWithLifecycle()
    val selectedIndicator by viewModel.selectedIndicator.collectAsStateWithLifecycle()
    val activeNotes by viewModel.activeNotes.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()

    // AI states
    val aiThinking by viewModel.aiThinking.collectAsStateWithLifecycle()
    val isUpdatingData by viewModel.isUpdatingData.collectAsStateWithLifecycle()
    val aiReport by viewModel.aiReport.collectAsStateWithLifecycle()
    val chatHistory by viewModel.chatHistory.collectAsStateWithLifecycle()
    val updateLog by viewModel.updateLog.collectAsStateWithLifecycle()

    // Dialog state
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var chatInputText by remember { mutableStateOf("") }
    
    // SnackBar or state log
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Radar,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                "美經雷達",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "全美核心總經數據實時監測系統",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // 心跳連線指示燈
                    Row(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    if (isUpdatingData) Color(0xFFF1C40F) else Color(0xFF2ECC71),
                                    CircleShape
                                )
                        )
                        Text(
                            text = if (isUpdatingData) "同步中" else "雷達在線",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 頂部更新提示與 AI 線上同步按鈕
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "即時美聯儲、商務部數據同步",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = updateLog ?: "點擊同步紐可透過 AI 搜索自動更新 2026 美經數值",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = { viewModel.triggerOnlineUpdate() },
                        enabled = !isUpdatingData,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .testTag("online_update_button")
                            .height(38.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        if (isUpdatingData) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("線上更新", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Material 3 TabRow 控制切換
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("home_tabs_row")
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    text = { Text("指標大廳", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                    icon = { Icon(Icons.Default.Analytics, contentDescription = "核心指標") },
                    modifier = Modifier.testTag("tab_indicator_hall")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    text = { Text("預警日程", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "公佈行程") },
                    modifier = Modifier.testTag("tab_calendar")
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { viewModel.selectTab(2) },
                    text = { Text("AI 解讀室", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                    icon = { Icon(Icons.Default.Psychology, contentDescription = "AI 智庫對話") },
                    modifier = Modifier.testTag("tab_ai_analyst")
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { viewModel.selectTab(3) },
                    text = { Text("智庫研究筆記", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                    icon = { Icon(Icons.Default.EditNote, contentDescription = "筆記與自訂預期") },
                    modifier = Modifier.testTag("tab_my_notes")
                )
            }

            // 主要 Tab 呈現區
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (selectedTab) {
                    0 -> {
                        // 【Tab 0: 指標看板】
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                // 頂部指標橫向切換滑動條 (提供 48dp 最低標準保障)
                                Text(
                                    "選擇監測對象：",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                                )

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("indicators_scroller_row")
                                ) {
                                    items(indicators) { item ->
                                        val isSelected = item.id == selectedId
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { viewModel.selectIndicator(item.id) },
                                            label = {
                                                Text(
                                                    text = item.id,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp
                                                )
                                            },
                                            leadingIcon = {
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                                selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                                            ),
                                            modifier = Modifier.testTag("indicator_chip_${item.id}")
                                        )
                                    }
                                }

                                LazyColumn(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    item {
                                        // 全面呈現選取之指標資訊卡
                                        selectedIndicator?.let { indicator ->
                                            EconomicIndicatorCard(
                                                indicator = indicator,
                                                isSelected = true,
                                                onClick = {},
                                                onBookmarkToggle = {
                                                    viewModel.toggleBookmark(
                                                        indicator.id,
                                                        indicator.isBookmarked
                                                    )
                                                }
                                            )

                                            Spacer(modifier = Modifier.height(10.dp))

                                            // 繪製高精緻度 Canvas 曲線歷史趨勢圖
                                            EconomicLineChart(
                                                indicatorName = indicator.name,
                                                detailJson = indicator.detailJson,
                                                unit = indicator.unit
                                            )

                                            Spacer(modifier = Modifier.height(10.dp))

                                            // 指標學術與交易常識解說
                                            Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(16.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                                        alpha = 0.25f
                                                    )
                                                )
                                            ) {
                                                Column(modifier = Modifier.padding(16.dp)) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(
                                                            6.dp
                                                        )
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Info,
                                                            contentDescription = null,
                                                            tint = MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                        Text(
                                                            text = "聯邦經濟學說與交易啟示",
                                                            style = MaterialTheme.typography.titleSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.height(6.dp))
                                                    Text(
                                                        text = indicator.description,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        lineHeight = 20.sp
                                                    )

                                                    Spacer(modifier = Modifier.height(12.dp))

                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = "下次預期公佈: ${indicator.nextReleaseDate}",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                            fontWeight = FontWeight.Bold
                                                        )

                                                        Button(
                                                            onClick = { showAddNoteDialog = true },
                                                            shape = RoundedCornerShape(10.dp),
                                                            colors = ButtonDefaults.buttonColors(
                                                                containerColor = MaterialTheme.colorScheme.secondary
                                                            ),
                                                            modifier = Modifier.testTag("add_note_trigger_button")
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.AddComment,
                                                                contentDescription = null,
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                            Text("筆記預約看法", fontSize = 12.sp)
                                                        }
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(14.dp))

                                            // 查看關於當前選取之經濟標的的相關筆記
                                            if (activeNotes.isNotEmpty()) {
                                                Text(
                                                    "我的歷史看法研判紀錄 (${activeNotes.size})",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(vertical = 8.dp)
                                                )

                                                activeNotes.forEach { note ->
                                                    NoteItemRow(
                                                        note = note,
                                                        onDelete = { viewModel.deleteNote(note.id) }
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(20.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // 【Tab 1: 預警行程日程表】
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp)
                        ) {
                            Text(
                                text = "未來關聯性宏觀大事件預警",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                            )
                            Text(
                                text = "以下事件公佈前後常引發美股、債券與黄金外匯市場波動劇烈，請注意對齊時間窗口。",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .testTag("calendar_list")
                            ) {
                                items(DefaultData.economicEvents) { event ->
                                    EconomicEventRow(
                                        title = event.title,
                                        date = event.date,
                                        description = event.notes,
                                        impact = event.impact
                                    )
                                }
                            }
                        }
                    }

                    2 -> {
                        // 【Tab 2: AI 宏觀解讀與會聊專家】
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp)
                        ) {
                            // 頂部有 AI 宏觀週報一鍵生成
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                                    .testTag("ai_report_generation_card"),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(
                                        alpha = 0.4f
                                    )
                                ),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.tertiary
                                        )
                                        Text(
                                            text = "一鍵生成：美國宏觀智庫週報",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "AI 專家將即時彙整當前資料庫的所有美債、通膨與增長數據，生成針對性的資產配置宏觀週報。",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = { viewModel.generateWeeklyReport() },
                                        enabled = !aiThinking,
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.tertiary
                                        ),
                                        modifier = Modifier.testTag("generate_report_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DocumentScanner,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("即刻生成週報", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // AI 週報顯示區 (若已生成)
                            if (aiReport != null) {
                                Text(
                                    "✨ 專家宏觀報告：",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(0.4f)
                                        .background(
                                            MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                                            RoundedCornerShape(16.dp)
                                        )
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.outlineVariant,
                                            RoundedCornerShape(16.dp)
                                        )
                                        .padding(14.dp)
                                ) {
                                    val scrollState = rememberScrollState()
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(scrollState)
                                            .testTag("weekly_report_markdown")
                                    ) {
                                        Text(
                                            text = aiReport!!,
                                            style = MaterialTheme.typography.bodyMedium,
                                            lineHeight = 22.sp
                                        )
                                    }
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                            }

                            // 聊天視窗區
                            Text(
                                text = "與首席策略 AI 專家諮詢：",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            // 聊天歷史紀錄
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(0.6f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                                    )
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                        RoundedCornerShape(14.dp)
                                    )
                                    .padding(8.dp)
                            ) {
                                if (chatHistory.isEmpty()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Chat,
                                            contentDescription = null,
                                            modifier = Modifier.size(44.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                alpha = 0.5f
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "首席 AI 宏觀智庫解讀室",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleSmall,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = "我精通全美核心 GDP、聯準會利率、通膨 cpi 精微分析。您可點擊下方快捷問題或輸入提問：",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        )

                                        Spacer(modifier = Modifier.height(14.dp))

                                        // 快捷提示按鈕
                                        val shortcuts = listOf(
                                            "聯準會為何不急於降息？",
                                            "通膨 2.8% 的實質影響？",
                                            "降息對股票與黃金的引導關係？"
                                        )

                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(6.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            shortcuts.forEach { phrase ->
                                                AssistChip(
                                                    onClick = { viewModel.askAiAnalyst(phrase) },
                                                    label = {
                                                        Text(
                                                            phrase,
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    },
                                                    leadingIcon = {
                                                        Icon(
                                                            imageVector = Icons.Default.Lightbulb,
                                                            contentDescription = null,
                                                            modifier = Modifier.size(12.dp)
                                                        )
                                                    },
                                                    colors = AssistChipDefaults.assistChipColors(
                                                        containerColor = MaterialTheme.colorScheme.surface
                                                    ),
                                                    modifier = Modifier.testTag("shortcut_chip_$phrase")
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(chatHistory) { message ->
                                            Column(
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                val bubbleColor =
                                                    if (message.isUser) MaterialTheme.colorScheme.primaryContainer
                                                    else MaterialTheme.colorScheme.secondaryContainer
                                                val align =
                                                    if (message.isUser) Alignment.End else Alignment.Start

                                                Box(
                                                    modifier = Modifier
                                                        .align(align)
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .background(bubbleColor)
                                                        .padding(12.dp)
                                                        .widthIn(max = 280.dp)
                                                ) {
                                                    Text(
                                                        text = message.text,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = if (message.isUser) MaterialTheme.colorScheme.onPrimaryContainer
                                                        else MaterialTheme.colorScheme.onSecondaryContainer
                                                    )
                                                }
                                            }
                                        }

                                        if (aiThinking) {
                                            item {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(8.dp),
                                                    horizontalArrangement = Arrangement.Start,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(16.dp),
                                                        strokeWidth = 2.dp
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        "智庫 AI 正在推算總體經濟學模型中...",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // 聊天輸入列 (保障 48dp 最低觸控及無死 taps)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = chatInputText,
                                    onValueChange = { chatInputText = it },
                                    placeholder = { Text("向智庫 AI 提問總體經濟議題...", fontSize = 13.sp) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("chat_input_text")
                                        .minimumInteractiveComponentSize(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    maxLines = 2
                                )

                                Button(
                                    onClick = {
                                        if (chatInputText.isNotBlank()) {
                                            viewModel.askAiAnalyst(chatInputText)
                                            chatInputText = ""
                                        }
                                    },
                                    enabled = !aiThinking && chatInputText.isNotBlank(),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .testTag("send_chat_button")
                                        .minimumInteractiveComponentSize()
                                        .height(44.dp)
                                ) {
                                    Icon(Icons.Filled.Send, contentDescription = "發送")
                                }
                            }
                        }
                    }

                    3 -> {
                        // 【Tab 3: 智庫研究筆記 & 我的收藏看板】
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp)
                        ) {
                            Text(
                                text = "特別特選我的關注數據",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                            )
                            Text(
                                text = "在此處統一監視已收藏指標的當期數值、預估值，並統整所有您的自訂智庫筆記與預測。",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            // 撈出 bookmarked 的指標作為精華大廳
                            val bookmarkedList = indicators.filter { it.isBookmarked }

                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .testTag("bookmarked_and_notes_list"),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (bookmarkedList.isNotEmpty()) {
                                    item {
                                        Text(
                                            "關注焦點指標板 (已收藏)",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                    }

                                    items(bookmarkedList) { bookmarked ->
                                        EconomicIndicatorCard(
                                            indicator = bookmarked,
                                            isSelected = false,
                                            onClick = {
                                                viewModel.selectIndicator(bookmarked.id)
                                                viewModel.selectTab(0) // 自動導向
                                            },
                                            onBookmarkToggle = {
                                                viewModel.toggleBookmark(
                                                    bookmarked.id,
                                                    bookmarked.isBookmarked
                                                )
                                            }
                                        )
                                    }
                                }

                                item {
                                    Text(
                                        "我的跨指標智庫筆記總表",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                                    )
                                }
                            }

                            // 跨指標筆記展示
                            EconNotesListView(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }

    // 呼叫新增筆記之彈窗
    if (showAddNoteDialog && selectedIndicator != null) {
        val indicator = selectedIndicator!!
        AddCommentDialog(
            indicatorName = indicator.name,
            onDismiss = { showAddNoteDialog = false },
            onSave = { t, c, exp ->
                viewModel.addAnalysisNote(indicator.id, t, c, exp)
            }
        )
    }
}

@Composable
fun EconNotesListView(viewModel: EconViewModel) {
    // 取得所有的筆記 Flow
    val database = EconomicDatabase.getDatabase(LocalContext.current)
    val notesFlow = remember { database.dao.getAllNotes() }
    val allNotes by notesFlow.collectAsStateWithLifecycle(initialValue = emptyList<AnalysisNoteEntity>())

    if (allNotes.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.EditNote,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
                Text(
                    text = "尚未撰寫任何總經看法筆記",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "請前往第一個「指標大廳」分頁，點擊右下角「筆記預約看法」按鈕存下您的預測看法！",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .testTag("all_notes_list"),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allNotes) { note ->
                val indicatorRelation = when (note.indicatorId) {
                    "GDP" -> "GDP 國內生產總值"
                    "CPI" -> "CPI 消費者物價指數"
                    "UNEMPLOYMENT" -> "失業率指標"
                    "FED_RATE" -> "聯準會利率目標"
                    "NON_FARM" -> "非農就業報告"
                    "RETAIL_SALES" -> "零售銷售(恐怖數據)"
                    "PMI" -> "ISM 製造業採購經理人"
                    "TREASURY_10Y" -> "十年期國債殖利率"
                    else -> note.indicatorId
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SuggestionChip(
                                onClick = {},
                                label = { Text(indicatorRelation, fontWeight = FontWeight.Bold) }
                            )
                            IconButton(onClick = { viewModel.deleteNote(note.id) }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "刪除", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(note.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        if (note.userExpectation != null) {
                            Text("我對下期之自訂預期期待值: ${note.userExpectation}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color(0xFFE67E22))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(note.content, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

// 簡單轉換 dp
fun Int.toDp() = this.dp
