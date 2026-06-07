package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// 經濟指標資料實體
@Entity(tableName = "economic_indicators")
data class EconomicIndicatorEntity(
    @PrimaryKey val id: String, // E.g., "GDP", "CPI", "UNEMPLOYMENT", etc.
    val name: String,           // E.g., "國內生產總值 (GDP)"
    val category: String,       // E.g., "增長", "通膨", "就業", "利率", "消費"
    val currentValue: Double,   // 最新一期數值
    val previousValue: Double,  // 前一期數值
    val unit: String,           // 單位, E.g., "%", "萬人", "點"
    val releaseDate: String,     // 公布日期
    val nextReleaseDate: String, // 下次公布日期
    val trend: String,          // "up" (上升), "down" (下降), "stable" (持平)
    val description: String,    // 解說細節
    val detailJson: String,     // 歷史趨勢點 list 串接成 JSON (E.g., [{"date":"12/25","val":2.1}])
    val isBookmarked: Boolean = false // 收藏狀態
)

// 使用者分析筆記與自訂預期
@Entity(tableName = "analysis_notes")
data class AnalysisNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val indicatorId: String,    // 關聯的指標 ID
    val title: String,          // 標題
    val content: String,        // 筆記內容
    val userExpectation: Double?, // 預期數值
    val timestamp: Long = System.currentTimeMillis() // 建立時間
)

@Dao
interface EconomicDao {
    @Query("SELECT * FROM economic_indicators ORDER BY category ASC")
    fun getAllIndicators(): Flow<List<EconomicIndicatorEntity>>

    @Query("SELECT * FROM economic_indicators WHERE id = :id LIMIT 1")
    suspend fun getIndicatorById(id: String): EconomicIndicatorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIndicators(indicators: List<EconomicIndicatorEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIndicator(indicator: EconomicIndicatorEntity)

    @Query("UPDATE economic_indicators SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmark(id: String, isBookmarked: Boolean)

    // 筆記相關
    @Query("SELECT * FROM analysis_notes WHERE indicatorId = :indicatorId ORDER BY timestamp DESC")
    fun getNotesForIndicator(indicatorId: String): Flow<List<AnalysisNoteEntity>>

    @Query("SELECT * FROM analysis_notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<AnalysisNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: AnalysisNoteEntity)

    @Query("DELETE FROM analysis_notes WHERE id = :id")
    suspend fun deleteNoteById(id: Int)
}

@Database(
    entities = [EconomicIndicatorEntity::class, AnalysisNoteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class EconomicDatabase : RoomDatabase() {
    abstract val dao: EconomicDao

    companion object {
        @Volatile
        private var INSTANCE: EconomicDatabase? = null

        fun getDatabase(context: Context): EconomicDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EconomicDatabase::class.java,
                    "economic_radar_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
