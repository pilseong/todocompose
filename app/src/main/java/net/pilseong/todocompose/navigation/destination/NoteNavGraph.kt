package net.pilseong.todocompose.navigation.destination

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.ui.screen.list.memoDetailComposable
import net.pilseong.todocompose.ui.screen.list.memoListComposable
import net.pilseong.todocompose.util.Constants.MEMO_ROOT

fun NavGraphBuilder.noteNavGraph(
    navHostController: NavHostController,
    toTaskScreen: () -> Unit,
    toListScreen: () -> Unit,
    toNoteScreen: () -> Unit
) {
    navigation(
        startDestination = Screen.Note.route,
        route = MEMO_ROOT,
    ) {

        noteComposable(navHostController)

        memoListComposable(navHostController, toTaskScreen, toNoteScreen)


        memoDetailComposable(navHostController, toListScreen)
    }
}