package net.pilseong.todocompose

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import net.pilseong.todocompose.navigation.MainNavGraph
import net.pilseong.todocompose.navigation.destination.BottomNavBar
import net.pilseong.todocompose.ui.theme.TodoComposeTheme
import net.pilseong.todocompose.ui.viewmodel.MemoViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var navHostController: NavHostController
    private val memoViewModel: MemoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoComposeTheme {
                navHostController = rememberNavController()
//
                memoViewModel.observePrioritySortState()
                memoViewModel.observeOrderEnabledState()
                memoViewModel.observeDateEnabledState()
                memoViewModel.observeFavoriteState()
//
//                // 시스템 navigation graph 를 가지고 있다.
//                SetupNavigation(
//                    navHostController = navHostController,
//                    sharedViewModel = sharedViewModel
//                )
                MainScreen(navHostController, memoViewModel)
            }
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    navHostController: NavHostController,
    memoViewModel: MemoViewModel
) {
    Scaffold {
        MainNavGraph(navHostController, memoViewModel)
    }
}