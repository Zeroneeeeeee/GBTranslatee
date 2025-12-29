package gambi.zerone.gbtranslate

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.gbtranslate.R
import com.google.mlkit.nl.translate.TranslateLanguage
import gambi.zerone.gbtranslate.utils.LanguageType
import gambi.zerone.gbtranslate.utils.SharedPreference
import gambi.zerone.gbtranslate.view.camera.CameraScreen
import gambi.zerone.gbtranslate.view.camera.ImageTranslateScreen
import gambi.zerone.gbtranslate.view.conversation.ConversationHistoryScreen
import gambi.zerone.gbtranslate.view.conversation.ConversationScreen
import gambi.zerone.gbtranslate.view.home.HomeScreen
import gambi.zerone.gbtranslate.view.onboarding.OnBoardingScreen
import gambi.zerone.gbtranslate.view.selectlanguage.ChooseLanguageScreen
import gambi.zerone.gbtranslate.view.setting.LanguageScreen
import gambi.zerone.gbtranslate.view.setting.SettingScreen
import gambi.zerone.gbtranslate.view.setting.UiMode
import gambi.zerone.gbtranslate.view.study.FlashCardListScreen
import gambi.zerone.gbtranslate.view.study.FlashcardScreen
import gambi.zerone.gbtranslate.view.study.StudyScreen
import gambi.zerone.gbtranslate.view.study.UpsertFlashcardScreen
import gambi.zerone.gbtranslate.view.study.listLessons
import gambi.zerone.gbtranslate.view.texttranslator.TextTranslatorScreen
import gambi.zerone.gbtranslate.view.translatehistory.TranslateHistoryScreen

@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    application: Application,
    activity: Activity,
    localizedContext: Context,
    getLocale: (String) -> Unit,
    onChangeMode: (UiMode) -> Unit = {},
    language: String
) {
    val backStack = rememberSaveable { mutableStateListOf<Screen>(Screen.LanguageSetting) }
    var inputLanguage by remember { mutableStateOf(TranslateLanguage.ENGLISH) }
    var outputLanguage by remember { mutableStateOf(TranslateLanguage.VIETNAMESE) }

    if (SharedPreference.getUiMode(application.applicationContext) == UiMode.DARK) {
        Image(
            painter = painterResource(R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.None
        )
    }

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = {
            backStack.removeLastOrNull()
        },
        entryProvider = entryProvider {
            entry<Screen.Home> {
                HomeScreen(
                    localizedContext = localizedContext,
                    inputLanguage = inputLanguage,
                    outputLanguage = outputLanguage,
                    application = application,
                    toLanguageScreen = { type ->
                        backStack.add(Screen.Language(type))
                    },
                    toTextTranslate = { input, output ->
                        backStack.add(Screen.TextTranslate(input, output))
                    },
                    onExchange = {
                        val temp = inputLanguage
                        inputLanguage = outputLanguage
                        outputLanguage = temp
                    },
                    toCameraScreen = {
                        backStack.add(Screen.Camera)
                    },
                    toConversationScreen = {
                        backStack.add(Screen.Conversation)
                    },
                    toSettingScreen = {
                        backStack.add(Screen.Setting)
                    },
                    toStudyScreen = {
                        backStack.add(Screen.Study(listLessons))
                    },
                    toHistoryScreen = {
                        backStack.add(Screen.TranslateHistory)
                    }
                )
            }
            entry<Screen.Language> { (type) ->
                ChooseLanguageScreen(
                    localizedContext = localizedContext,
                    toTranslateScreen = { language ->
                        if (type == LanguageType.INPUT) {
                            inputLanguage = language
                        } else {
                            outputLanguage = language
                        }
                        backStack.removeLastOrNull()
                    },
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<Screen.TextTranslate> { (input, output) ->
                TextTranslatorScreen(
                    localizedContext = localizedContext,
                    input = input,
                    output = output,
                    inputLanguage = inputLanguage,
                    outputLanguage = outputLanguage,
                    application = application,
                    toChoosingLanguage = { type ->
                        backStack.add(Screen.Language(type))
                    },
                    onExchange = {
                        val temp = inputLanguage
                        inputLanguage = outputLanguage
                        outputLanguage = temp
                    },
                    toHistoryScreen = {
                        backStack.add(Screen.TranslateHistory)
                        Log.d("Check", backStack[backStack.size - 2].toString())
                    },
                    toCameraScreen = {
                        backStack.add(Screen.Camera)
                    },
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<Screen.Conversation> {
                ConversationScreen(
                    localizedContext = localizedContext,
                    application = application,
                    firstLanguage = outputLanguage,
                    secondLanguage = inputLanguage,
                    toLanguageType = { type ->
                        backStack.add(Screen.Language(type))
                    },
                    onBack = { backStack.removeLastOrNull() },
                    onExchange = {
                        val temp = inputLanguage
                        inputLanguage = outputLanguage
                        outputLanguage = temp
                    },
                    toHistoryScreen = {
                        backStack.add(Screen.ConversationHistory)
                    }
                )
            }
            entry<Screen.Camera> {
                CameraScreen(
                    applicationContext = application.applicationContext,
                    inputLanguage = inputLanguage,
                    outputLanguage = outputLanguage,
                    toTranslateScreen = { type ->
                        backStack.add(Screen.Language(type))
                    },
                    toImageTranslate = {
                        backStack.add(Screen.ImageTranslate(it))
                    },
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                    onExchange = {
                        val temp = inputLanguage
                        inputLanguage = outputLanguage
                        outputLanguage = temp
                    }
                )
            }
            entry<Screen.ImageTranslate> { (image) ->
                ImageTranslateScreen(
                    bitmap = image,
                    inputLanguage = inputLanguage,
                    outputLanguage = outputLanguage,
                    toLanguageScreen = {
                        backStack.add(Screen.Language(it))
                    },
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                    onExchange = {
                        val temp = inputLanguage
                        inputLanguage = outputLanguage
                        outputLanguage = temp
                    },
                    localizedContext = localizedContext
                )
            }
            entry<Screen.Setting> {
                SettingScreen(
                    localizedContext = localizedContext,
                    activity = activity,
                    toLanguageScreen = {
                        backStack.add(Screen.LanguageSetting)
                    },
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                    onChangeMode = onChangeMode
                )
            }

            entry<Screen.Study> { (lists) ->
                StudyScreen(
                    localizedContext = localizedContext,
                    onItemClick = { lesson, type ->
                        backStack.add(Screen.FlashCardList(lesson, type))
                    },
                    lists = lists
                )
            }

            entry<Screen.FlashCardList> { (list, type) ->
                FlashCardListScreen(
                    localizedContext = localizedContext,
                    lesson = list,
                    type = type,
                    toFlashCardScreen = {
                        backStack.add(Screen.FlashCard(it))
                    },
                    toAddFlashCardScreen = { lesson, flashCard ->
                        backStack.add(Screen.UpsertFlashCard(lesson, flashCard))
                    },
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            entry<Screen.FlashCard> { (list) ->
                FlashcardScreen(
                    localizedContext = localizedContext,
                    cards = list.items,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            entry<Screen.TranslateHistory> {
                TranslateHistoryScreen(
                    localizedContext = localizedContext,
                    toTranslateScreen = { history ->
                        inputLanguage = history.inputLanguage
                        outputLanguage = history.outputLanguage
                        backStack.removeLastOrNull()
                        backStack.add(Screen.TextTranslate(history.inputText))
                    },
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            entry<Screen.ConversationHistory> {
                ConversationHistoryScreen(
                    localizedContext = localizedContext,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            entry<Screen.UpsertFlashCard> { (lesson, flashCard) ->
                UpsertFlashcardScreen(
                    localizedContext = localizedContext,
                    lessonId = lesson.timestamp,
                    lessonTitle = lesson.title,
                    flashcard = flashCard,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            entry<Screen.LanguageSetting> {
                LanguageScreen(
                    localizedContext = localizedContext,
                    getLocale = getLocale,
                    language = language,
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                    hideBackButton = backStack.size == 1,
                    onChecked = {
                        if (backStack.size > 1) {
                            backStack.removeLastOrNull()
                        } else {
                            backStack.clear()
                            backStack.add(Screen.Onboarding)
                        }
                    }
                )
            }

            entry<Screen.Onboarding> {
                OnBoardingScreen(
                    localizeContext = localizedContext,
                    toHome = {
                        backStack.clear()
                        backStack.add(Screen.Home)
                    }
                )
            }
        }
    )
}


