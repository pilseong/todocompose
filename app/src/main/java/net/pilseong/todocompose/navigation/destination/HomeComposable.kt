package net.pilseong.todocompose.navigation.destination

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import kotlinx.coroutines.launch
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.NotebookWithCount
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.ui.components.PriorityDropDown
import net.pilseong.todocompose.ui.screen.home.HomeScreen
import net.pilseong.todocompose.ui.screen.home.NoteAction
import net.pilseong.todocompose.ui.screen.home.NoteViewModel
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.MEDIUM_PADDING
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import net.pilseong.todocompose.ui.viewmodel.MemoViewModel
import net.pilseong.todocompose.util.MetricsUtil
import java.time.format.DateTimeFormatter

fun NavGraphBuilder.homeComposable(
    navHostController: NavHostController,
    viewModelStoreOwner: ViewModelStoreOwner,
    route: String
) {
    composable(
        route = route,
    ) {
//        val noteViewModel = hiltViewModel<NoteViewModel>()
        val noteViewModel = hiltViewModel<NoteViewModel>(
            viewModelStoreOwner = viewModelStoreOwner
        )

        val openDialog = remember { mutableStateOf(false) }
        val infoDialog = remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()
        val dialogTitle =
            remember { mutableStateOf(R.string.note_screen_create_notebook_dialog_title) }

        // NoteAction이 add 인지 edit 인지를 구분하여 동일한 방식으로 viewmodel에서 실행
        val action = remember { mutableStateOf(NoteAction.ADD) }
        val indexSelected = remember { mutableStateOf(-1) }


        if (noteViewModel.firstFetch) {
            noteViewModel.observeNotebookIdChange()
            noteViewModel.observeFirstRecentNotebookIdChange()
            noteViewModel.observeSecondRecentNotebookIdChange()
            noteViewModel.getNotebooks()
        }

        HomeScreen(
            onClickBottomNavBar = { route ->
                navHostController.navigate(route)
            },
            onFabClick = {
                action.value = NoteAction.ADD
                dialogTitle.value = R.string.note_screen_create_notebook_dialog_title
                openDialog.value = true
            },
            onSelectNotebook = { index ->
                noteViewModel.handleActions(NoteAction.SELECT_NOTEBOOK, index)
                scope.launch {
                    navHostController.navigate(Screen.MemoList.route)
                }
            },
            onSelectNotebookWithLongClick = { index ->
                noteViewModel.appendMultiSelectedNotebook(index)
            },
            onBackButtonClick = {
                noteViewModel.selectedNotebooks.clear()
            },
            notebooks = noteViewModel.notebooks.collectAsState().value,
            selectedNotebookIds = noteViewModel.selectedNotebooks,
            currentNotebook = noteViewModel.currentNotebook.value,
            onDeleteSelectedClicked = {
                noteViewModel.deleteSelectedNotebooks()
            },
            onEditClick = {
                noteViewModel.setEditProperties(noteViewModel.selectedNotebooks[0])
                action.value = NoteAction.EDIT
                dialogTitle.value = R.string.note_screen_edit_notebook_dialog_title
                openDialog.value = true
            },
            onInfoClick = { id ->
                indexSelected.value = id
                infoDialog.value = true

            },
            firstRecentNotebook = noteViewModel.firstRecentNotebook.value,
            secondRecentNotebook = noteViewModel.secondRecentNotebook.value,
        )

        CreateEditNotebookDialog(
            dialogTitle = dialogTitle.value,
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
                noteViewModel.title.value = ""
                noteViewModel.description.value = ""
                noteViewModel.priority.value = Priority.NONE
            },
            onOKClick = {
                noteViewModel.handleActions(action.value)
                openDialog.value = false
                noteViewModel.title.value = ""
                noteViewModel.description.value = ""
                noteViewModel.priority.value = Priority.NONE
            }
        )

        InfoDialog(
            visible = infoDialog.value,
            notebook = noteViewModel.notebooks.collectAsState().value.find { it.id == indexSelected.value },
            onDismissRequest = {
                infoDialog.value = false
            },
            onEditClick = { id ->
                noteViewModel.setEditProperties(id)
                action.value = NoteAction.EDIT
                dialogTitle.value = R.string.note_screen_edit_notebook_dialog_title
                infoDialog.value = false
                openDialog.value = true
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
fun InfoDialog(
    modifier: Modifier = Modifier.wrapContentHeight(),
    visible: Boolean,
    notebook: NotebookWithCount?,
    onDismissRequest: () -> Unit,
    onEditClick: (Int) -> Unit,
) {
    if (visible) {
        CustomAlertDialog(
            onDismissRequest = { onDismissRequest() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = modifier
                    .wrapContentHeight()
                    .width(320.dp),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(4.dp))
//                        .background(MaterialTheme.colorScheme.surface),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(vertical = 10.dp)
                            .padding(horizontal = XLARGE_PADDING),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.padding(top = 2.dp),
                            imageVector = Icons.Default.Info,
                            contentDescription = "notebook info icon"
                        )
                        Spacer(modifier = Modifier.width(SMALL_PADDING))
                        Text(
                            text = "Notebook Info",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Row(
                            modifier = Modifier.weight(1F),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clickable {
                                        onEditClick(notebook?.id ?: Integer.MIN_VALUE)
                                    },
                                imageVector = Icons.Default.EditNote,
                                contentDescription = "Edit Note Icon"
                            )
                            Spacer(modifier = Modifier.width(LARGE_PADDING))
                            Icon(
                                modifier = Modifier
                                    .border(
                                        BorderStroke(1.5.dp, MaterialTheme.colorScheme.onSurface),
                                        shape = RoundedCornerShape(30.dp)
                                    )
                                    .size(20.dp)
                                    .clickable {
                                        onDismissRequest()
                                    },
                                imageVector = Icons.Default.Close,
                                contentDescription = "close button"
                            )
                        }
                    }
                    Spacer(
                        Modifier
                            .height(1.dp)
                            .border(1.dp, color = MaterialTheme.colorScheme.primary)
                            .fillMaxWidth()
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(XLARGE_PADDING)
                            .verticalScroll(rememberScrollState())
                    ) {
                        val context = LocalContext.current
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val context = LocalContext.current;
                            val width = remember { mutableStateOf(0.dp) }
                            Column {
                                Text(
                                    text = stringResource(id = R.string.info_title),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    onTextLayout = { it ->
                                        width.value = MetricsUtil.convertPixelsToDp(
                                            it.size.width.toFloat(),
                                            context
                                        ).dp
                                    }
                                )
                                Surface(
                                    modifier = Modifier
                                        .padding(start = width.value)
                                        .wrapContentHeight()
                                        .fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.surface
                                ) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            modifier = Modifier
                                                .padding(horizontal = SMALL_PADDING)
                                                .wrapContentHeight(),
                                            text = notebook?.title ?: "",
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(
                            Modifier
                                .padding(bottom = MEDIUM_PADDING)
                                .height(1.dp)
                                .border(1.dp, color = MaterialTheme.colorScheme.primary)
                                .fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                val width = remember { mutableStateOf(0.dp) }
                                Text(
                                    text = stringResource(id = R.string.info_description),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    onTextLayout = { it ->
                                        width.value = MetricsUtil.convertPixelsToDp(
                                            it.size.width.toFloat(),
                                            context
                                        ).dp
                                    }
                                )
                                Surface(
                                    modifier = Modifier
                                        .padding(start = width.value)
                                        .fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.surface
                                ) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            modifier = Modifier
                                                .padding(horizontal = SMALL_PADDING)
                                                .wrapContentHeight(),
                                            text = notebook?.description ?: "",
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(
                            Modifier
                                .padding(bottom = MEDIUM_PADDING)
                                .fillMaxWidth()
                                .border(1.dp, color = MaterialTheme.colorScheme.primary)
                                .height(1.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.info_priority),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = stringResource(
                                    id = notebook?.priority?.label ?: R.string.priority_none
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(
                            Modifier
                                .padding(bottom = MEDIUM_PADDING)
                                .height(1.dp)
                                .border(1.dp, color = MaterialTheme.colorScheme.primary)
                                .fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.info_created_at),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = notebook?.createdAt?.toOffsetDateTime()
                                    ?.format(DateTimeFormatter.ISO_DATE_TIME) ?: "",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(
                            Modifier
                                .padding(bottom = MEDIUM_PADDING)
                                .height(1.dp)
                                .border(1.dp, color = MaterialTheme.colorScheme.primary)
                                .fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.info_updated_at),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = notebook?.updatedAt?.toOffsetDateTime()
                                    ?.format(DateTimeFormatter.ISO_DATE_TIME) ?: "",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(
                            Modifier
                                .padding(bottom = MEDIUM_PADDING)
                                .height(1.dp)
                                .border(1.dp, color = MaterialTheme.colorScheme.primary)
                                .fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.info_accessed_at),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = notebook?.accessedAt?.toOffsetDateTime()
                                    ?.format(DateTimeFormatter.ISO_DATE_TIME) ?: "",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(
                            Modifier
                                .padding(bottom = MEDIUM_PADDING)
                                .height(1.dp)
                                .border(1.dp, color = MaterialTheme.colorScheme.primary)
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewInfoDialog() {
    InfoDialog(
        visible = true,
        notebook = NotebookWithCount.instance(title = "askldflkasdjfkja;ls dkjf;alksd jfl;kjsdf;lkjaklddsk jf;laksdjf;lakjsd;lf"),
        onDismissRequest = {},
        onEditClick = {

        }
    )
}

@Composable
fun CreateEditNotebookDialog(
    dialogTitle: Int,
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
                        text = stringResource(id = dialogTitle),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,

                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(horizontal = XLARGE_PADDING)
                        .padding(vertical = LARGE_PADDING)
                        .fillMaxWidth()
                ) {
                    Card(
                        modifier = Modifier
                            .padding(bottom = LARGE_PADDING)
                            .fillMaxWidth(),
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
                    Card(
                        modifier = Modifier.padding(bottom = LARGE_PADDING),
                        shape = RoundedCornerShape(4.dp)
                    ) {

                        PriorityDropDown(
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
                            .padding(bottom = LARGE_PADDING),
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        // OK 버튼
                        OutlinedButton(onClick = { onDismissRequest() }) {
                            Text(text = stringResource(id = R.string.close_label))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = { onOKClick() },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = stringResource(id = R.string.save_label),
//                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }

            }
        }
    }
}

@Preview
@Composable
fun CreateNotebookDialogPreview() {
    MaterialTheme {
        CreateEditNotebookDialog(
            dialogTitle = R.string.note_screen_create_notebook_dialog_title,
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