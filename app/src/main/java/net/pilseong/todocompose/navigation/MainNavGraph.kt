package net.pilseong.todocompose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import net.pilseong.todocompose.navigation.destination.BottomBarScreen
import net.pilseong.todocompose.navigation.destination.memoNavGraph
import net.pilseong.todocompose.ui.screen.home.HomeScreen
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
        composable(
            route = BottomBarScreen.Home.route
        ) {
            HomeScreen(
                onClickBottomNavBar = { route ->
                    navHostController.navigate(route)
                }
            )
        }

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

