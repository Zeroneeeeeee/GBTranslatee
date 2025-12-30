package gambi.zerone.gbtranslate.view.texttranslator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import gambi.zerone.gbtranslate.entity.TranslateHistory
import gambi.zerone.gbtranslate.repository.impl.TranslateHistoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TextTranslatorViewModel(application: Application) : AndroidViewModel(application) {
    val translateHistoryRepo = TranslateHistoryImpl(application)

    private var _history = MutableStateFlow<List<TranslateHistory>>(emptyList())
    val history = _history.asStateFlow()

    fun insertHistory(translateHistory: TranslateHistory) {
        viewModelScope.launch {
            translateHistoryRepo.insertHistory(translateHistory)
        }
    }

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