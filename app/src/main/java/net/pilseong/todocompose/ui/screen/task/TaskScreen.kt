package net.pilseong.todocompose.ui.screen.task

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.TodoTask
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
    val tasks = memoViewModel.snapshotTasks
    Log.i("PHILIP", "[TaskScreen] size of tasks ${tasks.size}")

    val context = LocalContext.current

    // 뒤로 가기 버튼에 대한 가로 채기 및 처리
    BackHandler {
        toListScreen(null)
    }

    val emptyTitleString = stringResource(id = R.string.empty_title_popup)
    val emptyDescriptionString = stringResource(id = R.string.empty_description_popup)
    val clipboardMessage = stringResource(id = R.string.viewer_appbar_clipboard_copied_popup)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TaskAppBar(
                taskAppBarState = taskAppBarState,
                todoTask = if (taskIndex >= 0) tasks[taskIndex] else TodoTask.instance(),
                toListScreen = { action ->
                    // 수정 할 내용을 반영 해야 할 경우 title, description 이 비어 있는지 확인
                    if (action != Action.NO_ACTION) {
                        if (action == Action.DELETE) {
                            memoViewModel.handleActions(action = action)
                            toListScreen(null)
                        } else {
                            if (memoViewModel.title.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    emptyTitleString,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (memoViewModel.description.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    emptyDescriptionString,
                                    Toast.LENGTH_SHORT
                                ).show()
                                // the action is executed here
                            } else {
                                memoViewModel.handleActions(action = action)
                                toListScreen(null)
                            }
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
                            label = "content", text = memoViewModel.description
                        )
                        Toast.makeText(context, clipboardMessage, Toast.LENGTH_SHORT).show()
                    }
                },
                onEditClicked = {
                    memoViewModel.setTaskScreenToEditorMode(tasks[taskIndex])
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
                    taskIndex = taskIndex,
                    tasks = tasks,
                    taskAppBarState = taskAppBarState,
                    title = memoViewModel.title,
                    description = memoViewModel.description,
                    priority = memoViewModel.priority,
                    onTitleChange = { title ->
                        Log.i("PHILIP", "[TaskScreen] title has changed $title")
                        memoViewModel.updateTitle(title)
                    },
                    onDescriptionChange = { description ->
                        memoViewModel.description = description
                    },
                    onPriorityChange = { priority ->
                        memoViewModel.priority = priority
                    },
                    onSwipeRightOnViewer = { memoViewModel.decrementIndex() },
                    onSwipeLeftOnViewer = { memoViewModel.incrementIndex() }

                )
            }
        }
    )
}