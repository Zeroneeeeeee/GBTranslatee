package gambi.zerone.gbtranslate.utils

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.runtime.MutableState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

class VoiceToTextParser(
    private val app: Application
) : RecognitionListener {

    private val _state = MutableStateFlow(VoiceToTextParserState())
    val state = _state.asStateFlow()

    val recognizer = SpeechRecognizer.createSpeechRecognizer(app)

    fun startListening(languageCode: String) {
        _state.update { VoiceToTextParserState() }

        if (!SpeechRecognizer.isRecognitionAvailable(app)) {
            _state.update {
                it.copy(
                    error = "Recognition is not available"
                )
            }
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                languageCode
            )
//            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 10000)
//            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 500)
//            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        recognizer.setRecognitionListener(this)
        recognizer.startListening(intent)

        _state.update {
            it.copy(
                isSpeaking = true
            )
        }
        Log.d("VoiceToText", "startListening ${_state.value.isSpeaking}")

    }

    fun stopListening() {
        _state.update {
            it.copy(
                isSpeaking = false
            )
        }
        Log.d("VoiceToText", "stopListening ${_state.value.isSpeaking}")
        recognizer.stopListening()
    }

    override fun onBeginningOfSpeech() {
        Log.d("VoiceToText", "onBeginningOfSpeech")
    }

    override fun onBufferReceived(p0: ByteArray?) = Unit

    override fun onEndOfSpeech() {
        Log.d("VoiceToText", "onEndOfSpeech")
        _state.update {
            it.copy(
                isSpeaking = false
            )
        }
    }

    override fun onError(p0: Int) {
        if (p0 == SpeechRecognizer.ERROR_CLIENT) {
            return
        }
        _state.update {
            it.copy(
                error = "Error: $p0"
            )
        }
        Log.d("VoiceToText", "onError: $p0")
    }

    override fun onEvent(p0: Int, p1: Bundle?) = Unit

    override fun onPartialResults(p0: Bundle?) = Unit

    override fun onReadyForSpeech(p0: Bundle?) {
        _state.update {
            it.copy(
                error = null
            )
        }
        Log.d("VoiceToText", "onReadyForSpeech")
    }

    override fun onResults(p0: Bundle?) {
        p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            ?.getOrNull(0)
            ?.let { result ->
                _state.update {
                    Log.d("VoiceToText", "onResults $result")
                    it.copy(
                        spokenText = it.spokenText + result,
                        // isSpeaking = false
                    )
                }
            }
    }

    override fun onRmsChanged(p0: Float) = Unit
}

fun textToSpeech(
    textToSpeech: MutableState<TextToSpeech?>,
    context: Context,
    language: String,
    text: String
) {
    if (textToSpeech.value == null) {
        textToSpeech.value = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.value?.language =
                    Locale.forLanguageTag(language)
            }
        }
    }
    textToSpeech.value?.speak(
        text,
        TextToSpeech.QUEUE_FLUSH,
        null,
        null
    )
}

data class VoiceToTextParserState(
    val spokenText: String = "",
    val isSpeaking: Boolean = false,
    val error: String? = null
)