package net.pilseong.todocompose.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import net.pilseong.todocompose.navigation.destination.listComposable
import net.pilseong.todocompose.navigation.destination.taskComposable
import net.pilseong.todocompose.ui.viewmodel.SharedViewModel
import net.pilseong.todocompose.util.Constants.LIST_SCREEN

@Composable
fun SetupNavigation(
    navHostController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    // screens 객체가 한 번만 생성 되도록 안전망 을 두는 것으로 보인다.
    val router = remember(navHostController) {
        Log.i("PHILIP", "Screens has been created")
        Router(navHostController, sharedViewModel)
    }

    NavHost(
        navController = navHostController,
        startDestination = LIST_SCREEN,
    ) {
        listComposable(router.toTaskScreen, sharedViewModel)
        taskComposable(router.toListScreen, sharedViewModel)
    }
}