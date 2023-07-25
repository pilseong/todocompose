package net.pilseong.todocompose.navigation.destination

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.ui.screen.calendar.memoCalendarViewComposable
import net.pilseong.todocompose.ui.screen.list.memoDetailComposable
import net.pilseong.todocompose.ui.screen.list.memoListComposable
import net.pilseong.todocompose.ui.screen.taskmanager.memoTaskManagerComposable
import net.pilseong.todocompose.util.Constants.MEMO_ROOT

fun NavGraphBuilder.noteNavGraph(
    navHostController: NavHostController,
//    toTaskScreen: () -> Unit,
//    toListScreen: () -> Unit,
//    toNoteScreen: () -> Unit,
//    toTaskManagementScreen: () -> Unit,
    toScreen: (Screen) -> Unit,
) {
    navigation(
        startDestination = Screen.Notes.route,
        route = MEMO_ROOT,
    ) {

        noteComposable(navHostController)

        memoListComposable(
            navHostController = navHostController,
            toScreen = toScreen,
//            toTaskScreen,
//            toNoteScreen,
//            toTaskManagementScreen,
        )

        memoDetailComposable(navHostController, toScreen)

        memoTaskManagerComposable(
            navHostController = navHostController,
            toScreen = toScreen
        )

        memoCalendarViewComposable(
            navHostController = navHostController,
            toScreen = toScreen
        )
    }
}