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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.data.model.TodoTask
import net.pilseong.todocompose.ui.viewmodel.SharedViewModel
import net.pilseong.todocompose.util.Action
import net.pilseong.todocompose.util.TaskAppBarState
import net.pilseong.todocompose.util.copyToClipboard

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TaskScreen(
    sharedViewModel: SharedViewModel,
    toListScreen: (Action) -> Unit,
) {
    // 세부 화면 스크린 에서는 리스트 에서 생성 하고 저장한 snapshot 만 의존 한다.
    // 1. 현재 리스트
    // 2. 해당 인덱스
    // 3. 테스크 top bar 의 상태
    val taskScreenState = sharedViewModel.taskAppBarState
    val index = sharedViewModel.index

    Log.i("PHILIP", "[TaskScreen] index is $index")
    MainScreen(
        taskAppBarState = taskScreenState,
        tasks = sharedViewModel.snapshotTasks,
        taskIndex = index,
        sharedViewModel = sharedViewModel,
        toListScreen = toListScreen
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    taskAppBarState: TaskAppBarState,
    tasks: List<TodoTask>,
    taskIndex: Int,
    sharedViewModel: SharedViewModel,
    toListScreen: (Action) -> Unit,
) {

    Log.i("PHILIP", "[MainScreen] size of tasks ${tasks.size}")
    val context = LocalContext.current

    val selectedTask = sharedViewModel.selectedTask

    // 인덱스 가 변경 되었을 경우 에만 editor 의 값을 초기화 한다.
    LaunchedEffect(key1 = taskIndex) {
        Log.i("PHILIP", "[MainScreen] selectedTask $selectedTask")
        if (taskIndex >= 0)
            sharedViewModel.updateSelectedTask(tasks[taskIndex])
        else
            sharedViewModel.updateSelectedTask(TodoTask(-1, "", "", Priority.NONE))

    }

    // 뒤로 가기 버튼에 대한 가로 채기 및 처리
    BackHandler {
        toListScreen(Action.NO_ACTION)
    }

    val emptyTitleString = stringResource(id = R.string.empty_title_popup)
    val emptyDescriptionString = stringResource(id = R.string.empty_description_popup)
    val scope = rememberCoroutineScope()
    val clipboardMessage = stringResource(id = R.string.viewer_appbar_clipboard_copied_popup)

    Scaffold(
        topBar = {
            TaskAppBar(
                taskAppBarState = taskAppBarState,
                todoTask = selectedTask,
                toListScreen = { action ->
                    // 수정 할 내용을 반영 해야 할 경우 title, description 이 비어 있는지 확인
                    if (action != Action.NO_ACTION) {
                        if (sharedViewModel.title.isEmpty()) {
                            Toast.makeText(
                                context,
                                emptyTitleString,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (sharedViewModel.description.isEmpty()) {
                            Toast.makeText(
                                context,
                                emptyDescriptionString,
                                Toast.LENGTH_SHORT
                            ).show()
                            // the action is executed here
                        } else {
                            sharedViewModel.handleActions(action = action)
                            toListScreen(action)
                        }
                    } else {
                        toListScreen(action)
                    }
                },
                onCopyClicked = {
                    scope.launch {
                        copyToClipboard(
                            context = context,
                            label = "content", text = sharedViewModel.description
                        )
                        Toast.makeText(context, clipboardMessage, Toast.LENGTH_SHORT).show()
                    }
                },
                onUpdateClicked = {
                    sharedViewModel.setTaskScreenToEditorMode()
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(top = paddingValues.calculateTopPadding())
                    .fillMaxSize()
            ) {
                TaskContent(
                    taskIndex = taskIndex,
                    tasks = tasks,
                    taskAppBarState = taskAppBarState,
                    title = sharedViewModel.title,
                    description = sharedViewModel.description,
                    priority = sharedViewModel.priority,
                    createdAt = sharedViewModel.createdAt,
                    updatedAt = sharedViewModel.updatedAt,
                    onTitleChange = { title ->
                        Log.i("PHILIP", "[TaskScreen] title has changed $title")
                        sharedViewModel.updateTitle(title)
                    },
                    onDescriptionChange = { description ->
                        sharedViewModel.description = description
                    },
                    onPriorityChange = { priority ->
                        sharedViewModel.priority = priority
                    },
                    onSwipeRightOnViewer = { sharedViewModel.decrementIndex() },
                    onSwipeLeftOnViewer = { sharedViewModel.incrementIndex() }

                )
            }
        }
    )
}