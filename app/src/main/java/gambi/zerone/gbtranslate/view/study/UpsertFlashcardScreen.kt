package gambi.zerone.gbtranslate.view.study

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gbtranslate.R
import gambi.zerone.gbtranslate.view.conversation.Header

@Composable
fun UpsertFlashcardScreen(
    modifier: Modifier = Modifier,
    localizedContext: Context,
    lessonTitle: String = "Lesson Title",
    lessonId: Long = System.currentTimeMillis(),
    flashcard: FlashCardVM = FlashCardVM("", ""),
    onBack: () -> Unit
) {
    Column(modifier = modifier) {
        Log.d("Check", "${flashcard == FlashCardVM("", "")}")
        Header(
            title = if (flashcard.front.isBlank() && flashcard.back.isBlank()) localizedContext.resources.getString(
                R.string.add_words
            ) else localizedContext.resources.getString(
                R.string.edit_words
            ),
            onBack = onBack,
            modifier = modifier
        )
        Body(
            lessonTitle = lessonTitle,
            localizedContext = localizedContext,
            flashcard = flashcard,
            lessonId = lessonId,
            onBack = onBack,
        )
    }
}

@Composable
fun Body(
    modifier: Modifier = Modifier,
    localizedContext: Context,
    lessonTitle: String = "",
    flashcard: FlashCardVM,
    lessonId: Long = 0L,
    onBack: () -> Unit,
    viewmodel: StudyViewModel = viewModel()
) {
    var front by remember { mutableStateOf(flashcard.front) }
    var back by remember { mutableStateOf(flashcard.back) }
    var isFrontValid by remember { mutableStateOf(true) }
    var isBackValid by remember { mutableStateOf(true) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn {
            item {
                Text(
                    text = localizedContext.resources.getString(R.string.title),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = lessonTitle,
                    onValueChange = { },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        disabledBorderColor = Color.Transparent,
                        disabledTextColor = MaterialTheme.colorScheme.onBackground
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = localizedContext.resources.getString(R.string.vocabulary),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(8.dp))
                FlashcardInputField(
                    localizedContext = localizedContext,
                    front = front,
                    onFrontChange = { front = it },
                    back = back,
                    isFrontError = !isFrontValid,
                    isBackError = !isBackValid,
                    onBackChange = { back = it },
                    onExchangeClick = {
                        val temp = front
                        front = back
                        back = temp
                    }
                )
            }
            item {
                Spacer(Modifier.weight(1f))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            onBack()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFCBCDD3),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(0.3f)
                    ) {
                        Text(text = localizedContext.resources.getString(R.string.cancel))
                    }
                    Spacer(Modifier.width(16.dp))
                    Button(
                        onClick = {
                            if (front.isNotBlank() && back.isNotBlank()) {
                                isFrontValid = true
                                isBackValid = true
                                viewmodel.upsertFlashCard(
                                    flashCardVM = FlashCardVM(
                                        front = front,
                                        back = back,
                                        timestamp = flashcard.timestamp
                                    ), lessonId = lessonId
                                )
                                onBack()
                            } else {
                                Toast.makeText(
                                    localizedContext,
                                    "Please fill in all fields",
                                    Toast.LENGTH_SHORT
                                ).show()
                                if (front.isBlank()) {
                                    isFrontValid = false
                                }
                                if (back.isBlank()) {
                                    isBackValid = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFCC443),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(0.7f)
                    ) {
                        Text(text = localizedContext.resources.getString(R.string.save_vocabulary))
                    }
                }
            }
        }

    }
}

@Composable
fun FlashcardInputField(
    modifier: Modifier = Modifier,
    localizedContext: Context,
    front: String = "",
    back: String = "",
    isFrontError: Boolean = false,
    isBackError: Boolean = false,
    onFrontChange: (String) -> Unit = {},
    onBackChange: (String) -> Unit = {},
    onExchangeClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = localizedContext.resources.getString(R.string.vocabulary_front),
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF3162FF)
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = front,
            onValueChange = onFrontChange,
            placeholder = {
                Text(
                    text = localizedContext.resources.getString(R.string.enter_text_here),
                    color = Color.Gray
                )
            },
            isError = isFrontError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(152.dp)
        )
        Spacer(Modifier.height(8.dp))
        Box(contentAlignment = Alignment.Center) {
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.Gray)
            )
            Icon(
                painter = painterResource(R.drawable.ic_exchange_vertical),
                contentDescription = "Exchange",
                tint = Color.White,
                modifier = Modifier
                    .width(36.dp)
                    .background(Color.Blue, RoundedCornerShape(100.dp))
                    .clickable { onExchangeClick() }
                    .padding(2.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = localizedContext.resources.getString(R.string.define_back),
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF3162FF)
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = back,
            onValueChange = onBackChange,
            placeholder = {
                Text(
                    text = localizedContext.resources.getString(R.string.enter_text_here),
                    color = Color.Gray
                )
            },
            isError = isBackError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(152.dp)
        )
    }
}