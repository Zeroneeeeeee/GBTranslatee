package gambi.zerone.gbtranslate.view.study

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gambi.zerone.gbtranslate.repository.impl.FlashCardImpl
import gambi.zerone.gbtranslate.repository.impl.LessonImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class StudyViewModel(application: Application) : AndroidViewModel(application) {
    val lessonRepo = LessonImpl(application)
    val flashCardRepo = FlashCardImpl(application)

    private var _lessons = MutableStateFlow<List<LessonVM>>(emptyList())
    val lessons = _lessons.asStateFlow()

    private var _flashCards = MutableStateFlow<List<FlashCardVM>>(emptyList())
    val flashCards = _flashCards.asStateFlow()

    fun fetchLesson() {
        viewModelScope.launch(Dispatchers.IO) {
            val lessonEntities = lessonRepo.getAllLessons()

            val lessonVMs = lessonEntities.map { lesson ->
                val flashCards = flashCardRepo.getAllFlashCards(lesson.id)
                lesson.toLessonVM(flashCards)
            }

            _lessons.value = lessonVMs
        }
    }

    fun fetchFlashCard(lessonId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val flashCardEntities = flashCardRepo.getAllFlashCards(lessonId)

            val flashCardVMs = flashCardEntities.map { flashCard ->
                flashCard.toFlashCardVM()
            }

            _flashCards.value = flashCardVMs
        }
    }

    fun upsertLesson(lessonVM: LessonVM) {
        viewModelScope.launch(Dispatchers.IO) {
            val lessonEntity = lessonVM.toLesson()
            lessonRepo.upsertLesson(lessonEntity)
            fetchLesson()
        }
    }

    fun upsertFlashCard(flashCardVM: FlashCardVM, lessonId: Long){
        viewModelScope.launch(Dispatchers.IO) {
            val flashCardEntity = flashCardVM.toFlashCard(lessonId)
            flashCardRepo.upsertFlashCard(flashCardEntity)
            fetchLesson()
        }
    }
}