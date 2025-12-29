package gambi.zerone.gbtranslate.view.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import gambi.zerone.gbtranslate.entity.TranslateHistory
import gambi.zerone.gbtranslate.repository.impl.TranslateHistoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    val translateHistoryRepo = TranslateHistoryImpl(application)

    private var _history = MutableStateFlow<List<TranslateHistory>>(emptyList())
    val history = _history.asStateFlow()

    fun insertHistory(translateHistory: TranslateHistory) {
        viewModelScope.launch {
            translateHistoryRepo.insertHistory(translateHistory)
        }
    }
}