package net.pilseong.todocompose.ui.screen.calendar

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.navigation.sharedViewModel
import net.pilseong.todocompose.ui.components.NotebooksPickerDialog
import net.pilseong.todocompose.ui.components.NotebooksPickerMode
import net.pilseong.todocompose.ui.screen.calendar.CalendarAction.MONTH_CHANGE
import net.pilseong.todocompose.ui.screen.calendar.CalendarAction.SEARCH_RANGE_CHANGE
import net.pilseong.todocompose.ui.viewmodel.MemoCalendarViewModel

fun NavGraphBuilder.memoCalendarViewComposable(
    navHostController: NavHostController,
    toScreen: (Screen) -> Unit,
) {
    composable(
        route = Screen.MemoCalendar.route,
    ) { navBackStackEntry ->

        val memoCalendarViewModel =
            navBackStackEntry.sharedViewModel<MemoCalendarViewModel>(navHostController)

        val uiState = memoCalendarViewModel.uiState
        val selectedNotebook = memoCalendarViewModel.selectedNotebook
        val tasks by memoCalendarViewModel.tasks.collectAsState()


        var openDialog by remember { mutableStateOf(false) }
        var dialogMode by remember { mutableStateOf(NotebooksPickerMode.SWITCH_NOTE_MODE) }

        CalendarScreen(
            uiState = uiState,
            tasks = tasks,
            selectedNotebook = selectedNotebook,
            toScreen = toScreen,
            onMonthChange = {
                memoCalendarViewModel.handleActions(
                    calendarAction = MONTH_CHANGE,
                    month = it
                )
            },
            onFabClicked = {},
            onAppBarTitleClick = {
                memoCalendarViewModel.getDefaultNoteCount()
                dialogMode = NotebooksPickerMode.SWITCH_NOTE_MODE
                openDialog = true
            },
            onSearchRangeAllClicked = { state, _ ->
                memoCalendarViewModel.handleActions(
                    calendarAction = SEARCH_RANGE_CHANGE,
                    boolParam = state
                )
            }
        )

        // dialogMode 가 0이면 copy to, 1이면 move to가 된다.
        NotebooksPickerDialog(
            dialogMode = dialogMode,
            visible = openDialog,
            onDismissRequest = {
                openDialog = false
            },
            notebooks = memoCalendarViewModel.notebooks.collectAsState().value,
            defaultNoteMemoCount = memoCalendarViewModel.defaultNoteMemoCount,
            onCloseClick = {
                openDialog = false
            },
            onNotebookClick = { id, action ->
                Log.d("PHILIP", "[MemoNavGraph] onNotebookClick $id, $action")
                memoCalendarViewModel.handleActions(
                    calendarAction = CalendarAction.NOTE_SWITCH,
                    notebookId = id
                )
                openDialog = false
            }
        )
    }
}