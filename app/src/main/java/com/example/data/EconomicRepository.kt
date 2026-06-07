package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class EconomicRepository(private val dao: EconomicDao) {

    // 取得所有經濟指標，如果資料庫為空，則初始化
    val allIndicators: Flow<List<EconomicIndicatorEntity>> = dao.getAllIndicators()
        .onStart {
            // 如果為空由 ViewModel 確認，或在這裡可提供預載服務
        }

    suspend fun getIndicatorById(id: String): EconomicIndicatorEntity? {
        return dao.getIndicatorById(id)
    }

    suspend fun initializeDefaultDataIfEmpty(force: Boolean = false) {
        // 先測試是否為空，再裝載 values
        val existing = dao.getAllIndicators()
        // 由於 Flow 在 Room 中不合適直接在 suspend 方法呼叫 collect 阻塞，我們可以改用一個一次性查詢來確認或直接由 DAO 檢查 count。
        // 為簡單健壯，若我們呼叫初始化時，可以透過 getIndicatorById("GDP") 測試
        if (force || dao.getIndicatorById("GDP") == null) {
            dao.insertIndicators(DefaultData.initialIndicators)
        }
    }

    suspend fun insertIndicator(indicator: EconomicIndicatorEntity) {
        dao.insertIndicator(indicator)
    }

    suspend fun insertIndicators(indicators: List<EconomicIndicatorEntity>) {
        dao.insertIndicators(indicators)
    }

    suspend fun updateBookmark(id: String, isBookmarked: Boolean) {
        dao.updateBookmark(id, isBookmarked)
    }

    // 筆記相關
    fun getNotesForIndicator(indicatorId: String): Flow<List<AnalysisNoteEntity>> {
        return dao.getNotesForIndicator(indicatorId)
    }

    fun getAllNotes(): Flow<List<AnalysisNoteEntity>> {
        return dao.getAllNotes()
    }

    suspend fun insertNote(note: AnalysisNoteEntity) {
        dao.insertNote(note)
    }

    suspend fun deleteNote(id: Int) {
        dao.deleteNoteById(id)
    }
}
