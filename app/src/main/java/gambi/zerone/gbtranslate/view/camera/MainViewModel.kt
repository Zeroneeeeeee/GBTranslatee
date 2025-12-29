package gambi.zerone.gbtranslate.view.camera

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import gambi.zerone.gbtranslate.utils.LanguagesUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class OcrBox(
    val rect: Rect,
    val text: String
)

class MainViewModel : ViewModel() {

    var ocrBoxes by mutableStateOf<List<OcrBox>>(emptyList())
        private set

    private val _bitmaps = MutableStateFlow<List<Bitmap>>(emptyList())
    val bitmaps = _bitmaps.asStateFlow()

    var recognizedText by mutableStateOf("")
        private set

    var translatedText by mutableStateOf("")
        private set

    fun onTakePhoto(bitmap: Bitmap) {
        _bitmaps.value += bitmap
    }

    fun recognizeAndTranslate(
        bitmap: Bitmap,
        sourceLang: String,
        targetLang: String
    ) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer =
            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { result ->
                ocrBoxes = result.textBlocks.flatMap { block ->
                    block.lines.flatMap { line ->
                        line.elements.mapNotNull { e ->
                            e.boundingBox?.let { OcrBox(it, e.text) }
                        }
                    }
                }
                LanguagesUtils.translationInit(
                    result.text,
                    sourceLang,
                    targetLang,
                    onSuccess = { translatedText = this }
                )
            }
            .addOnFailureListener {
                IS_LOADING.value = false
            }
    }

    fun clear() {
        translatedText = ""
        ocrBoxes = emptyList()
    }
}

