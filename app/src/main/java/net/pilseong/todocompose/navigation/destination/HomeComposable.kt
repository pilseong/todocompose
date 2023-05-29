package net.pilseong.todocompose.navigation.destination

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.ui.components.PriorityDropDown
import net.pilseong.todocompose.ui.screen.home.HomeScreen
import net.pilseong.todocompose.ui.screen.home.NoteAction
import net.pilseong.todocompose.ui.screen.home.NoteViewModel
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING

fun NavGraphBuilder.homeComposable(
    navHostController: NavHostController,
    route: String
) {
    composable(
        route = route,
    ) {
        val noteViewModel = hiltViewModel<NoteViewModel>()
        val openDialog = remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(key1 = Unit) {
            noteViewModel.observeNotebookIdChange()
            noteViewModel.getNotebooks()
        }

        HomeScreen(
            onClickBottomNavBar = { route ->
                navHostController.navigate(route)
            },
            onFabClick = {
                openDialog.value = true
            },
            onSelectNotebook = { index ->
                noteViewModel.handleActions(NoteAction.SELECT_NOTEBOOK, index)
                scope.launch {
                    delay(100)
                    navHostController.navigate(Screen.MemoList.route)
                }
            },
            notebooks = noteViewModel.notebooks.collectAsState().value
        )

        CreateNotebookDialog(
            visible = openDialog.value,
            title = noteViewModel.title.value,
            description = noteViewModel.description.value,
            priority = noteViewModel.priority.value,
            onTitleChange = {
                noteViewModel.title.value = it
            },
            onDescriptionChange = {
                noteViewModel.description.value = it
            },
            onPriorityChange = {
                noteViewModel.priority.value = it
            },
            onDismissRequest = {
                openDialog.value = false
            },
            onOKClick = {
                Log.i("PHILIP", "OK clicked")
                noteViewModel.handleActions(NoteAction.ADD)
                openDialog.value = false
                noteViewModel.title.value = ""
                noteViewModel.description.value = ""
            }
        )
    }
}


@Composable
fun CustomAlertDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        content()
    }
}

@Composable
fun CreateNotebookDialog(
    visible: Boolean,
    title: String,
    priority: Priority,
    description: String,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriorityChange: (Priority) -> Unit,
    onDismissRequest: () -> Unit,
    onOKClick: () -> Unit
) {
    if (visible) {
        CustomAlertDialog(onDismissRequest = { onDismissRequest() }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surface),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(vertical = LARGE_PADDING)
                        .padding(horizontal = XLARGE_PADDING),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Create, contentDescription = "create notebook")
                    Spacer(modifier = Modifier.width(SMALL_PADDING))
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = stringResource(id = R.string.note_screen_create_notebook_dialog_title),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,

                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = XLARGE_PADDING)
                        .padding(vertical = LARGE_PADDING),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = title,
                            label = {
                                Text(text = stringResource(id = R.string.new_task_title_placeholder))
                            },
                            onValueChange = { onTitleChange(it) },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                unfocusedIndicatorColor = Color.Transparent,
                            )
                        )
                    }
                }
                Card(
                    modifier = Modifier.padding(horizontal = XLARGE_PADDING),
                    shape = RoundedCornerShape(4.dp)
                ) {

                    PriorityDropDown(
//                modifier = Modifier.fillMaxWidth(0.95F),
                        priority = priority,
                        onPrioritySelected = {
                            onPriorityChange(it)
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .padding(horizontal = XLARGE_PADDING)
                        .padding(vertical = LARGE_PADDING),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(4.dp),
                    ) {
                        TextField(
                            label = {
                                Text(
                                    text = stringResource(id = R.string.new_task_description_placeholder)
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent
                            ),
                            value = description,
                            maxLines = 4,
                            onValueChange = { onDescriptionChange(it) }
                        )
                    }
                }

                // OK 버튼
                Text(
                    modifier = Modifier
                        .padding(horizontal = XLARGE_PADDING)
                        .align(Alignment.End)
                        .clickable {
                            onOKClick()
                        }
                        .padding(12.dp),
                    text = "OK",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview
@Composable
fun CreateNotebookDialogPreview() {
    MaterialTheme {
        CreateNotebookDialog(
            visible = true,
            title = "꽃밭",
            priority = Priority.HIGH,
            description = "수엘이",
            onTitleChange = {},
            onDescriptionChange = {},
            onPriorityChange = {},
            onOKClick = {},
            onDismissRequest = {}
        )
    }
}