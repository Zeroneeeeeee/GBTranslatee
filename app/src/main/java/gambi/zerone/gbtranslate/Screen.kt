package gambi.zerone.gbtranslate

import android.graphics.Bitmap
import gambi.zerone.gbtranslate.utils.LanguageType
import gambi.zerone.gbtranslate.utils.LessonType
import gambi.zerone.gbtranslate.view.study.FlashCardVM
import gambi.zerone.gbtranslate.view.study.LessonVM

interface Screen {
    data object Home : Screen
    data class Language(var type: LanguageType) : Screen
    data class TextTranslate(var input: String = "", var output: String = "") : Screen
    data object Conversation : Screen
    data object Camera : Screen
    data class ImageTranslate(var bitmap: Bitmap) : Screen
    data class FlashCard(var lesson: LessonVM) : Screen
    data class Study(var lessons: List<LessonVM>) : Screen
    data class FlashCardList(var lesson: LessonVM, var type: LessonType) : Screen
    data object TranslateHistory : Screen
    data object ConversationHistory : Screen
    data object Setting : Screen
    data object LanguageSetting : Screen
    data class UpsertFlashCard(var lesson: LessonVM, var flashCard: FlashCardVM) : Screen
    data object Onboarding : Screen
}