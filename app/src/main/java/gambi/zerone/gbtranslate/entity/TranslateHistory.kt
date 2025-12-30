package gambi.zerone.gbtranslate.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "translate_history")
data class TranslateHistory(
    @PrimaryKey val id: Long = System.currentTimeMillis(),
    val inputLanguage: String,
    val outputLanguage: String,
    val inputText: String,
    val outputText: String
)