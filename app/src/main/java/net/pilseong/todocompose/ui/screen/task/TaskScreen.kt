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
import androidx.core.net.toUri
import androidx.paging.compose.LazyPagingItems
import kotlinx.coroutines.launch
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.MemoTask
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.data.model.ui.NotebookWithCount
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import net.pilseong.todocompose.ui.viewmodel.TaskDetails
import net.pilseong.todocompose.ui.viewmodel.TaskUiState
import net.pilseong.todocompose.util.Action
import net.pilseong.todocompose.util.TaskAppBarState
import net.pilseong.todocompose.util.copyToClipboard
import net.pilseong.todocompose.util.deleteFileFromUri

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TaskScreen(
    tasks: LazyPagingItems<MemoWithNotebook>,
    notebooks: List<NotebookWithCount>,
    notebook: Notebook,
    taskIndex: Int,
    taskAppBarState: TaskAppBarState,
    taskUiState: TaskUiState,
    onBackClick: () -> Unit,
    toListScreen: (Action) -> Unit,
    onEditClicked: () -> Unit,
    onValueChange: (TaskDetails) -> Unit,
    onSwipeRightOnViewer: () -> Unit,
    onSwipeLeftOnViewer: () -> Unit,
) {

    val context = LocalContext.current

    val clipboardMessage = stringResource(id = R.string.viewer_appbar_clipboard_copied_popup)
    val scope = rememberCoroutineScope()

    Log.d(
        "PHILIP",
        "[TaskScreen] index is $taskIndex size is ${tasks.itemCount} ${tasks.itemSnapshotList.size}"
    )

    Scaffold(
        topBar = {
            TaskAppBar(
                task = if (taskIndex >= 0) tasks[taskIndex]!!
                else MemoWithNotebook(
                    memo = MemoTask.instance(),
                    notebook = Notebook.instance(),
                    total = 1,
                    photos = emptyList(),

                ),
                taskAppBarState = taskAppBarState,
                taskUiState = taskUiState,
                toListScreen = toListScreen,
                onCopyClicked = {
                    scope.launch {
                        copyToClipboard(
                            context = context,
                            label = "content", text = tasks[taskIndex]!!.memo.description
                        )
                        Toast.makeText(context, clipboardMessage, Toast.LENGTH_SHORT).show()
                    }
                },
                onEditClicked = onEditClicked,
                onBackClick = onBackClick,
                clearAddedPhotos = {
                    taskUiState.taskDetails.photos.filter { photo ->  photo.id == 0L}
                        .forEach { photo ->
                            deleteFileFromUri(photo.uri.toUri())
                        }
                },
                onValueChange = onValueChange,
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(
                        top = paddingValues.calculateTopPadding() + SMALL_PADDING,
                        start = LARGE_PADDING,
                        end = LARGE_PADDING,
                        bottom = SMALL_PADDING
                    )
                    .fillMaxSize()
            ) {
                TaskContent(
                    task = if (taskIndex >= 0) tasks[taskIndex]!!
                    else MemoWithNotebook(
                        memo = MemoTask.instance(),
                        notebook = Notebook.instance(),
                        total = 1,
                        photos = emptyList(),
                    ),
                    notebooks = notebooks,
                    notebook = notebook,
                    taskUiState = taskUiState,
                    taskSize = tasks.itemCount,
                    taskIndex = taskIndex,
                    taskAppBarState = taskAppBarState,
                    onValueChange = onValueChange,
                    onSwipeRightOnViewer = onSwipeRightOnViewer,
                    onSwipeLeftOnViewer = onSwipeLeftOnViewer,
                )
            }
        }
    )
}