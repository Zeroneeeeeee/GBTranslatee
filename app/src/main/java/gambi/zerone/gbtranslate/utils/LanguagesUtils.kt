package gambi.zerone.gbtranslate.utils

import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import gambi.zerone.gbtranslate.view.camera.IS_LOADING
import java.util.Locale

object LanguagesUtils {

    fun getAllLanguagesCode(): List<String> = TranslateLanguage.getAllLanguages()

    fun downloadLanguageModel(
        languageCode: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val modelManager = RemoteModelManager.getInstance()
        val model = TranslateRemoteModel.Builder(languageCode).build()
        val conditions = DownloadConditions.Builder()
            .requireWifi() // Có thể tùy chỉnh theo ý bạn
            .build()

        modelManager.download(model, conditions)
            .addOnSuccessListener {
                Log.d("LanguagesUtils", "Download Success: $languageCode")
                onSuccess()
            }
            .addOnFailureListener {
                Log.e("LanguagesUtils", "Download Failed: ${it.localizedMessage}")
                onFailure(it)
            }
    }

    fun translationInit(
        text: String,
        inputLanguage: String,
        outputLanguage: String,
        onSuccess: String.() -> Unit
    ) {
        IS_LOADING.value = true
        val option = TranslatorOptions.Builder()
            .setSourceLanguage(inputLanguage)
            .setTargetLanguage(outputLanguage)
            .build()
        val client = Translation.getClient(option)
        Log.d("TranslationInit", "Init ==> $outputLanguage")

        //Download Model if Needed
        client.downloadModelIfNeeded(
            DownloadConditions.Builder()
                .requireWifi()
                .build()
        ).addOnSuccessListener {
            Log.d("TranslationInit", "Success ==> $this")
            client.translateLanguage(text, onSuccess)
        }.addOnFailureListener {
            Log.e("TranslationInit", "Failure ==> ${it.localizedMessage.orEmpty()}")
        }
    }

    fun Translator.translateLanguage(text: String, onSuccess: String.() -> Unit) {
        translate(text).addOnSuccessListener {
            IS_LOADING.value = false
            onSuccess(it.orEmpty())
        }.addOnFailureListener {
            Log.e("TranslateLanguage", "Failure ==> ${it.localizedMessage.orEmpty()}")
        }
    }

    fun getDownloadedLanguages(onResult: (List<String>) -> Unit) {
        val modelManager = RemoteModelManager.getInstance()

        modelManager.getDownloadedModels(TranslateRemoteModel::class.java)
            .addOnSuccessListener { models ->
                val downloadedCodes = models.map { it.language }
                onResult(downloadedCodes)
            }
            .addOnFailureListener {
                Log.e("LanguagesUtils", "Error fetching models: ${it.localizedMessage}")
                onResult(emptyList())
            }
    }

    fun deleteLanguageModel(
        languageCode: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val modelManager = RemoteModelManager.getInstance()
        val model = TranslateRemoteModel.Builder(languageCode).build()

        modelManager.deleteDownloadedModel(model)
            .addOnSuccessListener {
                Log.d("LanguagesUtils", "Delete Success: $languageCode")
                onSuccess()
            }
            .addOnFailureListener {
                Log.e("LanguagesUtils", "Delete Failed: ${it.localizedMessage}")
                onFailure(it)
            }
    }

}

fun String.toLanguageDisplayName(
    targetLocale: Locale = Locale.getDefault()
): String {
    val locale = Locale.forLanguageTag(this)

    var name = locale.getDisplayLanguage(targetLocale)

    if (name.isBlank()) {
        name = locale.displayName.ifBlank { this }
    }

    return name
}