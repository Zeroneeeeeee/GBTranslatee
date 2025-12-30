package gambi.zerone.gbtranslate.repository.impl

import android.content.Context
import gambi.zerone.gbtranslate.database.AppDB
import gambi.zerone.gbtranslate.entity.Lesson
import gambi.zerone.gbtranslate.repository.LessonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LessonImpl(context: Context) : LessonRepository {
    val lessonDao = AppDB.getInstance(context).lessonDao()
    override suspend fun getAllLessons(): List<Lesson> {
        return withContext(Dispatchers.IO) {
            lessonDao.getAllLessons()
        }
    }

    override suspend fun upsertLesson(lesson: Lesson) {
        return withContext(Dispatchers.IO) {
            lessonDao.upsertLesson(lesson)
        }
    }

    override suspend fun deleteLesson(lesson: Lesson) {
        return withContext(Dispatchers.IO) {
            lessonDao.deleteLesson(lesson)
        }
    }
}