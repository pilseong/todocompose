package net.pilseong.todocompose.ui.screen.list

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.core.net.toUri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.paging.compose.collectAsLazyPagingItems
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.navigation.sharedViewModel
import net.pilseong.todocompose.ui.screen.task.TaskScreen
import net.pilseong.todocompose.ui.viewmodel.MemoViewModel
import net.pilseong.todocompose.ui.viewmodel.toMemoTask
import net.pilseong.todocompose.util.Constants
import net.pilseong.todocompose.util.deleteFileFromUri

fun NavGraphBuilder.memoDetailComposable(
    navHostController: NavHostController,
    toScreen: (Screen) -> Unit,
) {
    composable(
        route = Screen.MemoDetail.route,
        arguments = listOf(
            navArgument(Constants.MEMO_ID_ARGUMENT) {
                type = NavType.IntType
                defaultValue = 0
            },
            navArgument("content") {
                type = NavType.StringType
                defaultValue = ""
            }
        )
    ) { navBackStackEntry ->
        val memoViewModel = navBackStackEntry.sharedViewModel<MemoViewModel>(navHostController)
        Log.d("PHILIP", "[memoDetailComposable] $memoViewModel")

        Log.d(
            "PHILIP",
            "[memoNavGraph] TaskScreen called id: ${
                navBackStackEntry.arguments?.getInt(
                    Constants.MEMO_ID_ARGUMENT
                )
            }, content: ${navBackStackEntry.arguments?.getString("content")}"
        )

        // 세부 화면 스크린 에서는 리스트 에서 생성 하고 저장한 snapshot 만 의존 한다.
        // 1. 현재 리스트
        // 2. 테스크 top bar 의 상태
        // 3. 해당 인덱스
        val tasks = memoViewModel.tasks.collectAsLazyPagingItems()
        Log.d("PHILIP", "[MemoNavGraph] taskScreen  size of tasks ${tasks.itemCount}")
        val taskAppBarState = memoViewModel.taskAppBarState
        val taskIndex = memoViewModel.index
        Log.d("PHILIP", "[MemoNavGraph] taskScreen index is $taskIndex")

        val notebooks = memoViewModel.notebooks.collectAsState().value


        // 뒤로가기 버튼을 눌렀을 대에도 임시 저장된 이미지를 삭제 해야 한다.
        BackHandler(memoViewModel.taskUiState.taskDetails.photos.isNotEmpty()) {
            memoViewModel.taskUiState.taskDetails.photos.filter { photo ->  photo.id == 0L}
                .forEach { photo ->
                    deleteFileFromUri(photo.uri.toUri())
                }
            Log.d("PHILIP", "[MemoNavGraph] BackHandler performed")
            navHostController.popBackStack()
        }

        // intent 로 전달 받은 데이터 를 화면에 보여 주기 위한 로직
        if (navBackStackEntry.arguments?.getInt(Constants.MEMO_ID_ARGUMENT) == -1) {

            // intent 로 넘긴 데이터 를 받는다
            val detailArgument =
                navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("content")
            LaunchedEffect(key1 = navBackStackEntry.arguments?.getInt(Constants.MEMO_ID_ARGUMENT)) {
                memoViewModel.updateIndex(Constants.NEW_ITEM_INDEX)
                memoViewModel.setTaskScreenToEditorMode()
                memoViewModel.updateUiState(
                    memoViewModel.taskUiState.taskDetails.copy(
                        description = detailArgument ?: ""
                    )
                )
            }
        }

        if (taskIndex >= tasks.itemCount) {
            // 1. activity  destroy 되고 다시 생성된 경우는 List 화면 으로 forwarding - 샤오미 종특
            // 이 경우는 Memo List, home 이 직전의 destination 이다. list 나 home 으로 돌아 가야 한다.
            // 2. 마지막 메모를 삭제할 경우 에도 index 가 0 tasks 가 0인 경우가 발생 한다. 이 경우는 실행이 MemoDetail 에서 오기 때문에 구별 가능
            // 이 경우는 무시 한다.
            // 3. 링크를 share 하여 접근 하는 경우가 있다. 이런 경우는 argument 의 memo_id 가 -1 로 지정되어 있다. 이전 route는 home 이 된다.
            // 이 경우는 이전 화면 으로 이동 해서는 안된다.
            Log.d("PHILIP", "${navHostController.previousBackStackEntry?.destination?.route}")
            if (navHostController.previousBackStackEntry?.destination?.route != Screen.MemoDetail.route &&
                navBackStackEntry.arguments?.getInt(Constants.MEMO_ID_ARGUMENT) != -1
            ) {
                Log.d("PHILIP", "[MemoNavGraph] previous value $route")
                // 다른 action 을 처리 하고 있는 경우는 action 이 처리 되고 있는 정상적인 경우이다.
                if (memoViewModel.memoAction == MemoAction.NO_ACTION) {
                    LaunchedEffect(key1 = taskIndex, key2 = tasks.itemCount) {
                        Log.d(
                            "PHILIP",
                            "[MemoNavGraph] memoViewModel value ${memoViewModel.memoAction}"
                        )
                        toScreen(Screen.MemoList)
                    }
                }
            }
        } else {
            TaskScreen(
                tasks = tasks,
                notebooks = notebooks,
                notebook = memoViewModel.selectedNotebook.value,
                taskIndex = taskIndex,
                taskAppBarState = taskAppBarState,
                taskUiState = memoViewModel.taskUiState,
                toListScreen = { action ->
                    // 수정 할 내용을 반영 해야 할 경우 title, description 이 비어 있는지 확인
                    if (action != MemoAction.NO_ACTION) {
                        when (action) {
                            MemoAction.DELETE -> {
                                memoViewModel.handleActions(
                                    memoAction = action,
                                    memo = tasks[taskIndex]!!.toMemoTask()
                                )
                            }
                            MemoAction.UPDATE -> {
                                memoViewModel.handleActions(
                                    memoAction = action,
                                    memoWithNotebook = tasks[taskIndex]!!
                                )
                            }
                            else -> {
                                memoViewModel.handleActions(
                                    memoAction = action
                                )
                            }
                        }
                    } else {
                        memoViewModel.handleActions(MemoAction.NO_ACTION)
                    }
                    toScreen(Screen.MemoList)
                    // 화면 전환 후에 이전의 index가 lazyloading 범위를 넘어갈 경우 처리를 위해 초기화 필요
                    // 45번 task인데 refresh 이후에 기본 default size 만큼만 로딩 되기 때문이다.
                    // 화면이 list로 전환된 이후에도 몇 번이 호출이 일어난다.
                    memoViewModel.index = 0
                },
                onEditClicked = {
                    memoViewModel.setTaskScreenToEditorMode(tasks[taskIndex]!!)
                },
                onValueChange = memoViewModel::updateUiState,
                onSwipeRightOnViewer = { memoViewModel.decrementIndex() },
                onSwipeLeftOnViewer = { memoViewModel.incrementIndex() },
                onBackClick = {
                    navHostController.popBackStack()
                },
            )
        }

    }
}

