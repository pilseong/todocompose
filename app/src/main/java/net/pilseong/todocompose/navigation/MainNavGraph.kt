package net.pilseong.todocompose.navigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.navigation.destination.BottomBarScreen
import net.pilseong.todocompose.navigation.destination.CreateNotebookDialog
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

    // memoViewModel 을 list 와 task 에서 공유 하기 위하여 owner 를 상위 컴포 넌트 에서 지정함
    // 이렇게 하지 않으면 list 에서 새로운 memoViewModel 생성 된다.
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }
    NavHost(
        navController = navHostController,
        startDestination = startDestination
    ) {
        homeComposable(
            navHostController = navHostController,
            route = BottomBarScreen.Home.route
        )

        memoNavGraph(
            navHostController = navHostController,
            viewModelStoreOwner = viewModelStoreOwner,
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

