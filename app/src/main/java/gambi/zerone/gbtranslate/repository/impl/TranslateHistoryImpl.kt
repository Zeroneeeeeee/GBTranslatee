package gambi.zerone.gbtranslate.repository.impl

import android.content.Context
import gambi.zerone.gbtranslate.database.AppDB
import gambi.zerone.gbtranslate.entity.TranslateHistory
import gambi.zerone.gbtranslate.repository.TranslateHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TranslateHistoryImpl(context: Context): TranslateHistoryRepository {
    val translateHistoryDao = AppDB.getInstance(context).translateHistoryDao()
    override suspend fun getAllHistories(): List<TranslateHistory> {
        return withContext(Dispatchers.IO) {
            translateHistoryDao.getHistoriesById()
        }
    }

    override suspend fun deleteHistory(translateHistory: TranslateHistory) {
        return withContext(Dispatchers.IO) {
            translateHistoryDao.deleteHistory(translateHistory)
        }
    }

    override suspend fun insertHistory(translateHistory: TranslateHistory) {
        return withContext(Dispatchers.IO) {
            translateHistoryDao.insertHistory(translateHistory)
        }
    }

    override suspend fun getTimestamps(): List<String> {
        return withContext(Dispatchers.IO) {
            translateHistoryDao.getTimestamps()
        }
    }

}