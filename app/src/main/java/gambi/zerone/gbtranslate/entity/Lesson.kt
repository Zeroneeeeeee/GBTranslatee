package gambi.zerone.gbtranslate.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lesson")
data class Lesson(
    @PrimaryKey val id:Long = System.currentTimeMillis(),
    val title:String,
)
