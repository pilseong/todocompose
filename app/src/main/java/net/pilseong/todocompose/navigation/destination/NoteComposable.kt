package net.pilseong.todocompose.navigation.destination

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import net.pilseong.todocompose.R
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.navigation.sharedViewModel
import net.pilseong.todocompose.ui.screen.home.CreateEditNotebookDialog
import net.pilseong.todocompose.ui.screen.home.InfoDialog
import net.pilseong.todocompose.ui.screen.home.NoteAction
import net.pilseong.todocompose.ui.screen.home.NoteViewModel
import net.pilseong.todocompose.ui.screen.note.NoteScreen
import net.pilseong.todocompose.ui.viewmodel.UiState
import net.pilseong.todocompose.util.Constants.MEMO_LIST

fun NavGraphBuilder.noteComposable(
    navHostController: NavHostController,
) {
    composable(
        route = Screen.Notes.route
    ) { navBackStackEntry ->
        val noteViewModel = navBackStackEntry.sharedViewModel<NoteViewModel>(navHostController)

        val openDialog = remember { mutableStateOf(false) }
        val infoDialog = remember { mutableStateOf(false) }
        var dialogTitle by remember { mutableStateOf(R.string.note_screen_create_notebook_dialog_title) }

        // NoteAction이 add 인지 edit 인지를 구분하여 동일한 방식으로 viewmodel에서 실행
        var action by remember { mutableStateOf(NoteAction.ADD) }
        var indexSelected by remember { mutableStateOf(-1L) }
        var defaultNotebook = noteViewModel.defaultNotebook.value

        when (noteViewModel.uiState) {
            UiState.Loading -> {
                Log.d("PHILIP", "Ui is loading")
            }

            is UiState.Success -> {
                NoteScreen(
                    notebooks = noteViewModel.notebooks.collectAsState().value,
                    selectedNotebookIds = noteViewModel.selectedNotebooks,
                    currentNotebook = noteViewModel.currentNotebook.value,
                    noteSortingOption = (noteViewModel.uiState as UiState.Success).userData.noteSortingOptionState,
                    firstRecentNotebook = noteViewModel.firstRecentNotebook.value,
                    secondRecentNotebook = noteViewModel.secondRecentNotebook.value,
                    defaultNotebook = defaultNotebook,
                    onClickBottomNavBar = { route ->
                        navHostController.popBackStack(route, true)
                        navHostController.navigate(route)
                    },
                    onFabClick = {
                        action = NoteAction.ADD
                        dialogTitle = R.string.note_screen_create_notebook_dialog_title
                        openDialog.value = true
                    },
                    onSelectNotebook = { id ->
                        noteViewModel.handleActions(NoteAction.SELECT_NOTEBOOK, notebookId = id)
                        navHostController.navigate(MEMO_LIST)
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
                        action = NoteAction.EDIT
                        dialogTitle = R.string.note_screen_edit_notebook_dialog_title
                        openDialog.value = true
                    },
                    onInfoClick = { id ->
                        indexSelected = id
                        infoDialog.value = true

                    },
                    onSortMenuClick = { option ->
                        noteViewModel.handleActions(NoteAction.SORT_BY_TIME, option)

                    }
                )

            }
        }

        CreateEditNotebookDialog(
            dialogTitle = dialogTitle,
            visible = openDialog.value,
            mode = action == NoteAction.ADD,
            notebookInput = noteViewModel.notebookUserInput.value,
            onInputChange = { notebook ->
                noteViewModel.notebookUserInput.value = notebook
            },
            onDismissRequest = {
                openDialog.value = false
                noteViewModel.clearNotebookUserInput()
            },
            onOKClick = {
                noteViewModel.handleActions(action)
                openDialog.value = false
                noteViewModel.clearNotebookUserInput()
            }
        )

        InfoDialog(
            visible = infoDialog.value,
            notebook = noteViewModel.notebooks.collectAsState().value.find { it.id == indexSelected }
                ?: noteViewModel.defaultNotebook.value,
            onDismissRequest = {
                infoDialog.value = false
            },
            onEditClick = { id ->
                noteViewModel.setEditProperties(id)
                action = NoteAction.EDIT
                dialogTitle = R.string.note_screen_edit_notebook_dialog_title
                infoDialog.value = false
                openDialog.value = true
            }
        )
    }
}

