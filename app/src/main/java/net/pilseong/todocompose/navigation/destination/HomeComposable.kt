package net.pilseong.todocompose.navigation.destination

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.util.Log
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import kotlinx.coroutines.launch
import net.pilseong.todocompose.R
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.ui.components.SortMenuItems
import net.pilseong.todocompose.ui.screen.home.CreateEditNotebookDialog
import net.pilseong.todocompose.ui.screen.home.HomeScreen
import net.pilseong.todocompose.ui.screen.home.InfoDialog
import net.pilseong.todocompose.ui.screen.home.NoteAction
import net.pilseong.todocompose.ui.screen.home.NoteViewModel
import net.pilseong.todocompose.ui.viewmodel.UiState

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
        var dialogTitle by
            remember { mutableIntStateOf(R.string.note_screen_create_notebook_dialog_title) }

        // NoteAction이 add 인지 edit 인지를 구분하여 동일한 방식으로 viewmodel에서 실행
        var action by remember { mutableStateOf(NoteAction.ADD) }
        var indexSelected by remember { mutableIntStateOf(-1) }

        when (noteViewModel.uiState) {
            UiState.Loading -> {
                Log.i("PHILIP", "Ui is loading")
            }
            is UiState.Success -> {
                HomeScreen(
                    notebooks = noteViewModel.notebooks.collectAsState().value,
                    selectedNotebookIds = noteViewModel.selectedNotebooks,
                    currentNotebook = noteViewModel.currentNotebook.value,
                    noteSortingOption = (noteViewModel.uiState as UiState.Success).userData.noteSortingOptionState,
                    firstRecentNotebook = noteViewModel.firstRecentNotebook.value,
                    secondRecentNotebook = noteViewModel.secondRecentNotebook.value,
                    onClickBottomNavBar = { route ->
                        navHostController.navigate(route)
                    },
                    onFabClick = {
                        action = NoteAction.ADD
                        dialogTitle = R.string.note_screen_create_notebook_dialog_title
                        openDialog.value = true
                    },
                    onSelectNotebook = { id ->
                        scope.launch {
                            navHostController.navigate(Screen.MemoList.route)
                        }
                        noteViewModel.handleActions(NoteAction.SELECT_NOTEBOOK, notebookId = id)
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
                    onSortMenuClick = {
                        sortingOptionDialog.value = !sortingOptionDialog.value
                    }
                )

            }
        }


        DropdownMenu(
            expanded = sortingOptionDialog.value,
            onDismissRequest = { sortingOptionDialog.value = false },
            offset = if (LocalConfiguration.current.orientation == ORIENTATION_LANDSCAPE)
                DpOffset(LocalConfiguration.current.screenWidthDp.dp, (-200).dp)
            else {
                DpOffset(290.dp, (-480).dp)
            }
        ) {
            SortMenuItems { option ->
                Log.i("PHILIP", "sortMenuItem clicked $option")
                noteViewModel.handleActions(NoteAction.SORT_BY_TIME, option)
                sortingOptionDialog.value = false
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
            notebook = noteViewModel.notebooks.collectAsState().value.find { it.id == indexSelected } ?: noteViewModel.defaultNotebook.value,
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

