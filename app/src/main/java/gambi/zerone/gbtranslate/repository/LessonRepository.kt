package gambi.zerone.gbtranslate.repository

import gambi.zerone.gbtranslate.entity.Lesson

interface LessonRepository {
    suspend fun getAllLessons(): List<Lesson>
    suspend fun upsertLesson(lesson: Lesson)
    suspend fun deleteLesson(lesson: Lesson)
}