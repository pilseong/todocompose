package net.pilseong.todocompose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import net.pilseong.todocompose.navigation.destination.BottomBarScreen
import net.pilseong.todocompose.navigation.destination.homeComposable
import net.pilseong.todocompose.navigation.destination.memoNavGraph
import net.pilseong.todocompose.ui.screen.home.HomeScreen
import net.pilseong.todocompose.ui.screen.home.NoteViewModel
import net.pilseong.todocompose.ui.screen.settings.SettingsScreen

@Composable
fun MainNavGraph(
    startDestination: String,
    navHostController: NavHostController,
) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination
    ) {
        homeComposable(
            navHostController = navHostController,
            route = BottomBarScreen.Home.route
        )
//        composable(
//            route = BottomBarScreen.Home.route
//        ) {
//            val noteViewModel = hiltViewModel<NoteViewModel>()
//
//            LaunchedEffect(key1 = Unit) {
//                noteViewModel.getNotebooks()
//            }
//
//            HomeScreen(
//                onClickBottomNavBar = { route ->
//                    navHostController.navigate(route)
//                },
//                notebooks = noteViewModel.notebooks.collectAsState().value
//            )
//        }

        memoNavGraph(
            navHostController = navHostController,
            toTaskScreen = {
                navHostController.navigate(Screen.MemoDetail.route)
            },
            toListScreen = {
                navHostController.navigate(Screen.MemoList.route)
            },
            onClickBottomNavBar = { route ->
                navHostController.navigate(route)
            }
        )

        composable(
            route = BottomBarScreen.Settings.route
        ) {
            SettingsScreen(onClickBottomNavBar = { route ->
                navHostController.navigate(route)
            })
        }
    }
}

