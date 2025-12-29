package gambi.zerone.gbtranslate.repository

import gambi.zerone.gbtranslate.entity.TranslateHistory

interface TranslateHistoryRepository {
    suspend fun getAllHistories():List<TranslateHistory>
    suspend fun deleteHistory(translateHistory: TranslateHistory)
    suspend fun insertHistory(translateHistory: TranslateHistory)
    suspend fun getTimestamps():List<String>
}