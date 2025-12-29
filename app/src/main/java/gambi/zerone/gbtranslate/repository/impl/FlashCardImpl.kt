package gambi.zerone.gbtranslate.repository.impl

import android.content.Context
import gambi.zerone.gbtranslate.database.AppDB
import gambi.zerone.gbtranslate.entity.FlashCard
import gambi.zerone.gbtranslate.repository.FlashCardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FlashCardImpl(context: Context): FlashCardRepository {
    val flashCardDao = AppDB.getInstance(context).flashCardDao()
    override suspend fun getAllFlashCards(lessonId: Long): List<FlashCard> {
        return withContext(Dispatchers.IO) {
            flashCardDao.getAllFlashCards(lessonId)
        }
    }

    override suspend fun upsertFlashCard(flashCard: FlashCard) {
        return withContext(Dispatchers.IO) {
            flashCardDao.upsertFlashCard(flashCard)
        }
    }

    override suspend fun deleteFlashCard(flashCard: FlashCard) {
        return withContext(Dispatchers.IO) {
            flashCardDao.deleteFlashCard(flashCard)
        }
    }
}