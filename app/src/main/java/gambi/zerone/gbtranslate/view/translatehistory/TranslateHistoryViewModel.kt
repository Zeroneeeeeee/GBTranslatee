package gambi.zerone.gbtranslate.view.translatehistory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import gambi.zerone.gbtranslate.entity.TranslateHistory
import gambi.zerone.gbtranslate.repository.impl.TranslateHistoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TranslateHistoryViewModel(application: Application) : AndroidViewModel(application) {
    val translateHistoryRepo = TranslateHistoryImpl(application)

    private var _history = MutableStateFlow<List<TranslateHistory>>(emptyList())
    val history = _history.asStateFlow()

    private var _timestamps = MutableStateFlow<List<String>>(emptyList())
    val timestamps = _timestamps.asStateFlow()

    fun fetchHistory() {
        viewModelScope.launch {
            _history.value = translateHistoryRepo.getAllHistories()
        }
    }

    fun fetchTimestamps() {
        viewModelScope.launch {
            _timestamps.value = translateHistoryRepo.getTimestamps()
        }
    }

    fun deleteHistory(translateHistory: TranslateHistory) {
        viewModelScope.launch {
            translateHistoryRepo.deleteHistory(translateHistory)
            fetchHistory()
            fetchTimestamps()
        }
    }
}