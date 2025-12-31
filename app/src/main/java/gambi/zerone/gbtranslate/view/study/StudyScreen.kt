package gambi.zerone.gbtranslate.view.study

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gbtranslate.R
import gambi.zerone.gbtranslate.utils.LessonType
import gambi.zerone.gbtranslate.view.conversation.Header

@Composable
fun StudyScreen(
    modifier: Modifier = Modifier,
    onItemClick: (LessonVM, LessonType) -> Unit = { _, _ -> },
    onBack: () -> Unit = {},
    localizedContext: Context,
    viewModel: StudyViewModel = viewModel()
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val list by viewModel.lessons.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchLesson()
    }

    Content(
        localizedContext = localizedContext,
        list = list,
        onItemClick = onItemClick,
        onAddNewList = {
            showAddDialog = true
        },
        onBack = onBack
    )
    if (showAddDialog) {
        AddLessonDialog(
            localizedContext = localizedContext,
            onConfirm = { name ->
                viewModel.upsertLesson(
                    LessonVM(
                        title = name,
                        items = emptyList()
                    )
                )
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    list: List<LessonVM> = listLessons,
    localizedContext: Context,
    onItemClick: (LessonVM, LessonType) -> Unit,
    onAddNewList: () -> Unit = {},
    onBack: () -> Unit = {}
) {

    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {
        Header(title = localizedContext.resources.getString(R.string.study), onBack = onBack)
        Lists(
            localizedContext = localizedContext,
            lists = list,
            onItemClick = onItemClick,
            onAddNewList = onAddNewList
        )
    }
}

@Composable
fun Lists(
    modifier: Modifier = Modifier,
    lists: List<LessonVM>,
    localizedContext: Context,
    onAddNewList: () -> Unit = {},
    onItemClick: (LessonVM, LessonType) -> Unit
) {
    var search by remember { mutableStateOf("") }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            shape = RoundedCornerShape(16.dp),
            placeholder = { Text(localizedContext.resources.getString(R.string.search)) },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp)),
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = localizedContext.resources.getString(R.string.your_list),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp)),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onAddNewList()
                            }
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_add),
                            contentDescription = "Add",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(
                            text = localizedContext.resources.getString(R.string.new_list),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    lists.filter { it.title.contains(search) }.forEach { list ->
                        ListItem(
                            lesson = list,
                            localizedContext = localizedContext,
                            modifier = Modifier
                                .clickable {
                                    onItemClick(list, LessonType.USER_DEFINED)
                                }
                        )
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = localizedContext.resources.getString(R.string.collections),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp)),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    listLessons.forEach { lesson ->
                        ListItem(
                            lesson = lesson,
                            localizedContext = localizedContext,
                            modifier = Modifier
                                .clickable {
                                    onItemClick(lesson, LessonType.PREDEFINED)
                                }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ListItem(modifier: Modifier = Modifier, lesson: LessonVM, localizedContext: Context) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp)
    ) {
        Text(text = lesson.title, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(8.dp))
        Text(
            text = localizedContext.resources.getString(R.string.phrases, lesson.items.size),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}


var listLessons = mutableListOf(
    LessonVM(
        title = "Essentials",
        items = listOf(FlashCardVM("Hello", "Xin chào"), FlashCardVM("Goodbye", "Tạm biệt"))
    ),
    LessonVM(
        title = "Numbers",
        items = listOf(FlashCardVM("One", "Một"), FlashCardVM("Two", "Hai"))
    ),
    LessonVM(
        title = "Colors",
        items = listOf(FlashCardVM("Red", "Đỏ"), FlashCardVM("Blue", "Xanh dương"))
    ),
)