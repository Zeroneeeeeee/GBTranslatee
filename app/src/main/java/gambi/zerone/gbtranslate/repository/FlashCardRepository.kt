package gambi.zerone.gbtranslate.repository

import gambi.zerone.gbtranslate.entity.FlashCard

interface FlashCardRepository {
    suspend fun getAllFlashCards(lessonId: Long): List<FlashCard>
    suspend fun upsertFlashCard(flashCard: FlashCard)
    suspend fun deleteFlashCard(flashCard: FlashCard)
}