package net.pilseong.todocompose.navigation.destination

import android.util.Log
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import kotlinx.coroutines.launch
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.ui.components.SortMenuItems
import net.pilseong.todocompose.ui.screen.home.CreateEditNotebookDialog
import net.pilseong.todocompose.ui.screen.home.HomeScreen
import net.pilseong.todocompose.ui.screen.home.InfoDialog
import net.pilseong.todocompose.ui.screen.home.NoteAction
import net.pilseong.todocompose.ui.screen.home.NoteViewModel

fun NavGraphBuilder.homeComposable(
    navHostController: NavHostController,
    viewModelStoreOwner: ViewModelStoreOwner,
    route: String
) {
    composable(
        route = route,
    ) {
        val noteViewModel = hiltViewModel<NoteViewModel>(
            viewModelStoreOwner = viewModelStoreOwner
        )

        val openDialog = remember { mutableStateOf(false) }
        val infoDialog = remember { mutableStateOf(false) }
        val sortingOptionDialog = remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()
        val dialogTitle =
            remember { mutableStateOf(R.string.note_screen_create_notebook_dialog_title) }

        // NoteAction이 add 인지 edit 인지를 구분하여 동일한 방식으로 viewmodel에서 실행
        val action = remember { mutableStateOf(NoteAction.ADD) }
        val indexSelected = remember { mutableStateOf(-1) }


        if (noteViewModel.firstFetch) {
            noteViewModel.observeUiState()
//            noteViewModel.observeNotebookIdChange()
//            noteViewModel.observeNoteSortingState()
//            noteViewModel.observeFirstRecentNotebookIdChange()
//            noteViewModel.observeSecondRecentNotebookIdChange()
        }

        HomeScreen(
            notebooks = noteViewModel.notebooks.collectAsState().value,
            selectedNotebookIds = noteViewModel.selectedNotebooks,
            currentNotebook = noteViewModel.currentNotebook.value,
            noteSortingOption = noteViewModel.noteSortingOptionState,
            firstRecentNotebook = noteViewModel.firstRecentNotebook.value,
            secondRecentNotebook = noteViewModel.secondRecentNotebook.value,
            onClickBottomNavBar = { route ->
                navHostController.navigate(route)
            },
            onFabClick = {
                action.value = NoteAction.ADD
                dialogTitle.value = R.string.note_screen_create_notebook_dialog_title
                openDialog.value = true
            },
            onSelectNotebook = { id ->
                noteViewModel.handleActions(NoteAction.SELECT_NOTEBOOK, notebookId = id)
                scope.launch {
                    navHostController.navigate(Screen.MemoList.route)
                }
            },
            onSelectNotebookWithLongClick = { id ->
                noteViewModel.appendMultiSelectedNotebook(id)
            },
            onBackButtonClick = {
                noteViewModel.selectedNotebooks.clear()
            },
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
            onSortMenuClick = {
                sortingOptionDialog.value = !sortingOptionDialog.value
            }
        )

        DropdownMenu(
            expanded = sortingOptionDialog.value,
            onDismissRequest = { sortingOptionDialog.value = false },
            offset = DpOffset(290.dp, (-490).dp)
        ) {
            SortMenuItems { option ->
                Log.i("PHILIP", "sortMenuItem clicked $option")
                noteViewModel.handleActions(NoteAction.SORT_BY_TIME, option)
                sortingOptionDialog.value = false
            }
        }

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

