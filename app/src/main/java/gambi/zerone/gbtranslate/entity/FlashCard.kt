package gambi.zerone.gbtranslate.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "flashcard", foreignKeys = [
        ForeignKey(
            entity = Lesson::class,
            parentColumns = ["id"],
            childColumns = ["lessonId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FlashCard(
    @PrimaryKey var id: Long = System.currentTimeMillis(),
    var front: String,
    var back: String,
    var lessonId: Long
)