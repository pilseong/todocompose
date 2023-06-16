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
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.TodoTask
import net.pilseong.todocompose.data.model.UserData
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import net.pilseong.todocompose.ui.viewmodel.MemoViewModel
import net.pilseong.todocompose.ui.viewmodel.TaskUiState
import net.pilseong.todocompose.util.Action
import net.pilseong.todocompose.util.TaskAppBarState
import net.pilseong.todocompose.util.copyToClipboard

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TaskScreen(
    tasks: LazyPagingItems<TodoTask>,
    taskIndex: Int,
    taskAppBarState: TaskAppBarState,
    taskUiState: TaskUiState,
    memoViewModel: MemoViewModel,
    toListScreen: (Int?) -> Unit,
) {

    val context = LocalContext.current

    val clipboardMessage = stringResource(id = R.string.viewer_appbar_clipboard_copied_popup)
    val scope = rememberCoroutineScope()

    Log.i("PHILIP", "[TaskScreen] index is $taskIndex")

    Scaffold(
        topBar = {
            TaskAppBar(
                task = if (taskIndex >= 0) tasks.peek(taskIndex)!! else TodoTask.instance(),
                taskAppBarState = taskAppBarState,
                taskUiState = taskUiState,
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
                    task = if (taskIndex >= 0) tasks.peek(taskIndex)!! else TodoTask.instance(),
                    taskUiState = taskUiState,
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