package net.pilseong.todocompose

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import net.pilseong.todocompose.navigation.MainNavGraph
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.navigation.destination.BottomBarScreen
import net.pilseong.todocompose.ui.screen.home.NoteViewModel
import net.pilseong.todocompose.ui.theme.TodoComposeTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val noteViewModel: NoteViewModel by viewModels()
    private lateinit var navHostController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("PHILIP", "[MainActivity] onCreate called")

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            noteViewModel.isLoading
        }

        setContent {
            TodoComposeTheme {
                navHostController = rememberNavController()

                // 아래 surface는 네비게이션 전환 시 발생하는 cross fade 표과를 제거하기 위해 추가 되었다.
                // dark mode에서는 더 이상 이슈가 생기지 않지만
                // light 모드 에서는 배경색으로 인한 flikering이 여전히 존재
                Surface {
                    MainNavGraph(
                        startDestination = BottomBarScreen.Home.route,
                        navHostController = navHostController
                    )
                }
                handleIntent(intent)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("PHILIP", "[MainActivity]onPause is called")

    }

    override fun onStop() {
        super.onStop()
        Log.d("PHILIP", "[MainActivity]onStop is called")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("PHILIP", "[MainActivity]onRestart is called")
    }


    override fun onResume() {
        super.onResume()
        Log.d("PHILIP", "[MainActivity]onResume is called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("PHILIP", "[MainActivity]onDestory is called")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        when {
            intent?.action == Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                    intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                        Log.d("PHILIP", "intent called $it")
                        navHostController.navigate(Screen.MemoDetail.route)
                    }
                } else if (intent.type?.startsWith("image/") == true) {
    //                    handleSendImage(intent) // Handle single image being sent
                }
            }

            intent?.action == Intent.ACTION_SEND_MULTIPLE
                    && intent.type?.startsWith("image/") == true -> {
    //                handleSendMultipleImages(intent) // Handle multiple images being sent
            }

            else -> {
                // Handle other intents, such as being started from the home screen
            }
        }
    }
}