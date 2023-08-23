package net.pilseong.todocompose.ui.screen.calendar

import android.util.Log
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
import net.pilseong.todocompose.ui.screen.calendar.CalendarAction.Edit
import net.pilseong.todocompose.ui.screen.calendar.CalendarAction.MONTH_CHANGE
import net.pilseong.todocompose.ui.screen.calendar.CalendarAction.SEARCH_RANGE_CHANGE
import net.pilseong.todocompose.ui.viewmodel.MemoCalendarViewModel
import net.pilseong.todocompose.util.Constants.NEW_ITEM_ID

fun NavGraphBuilder.memoCalendarViewComposable(
    navHostController: NavHostController,
    toScreen: (Screen) -> Unit,
) {
    composable(
        route = Screen.MemoCalendar.route,
    ) { navBackStackEntry ->

        val memoCalendarViewModel =
            navBackStackEntry.sharedViewModel<MemoCalendarViewModel>(navHostController)

        val userData = memoCalendarViewModel.userData
        val selectedNotebook = memoCalendarViewModel.selectedNotebook
        val tasks by memoCalendarViewModel.tasks.collectAsState()


        var openDialog by remember { mutableStateOf(false) }
        var dialogMode by remember { mutableStateOf(NotebooksPickerMode.SWITCH_NOTE_MODE) }
        val taskUiState = memoCalendarViewModel.taskUiState
        val taskUiStateList = memoCalendarViewModel.taskUiStateList

        val selectedMonth = memoCalendarViewModel.selectedMonth

        CalendarScreen(
            userData = userData,
            taskUiState = taskUiState,
            taskUiStateList = taskUiStateList,
            tasks = tasks,
            selectedMonth = selectedMonth,
            selectedNotebook = selectedNotebook,
            toScreen = toScreen,
            onMonthChange = {
                memoCalendarViewModel.handleActions(
                    calendarAction = MONTH_CHANGE,
                    month = it
                )
            },
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
            },
            onValueChange = {
                if (it.id != NEW_ITEM_ID) {
                    memoCalendarViewModel.updateUiStateInList(it)
                } else memoCalendarViewModel.updateUiState(it)
            },
            onNewConfirm = {
                memoCalendarViewModel.handleActions(it)
                // 저장 후 초기화 를 해 주어야 한다.
            },
            onEditClicked = {
                memoCalendarViewModel.handleActions(
                    calendarAction = Edit,
                    currentMemoTask = it
                )
            },
            onTaskUiStateListClean = {
                memoCalendarViewModel.cleanUiStateInList()
            },
            onDeleteClicked = {
                memoCalendarViewModel.handleActions(
                    calendarAction = CalendarAction.DELETE,
                    currentMemoTask = it
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