package gambi.zerone.gbtranslate.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import gambi.zerone.gbtranslate.entity.Lesson

@Dao
interface LessonDao {
    @Query("SELECT * FROM lesson")
    suspend fun getAllLessons(): List<Lesson>
    @Upsert
    suspend fun upsertLesson(lesson: Lesson)
    @Delete
    suspend fun deleteLesson(lesson: Lesson)
}