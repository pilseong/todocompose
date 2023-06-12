package net.pilseong.todocompose.ui.screen.task

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import net.pilseong.todocompose.R
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import net.pilseong.todocompose.ui.viewmodel.MemoViewModel
import net.pilseong.todocompose.util.Action
import net.pilseong.todocompose.util.copyToClipboard

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TaskScreen(
    memoViewModel: MemoViewModel,
    toListScreen: (Int?) -> Unit,
) {
    // 세부 화면 스크린 에서는 리스트 에서 생성 하고 저장한 snapshot 만 의존 한다.
    // 1. 현재 리스트
    // 2. 해당 인덱스
    // 3. 테스크 top bar 의 상태
    val taskAppBarState = memoViewModel.taskAppBarState
    val taskIndex = memoViewModel.index


    Log.i("PHILIP", "[TaskScreen] index is $taskIndex")
    val tasks = memoViewModel.tasks.collectAsLazyPagingItems()
    Log.i("PHILIP", "[TaskScreen] size of tasks ${tasks.itemCount}")

    val context = LocalContext.current

    val clipboardMessage = stringResource(id = R.string.viewer_appbar_clipboard_copied_popup)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TaskAppBar(
                task = tasks.peek(taskIndex)!!,
                taskAppBarState = taskAppBarState,
                taskUiState = memoViewModel.taskUiState,
                toListScreen = { action ->
                    // 수정 할 내용을 반영 해야 할 경우 title, description 이 비어 있는지 확인
                    if (action != Action.NO_ACTION) {
                        if (action == Action.DELETE) {
                            memoViewModel.handleActions(action = action, todoTask = tasks[taskIndex]!!)
                            toListScreen(null)
                        } else {
                                memoViewModel.handleActions(
                                    action = action
                                )
                                toListScreen(null)
                        }
                    } else {
                        toListScreen(null)
                        memoViewModel.refreshAllTasks()
                    }
                },
                onCopyClicked = {
                    scope.launch {
                        copyToClipboard(
                            context = context,
                            label = "content", text = tasks[taskIndex]!!.description
                        )
                        Toast.makeText(context, clipboardMessage, Toast.LENGTH_SHORT).show()
                    }
                },
                onEditClicked = {
                    memoViewModel.setTaskScreenToEditorMode(tasks.peek(taskIndex)!!)
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(
                        top = paddingValues.calculateTopPadding() + LARGE_PADDING,
                        start = XLARGE_PADDING,
                        end = XLARGE_PADDING,
                        bottom = SMALL_PADDING
                    )
                    .fillMaxSize()
            ) {
                TaskContent(
                    task = tasks.peek(taskIndex)!!,
                    taskUiState = memoViewModel.taskUiState,
                    taskSize = tasks.itemCount,
                    taskIndex = taskIndex,
                    taskAppBarState = taskAppBarState,
                    onValueChange = memoViewModel::updateUiState,
                    onSwipeRightOnViewer = { memoViewModel.decrementIndex() },
                    onSwipeLeftOnViewer = { memoViewModel.incrementIndex() }

                )
            }
        }
    )
}