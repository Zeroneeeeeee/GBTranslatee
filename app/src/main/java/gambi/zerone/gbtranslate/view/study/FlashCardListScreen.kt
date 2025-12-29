package gambi.zerone.gbtranslate.view.study

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gbtranslate.R
import gambi.zerone.gbtranslate.utils.LessonType
import gambi.zerone.gbtranslate.view.conversation.Header

@Composable
fun FlashCardListScreen(
    modifier: Modifier = Modifier,
    toFlashCardScreen: (LessonVM) -> Unit = {},
    toAddFlashCardScreen: (LessonVM, FlashCardVM) -> Unit = { _, _ -> },
    lesson: LessonVM = listLessons[0],
    type: LessonType = LessonType.PREDEFINED,
    localizedContext: Context,
    onBack: () -> Unit,
    viewModel: StudyViewModel = viewModel()
) {
    val dataLessons by viewModel.lessons.collectAsState()
    var typedLesson by remember {
        mutableStateOf(
            if (type == LessonType.PREDEFINED) lesson else dataLessons.find { it.timestamp == lesson.timestamp }!!
        )
    }
    Content(
        lesson = typedLesson,
        type = type,
        localizedContext = localizedContext,
        toFlashCardScreen = toFlashCardScreen,
        toAddFlashCardScreen = toAddFlashCardScreen,
        onBack = onBack
    )
}

@Composable
fun Content(
    modifier: Modifier = Modifier,
    lesson: LessonVM,
    localizedContext: Context,
    type: LessonType = LessonType.PREDEFINED,
    toFlashCardScreen: (LessonVM) -> Unit = {},
    toAddFlashCardScreen: (LessonVM, FlashCardVM) -> Unit,
    onBack: () -> Unit
) {
    Box(modifier = modifier
        .fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Header(title = lesson.title, onBack = onBack)
            FlashCardList(
                cardList = lesson.items,
                onClick = {
                    toAddFlashCardScreen(lesson, it)
                },
                localizedContext = localizedContext,
                modifier = Modifier
            )
        }
        Button(
            onClick = {
                toFlashCardScreen(lesson)
            },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFCC443),

                ),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text(localizedContext.resources.getString(R.string.start_study), color = Color.Black)
        }
        if (type == LessonType.USER_DEFINED) {
            IconButton(
                onClick = { toAddFlashCardScreen(lesson, FlashCardVM("", "")) },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                    .background(Color(0xFFFCC443), RoundedCornerShape(16.dp))
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = "Add"
                )
            }
        }
    }
}

@Composable
fun FlashCardList(
    modifier: Modifier = Modifier,
    cardList: List<FlashCardVM>,
    localizedContext: Context,
    onClick: (FlashCardVM) -> Unit = {}
) {
    var search by remember { mutableStateOf("") }
    Column(modifier = modifier.padding(16.dp)) {
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            shape = RoundedCornerShape(16.dp),
            placeholder = { Text(localizedContext.resources.getString(R.string.search)) },
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp)),
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(cardList.filter {
                it.front.contains(search) || it.back.contains(search)
            }) { card ->
                FlashCardItem(flashCard = card, onClick = {
                    Log.d("Check", card.toString())
                    onClick(card)
                })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FlashCardItem(
    modifier: Modifier = Modifier,
    flashCard: FlashCardVM = FlashCardVM("Front", "Back"),
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(text = flashCard.front, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = flashCard.back, color = MaterialTheme.colorScheme.onBackground)
    }
}