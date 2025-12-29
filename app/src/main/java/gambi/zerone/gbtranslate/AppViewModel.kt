package gambi.zerone.gbtranslate

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import gambi.zerone.gbtranslate.utils.SharedPreference
import gambi.zerone.gbtranslate.view.setting.UiMode

class AppViewModel(application: Application) : AndroidViewModel(application){
    val context = application
    var uiMode by mutableStateOf(
        SharedPreference.getUiMode(context)
    )
        private set

    fun changeMode(mode: UiMode) {
        uiMode = mode
        SharedPreference.saveUiMode(context, mode)
    }
}
