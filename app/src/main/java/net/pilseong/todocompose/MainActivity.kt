package net.pilseong.todocompose

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.text.htmlEncode
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.pilseong.todocompose.navigation.MainNavGraph
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.ui.screen.home.NoteViewModel
import net.pilseong.todocompose.ui.theme.TodoComposeTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val noteViewModel: NoteViewModel by viewModels()
    private lateinit var navHostController: NavHostController

    private val channel: Channel<(Intent) -> Unit> = Channel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("PHILIP", "[MainActivity] onCreate called")
        lifecycleScope.launch {
            channel.send { handleIntent(intent) }
        }

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            noteViewModel.isLoading
        }

        setContent {
            TodoComposeTheme {
                navHostController = rememberNavController()
                LaunchedEffect(key1 = Unit) {
                    channel.receiveAsFlow().collectLatest {
                        Log.d("PHILIP", "[MainActivity] inside channel $it")
                        it.invoke(intent)
                    }
                }

                // 아래 surface는 네비게이션 전환 시 발생하는 cross fade 표과를 제거하기 위해 추가 되었다.
                // dark mode에서는 더 이상 이슈가 생기지 않지만
                // light 모드 에서는 배경색으로 인한 flikering이 여전히 존재
                Surface {
                    MainNavGraph(
                        startDestination = "root",
                        navHostController = navHostController
                    )
                }
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
        Log.d("PHILIP", "[MainActivity]onDestroy is called")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        lifecycleScope.launch {
            channel.send { handleIntent(intent) }
        }
    }

    private fun handleIntent(intent: Intent?) {
        Log.d("PHILIP", "[MainActivity]handleIntent is called $intent")
        when {
            intent?.action == Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                    intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                        Log.d(
                            "PHILIP",
                            "[MainActivity]handleIntent intent called ${it.htmlEncode()}"
                        )
                        navHostController.currentBackStackEntry?.savedStateHandle?.apply {
                            set("data", it)
                        }
                        navHostController.navigate(Screen.MemoDetail.passId(memoId = -1))
                    }
                } else {// if (intent.type?.startsWith("image/") == true) {
                    // TODO: handleSendImage(intent) // Handle single image being sent
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