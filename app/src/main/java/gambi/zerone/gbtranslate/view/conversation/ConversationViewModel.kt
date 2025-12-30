package gambi.zerone.gbtranslate.view.conversation

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class ConversationViewModel(application: Application) : AndroidViewModel(application) {
//    private var translator: Translator? = null
//
//    fun initTranslator(
//        inputLanguage: String,
//        outputLanguage: String
//    ) {
//        translator?.close()
//        translator = LanguagesUtils.createTranslator(inputLanguage, outputLanguage)
//    }
//
//    fun downloadLanguageIfNeeded(
//        onSuccess: () -> Unit
//    ) {
//        translator?.let {
//            LanguagesUtils.downloadLanguage(it, onSuccess)
//        }
//    }
//
//    fun translate(
//        text: String,
//        onResult: (String) -> Unit
//    ) {
//        translator?.let {
//            LanguagesUtils.translate(it, text, onResult)
//        }
//    }
//
//    override fun onCleared() {
//        translator?.close()
//        super.onCleared()
//    }
}