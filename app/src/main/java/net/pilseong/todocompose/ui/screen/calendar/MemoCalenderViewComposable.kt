package net.pilseong.todocompose.ui.screen.calendar

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.navigation.sharedViewModel
import net.pilseong.todocompose.ui.screen.calendar.CalendarAction.MONTH_CHANGE
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

        CalendarScreen(
            uiState = uiState,
            tasks = tasks,
            selectedNotebook = selectedNotebook,
            toScreen = toScreen,
            onMonthChange = {
                memoCalendarViewModel.handleAction(
                    calendarAction = MONTH_CHANGE,
                    month = it
                )
            },
            onFabClicked = {},
        )
    }
}