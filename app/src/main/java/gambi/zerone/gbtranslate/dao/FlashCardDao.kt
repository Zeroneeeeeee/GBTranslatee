package gambi.zerone.gbtranslate.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import gambi.zerone.gbtranslate.entity.FlashCard

@Dao
interface FlashCardDao {
    @Query("SELECT * FROM flashcard WHERE lessonId = :lessonId")
    suspend fun getAllFlashCards(lessonId: Long): List<FlashCard>
    @Upsert
    suspend fun upsertFlashCard(flashCard: FlashCard)
    @Delete
    suspend fun deleteFlashCard(flashCard: FlashCard)
}