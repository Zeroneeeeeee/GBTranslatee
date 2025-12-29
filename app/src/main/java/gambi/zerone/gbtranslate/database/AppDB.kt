package gambi.zerone.gbtranslate.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import gambi.zerone.gbtranslate.dao.FlashCardDao
import gambi.zerone.gbtranslate.dao.LessonDao
import gambi.zerone.gbtranslate.dao.TranslateHistoryDao
import gambi.zerone.gbtranslate.entity.FlashCard
import gambi.zerone.gbtranslate.entity.Lesson
import gambi.zerone.gbtranslate.entity.TranslateHistory

@Database(entities = [Lesson::class, FlashCard::class, TranslateHistory::class], version = 1)
abstract class AppDB: RoomDatabase() {
    abstract fun flashCardDao(): FlashCardDao
    abstract fun lessonDao(): LessonDao
    abstract fun translateHistoryDao(): TranslateHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDB? = null
        fun getInstance(context: Context): AppDB {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDB::class.java, "translate.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { INSTANCE = it }
            }
        }

    }
}