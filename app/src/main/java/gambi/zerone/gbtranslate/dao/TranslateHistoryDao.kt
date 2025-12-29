package gambi.zerone.gbtranslate.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import gambi.zerone.gbtranslate.entity.TranslateHistory

@Dao
interface TranslateHistoryDao {
    @Query("SELECT * FROM translate_history ORDER BY id DESC")
    fun getHistoriesById(): List<TranslateHistory>
    @Delete
    fun deleteHistory(history: TranslateHistory)
    @Insert
    fun insertHistory(history: TranslateHistory)
    @Query("SELECT strftime('%d/%m/%Y',id/1000,'unixepoch','localtime') as date FROM translate_history GROUP BY date ORDER BY id DESC")
    suspend fun getTimestamps(): List<String>
}