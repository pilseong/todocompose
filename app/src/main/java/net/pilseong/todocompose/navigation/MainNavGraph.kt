package net.pilseong.todocompose.navigation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.coroutines.launch
import net.pilseong.todocompose.R
import net.pilseong.todocompose.navigation.destination.BottomBarScreen
import net.pilseong.todocompose.navigation.destination.noteNavGraph
import net.pilseong.todocompose.ui.components.AppDrawer
import net.pilseong.todocompose.ui.components.DisplayAlertDialog
import net.pilseong.todocompose.ui.screen.settings.SettingsScreen
import net.pilseong.todocompose.ui.viewmodel.NoteViewModel
import net.pilseong.todocompose.util.Constants.MAIN_ROOT
import net.pilseong.todocompose.util.Constants.MEMO_ROOT

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

    val noteViewModel = hiltViewModel<NoteViewModel>(viewModelStoreOwner)

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val intentResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) noteViewModel.handleImport(uri)
        }

    // 내 보내기 팝업
    var importAlertExpanded by remember { mutableStateOf(false) }
    DisplayAlertDialog(
        title = stringResource(id = R.string.import_dialog_title),
        message = stringResource(id = R.string.import_dialog_confirmation),
        openDialog = importAlertExpanded,
        onYesClicked = {
            intentResultLauncher.launch("text/plain")
        },
        onCloseDialog = { importAlertExpanded = false }
    )

    // 가 져오기 팝업
    // Export 다이얼 로그 박스 에 대한 상태
    var exportAlertExpanded by remember { mutableStateOf(false) }

    // 모두 삭제 하기의 confirm 용도의 alert dialog 생성
    DisplayAlertDialog(
        title = stringResource(id = R.string.export_memos_dialog_title),
        message = stringResource(id = R.string.export_all_memos_dialog_confirmation),
        openDialog = exportAlertExpanded,
        onYesClicked = {
            noteViewModel.exportData()
        },
        onCloseDialog = { exportAlertExpanded = false }
    )


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                AppDrawer(
                    onImportClicked = {
                        importAlertExpanded = true
                    },
                    onExportClicked = {
                        exportAlertExpanded = true
                    },
                    onScreenSelected = { to ->
                        navHostController.popBackStack(to, true)
                        navHostController.navigate(to)

                        scope.launch {
                            drawerState.close()
                        }
                    })
            }
        }
    ) {
        NavHost(
            navController = navHostController,
            startDestination = startDestination
        ) {
            navigation(
                startDestination = MEMO_ROOT,
                route = MAIN_ROOT,
            ) {
//                homeComposable(
//                    navHostController = navHostController,
//                    viewModelStoreOwner = viewModelStoreOwner,
//                    route = HOME_ROOT
//                )

                noteNavGraph(
                    navHostController = navHostController,
                    viewModelStoreOwner = viewModelStoreOwner,
//                    toTaskScreen = {
//                        navHostController.navigate(Screen.MemoDetail.route)
//                    },
//                    toListScreen = {
//                        navHostController.navigate(Screen.MemoList.route)
//                    },
//                    toNoteScreen = {
//                        navHostController.navigate(Screen.Notes.route)
//                    },
//                    toTaskManagementScreen = {
//                        navHostController.navigate(Screen.MemoTaskManager.route)
//                    },
                    toScreen = {
                        navHostController.navigate(it.route)
                    }
                )

                composable(
                    route = BottomBarScreen.Settings.route
                ) {
                    SettingsScreen(onClickBottomNavBar = { screen ->
                        navHostController.navigate(screen.route)
                    })
                }
            }
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navHostController: NavHostController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
//        Log.d("PHILIP", "sharedViewModel set $navGraphRoute $this")
        navHostController.getBackStackEntry(navGraphRoute)
    }

//    LaunchedEffect(key1 = this) {
//        Log.d("PHILIP", "sharedViewModel after $parentEntry")
//    }

    return hiltViewModel(parentEntry)
}