package gambi.zerone.gbtranslate.view.selectlanguage

import android.app.Application
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.AndroidViewModel
import gambi.zerone.gbtranslate.utils.LanguagesUtils

class SelectLanguageViewModel(application: Application) : AndroidViewModel(application) {
    // LanguageCode -> DownloadState
    private val _downloadState =
        mutableStateMapOf<String, DownloadState>()
    val downloadState: Map<String, DownloadState> = _downloadState

    init {
        // 1️⃣ Set toàn bộ là Loading trước
        LanguagesUtils.getAllLanguagesCode().forEach { code ->
            _downloadState[code] = DownloadState.Loading
        }

        // 2️⃣ Query ML Kit
        LanguagesUtils.getDownloadedLanguages { downloadedList ->
            _downloadState.forEach { (code, _) ->
                _downloadState[code] =
                    if (downloadedList.contains(code))
                        DownloadState.Downloaded
                    else
                        DownloadState.NotDownloaded
            }
        }
    }


    fun getState(languageCode: String): DownloadState {
        return _downloadState[languageCode] ?: DownloadState.Loading
    }

    fun startDownload(languageCode: String) {
        _downloadState[languageCode] = DownloadState.Downloading
    }

    fun downloadSuccess(languageCode: String) {
        _downloadState[languageCode] = DownloadState.Downloaded
    }

    fun downloadFailed(languageCode: String) {
        _downloadState[languageCode] = DownloadState.NotDownloaded
    }

    fun deleteLanguage(languageCode: String) {
        _downloadState[languageCode] = DownloadState.Downloading // optional (loading)

        LanguagesUtils.deleteLanguageModel(
            languageCode,
            onSuccess = {
                _downloadState[languageCode] = DownloadState.NotDownloaded
            },
            onFailure = {
                _downloadState[languageCode] = DownloadState.Downloaded
            }
        )
    }
}

enum class DownloadState {
    NotDownloaded,
    Downloading,
    Downloaded,
    Loading
}