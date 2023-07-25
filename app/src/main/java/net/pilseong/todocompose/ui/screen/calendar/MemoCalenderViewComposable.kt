package net.pilseong.todocompose.ui.screen.calendar

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.navigation.sharedViewModel
import net.pilseong.todocompose.ui.viewmodel.MemoViewModel

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

        // starting point 시작점
        val loadedDates by memoCalendarViewModel.visibleDates.collectAsState()

        // 현재 선택 된 날짜
        val selectedDate by memoCalendarViewModel.selectedDate.collectAsState()


        val currentMonth by memoCalendarViewModel.currentMonth.collectAsState()


        CalendarScreen(
            uiState = uiState,
            selectedNotebook = selectedNotebook,
            loadedDates = loadedDates,
            selectedDate = selectedDate,
            currentMonth = currentMonth,
            toScreen = toScreen,
            onFabClicked = {},
            onCalendarIntent = {
                memoCalendarViewModel.onIntent(it)
            }
        )
    }
}