package gambi.zerone.gbtranslate.view.study

import gambi.zerone.gbtranslate.entity.FlashCard
import gambi.zerone.gbtranslate.entity.Lesson

data class LessonVM(
    val title: String,
    val items: List<FlashCardVM>,
    val timestamp: Long = System.currentTimeMillis()
)

data class FlashCardVM(
    val front: String,
    val back: String,
    val timestamp: Long = System.currentTimeMillis()
)

fun LessonVM.toLesson() = Lesson(
    id = this.timestamp,
    title = this.title
)

fun Lesson.toLessonVM(items: List<FlashCard>) = LessonVM(
    title = this.title,
    timestamp = this.id,
    items = items.map { it.toFlashCardVM() }
)

fun FlashCardVM.toFlashCard(lessonId: Long) = FlashCard(
    front = this.front,
    back = this.back,
    id = this.timestamp,
    lessonId = lessonId
)

fun FlashCard.toFlashCardVM() = FlashCardVM(
    front = this.front,
    back = this.back,
    timestamp = this.id
)