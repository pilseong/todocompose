package net.pilseong.todocompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import net.pilseong.todocompose.navigation.MainNavGraph
import net.pilseong.todocompose.navigation.destination.BottomBarScreen
import net.pilseong.todocompose.ui.theme.TodoComposeTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            TodoComposeTheme {
                val navHostController = rememberNavController()
                // 아래 surface는 네비게이션 전환 시 발생하는 cross fade 표과를 제거하기 위해 추가 되었다.
                // dark mode에서는 더 이상 이슈가 생기지 않지만
                // light 모드 에서는 배경색으로 인한 flikering이 여전히 존재
                Surface {
                    Log.i("PHILIP", "MainActivity")
                    MainNavGraph(
                        startDestination = BottomBarScreen.Home.route,
                        navHostController = navHostController
                    )
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.i("PHILIP", "[MainActivity]onPause is called")

    }

    override fun onStop() {
        super.onStop()
        Log.i("PHILIP", "[MainActivity]onStop is called")
    }

    override fun onRestart() {
        super.onRestart()
        Log.i("PHILIP", "[MainActivity]onRestart is called")
    }


    override fun onResume() {
        super.onResume()
        Log.i("PHILIP", "[MainActivity]onResume is called")
    }
}