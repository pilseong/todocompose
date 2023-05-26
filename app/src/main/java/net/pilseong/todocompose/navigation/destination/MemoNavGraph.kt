package net.pilseong.todocompose.navigation.destination

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.ui.screen.list.ListScreen
import net.pilseong.todocompose.ui.screen.task.TaskScreen
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import net.pilseong.todocompose.ui.viewmodel.MemoViewModel
import net.pilseong.todocompose.util.Action
import net.pilseong.todocompose.util.Constants.MEMO_LIST
import net.pilseong.todocompose.util.Constants.NOTE_ID_ARGUMENT

fun NavGraphBuilder.memoNavGraph(
    viewModelStoreOwner: ViewModelStoreOwner,
    toTaskScreen: () -> Unit,
    toListScreen: (Int?) -> Unit,
    onClickBottomNavBar: (String) -> Unit
) {
    navigation(
        startDestination = Screen.MemoList.route,
        route = MEMO_LIST,
    ) {
        composable(
            route = Screen.MemoList.route,
            arguments = listOf(
                navArgument(NOTE_ID_ARGUMENT) {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val memoViewModel = hiltViewModel<MemoViewModel>(
                viewModelStoreOwner = viewModelStoreOwner
            )

            val openDialog = remember { mutableStateOf(false) }


            Log.i(
                "PHILIP",
                "[memoNavGraph] ListScreen called with " +
                        "${backStackEntry.arguments?.getInt(NOTE_ID_ARGUMENT)}"
            )

            if (memoViewModel.firstFetch) {
                Log.i("PHILIP", "[MemoNavGraph] memoViewModel value ${memoViewModel.toString()}")
                memoViewModel.observePrioritySortState()
                memoViewModel.observeOrderEnabledState()
                memoViewModel.observeDateEnabledState()
                memoViewModel.observeFavoriteState()
                memoViewModel.observeNotebookIdChange()
            }


            ListScreen(
                toTaskScreen = { snapshot ->
                    Log.i("PHILIP", "Snapshot is $snapshot")
                    memoViewModel.updateSnapshotTasks(snapshot)
                    // 화면 전환 시에는 action 을 초기화 해야 뒤로 가기 버튼을 눌렀을 때 오동작 을 예방할 수 있다.
                    memoViewModel.updateAction(Action.NO_ACTION)
                    toTaskScreen()
                },
                onClickBottomNavBar = onClickBottomNavBar,
                memoViewModel = memoViewModel,
                onAppBarTitleClick = {
                    memoViewModel.getNotebooks()
                    openDialog.value = true
                }
            )

            NotebooksPickerDialog(
                visible = openDialog.value,
                onDismissRequest = {
                    openDialog.value = false
                },
                notebooks = memoViewModel.notebooks.collectAsState().value,
                onCloseClick = {
                    openDialog.value = false
                },
                onNotebookClick = {
                    Log.i("PHILIP", "[MemoNavGraph] onNotebookClick $it")
                    memoViewModel.handleActions(Action.NOTEBOOK_CHANGE, notebookId = it)
                    openDialog.value = false
                }
            )
        }


        composable(
            route = Screen.MemoDetail.route,
        ) {
            val memoViewModel = hiltViewModel<MemoViewModel>(
                viewModelStoreOwner = viewModelStoreOwner
            )

            TaskScreen(
                memoViewModel = memoViewModel,
                toListScreen = toListScreen
            )
        }
    }
}

@Composable
fun NotebooksPickerDialog(
    visible: Boolean,
    notebooks: List<Notebook>,
    onDismissRequest: () -> Unit,
    onCloseClick: () -> Unit,
    onNotebookClick: (Int) -> Unit
) {
    if (visible) {
        CustomAlertDialog(onDismissRequest = { onDismissRequest() }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surface),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(vertical = LARGE_PADDING)
                        .padding(horizontal = XLARGE_PADDING),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ListAlt,
                        contentDescription = "Choose Notebook"
                    )
                    Spacer(modifier = Modifier.width(SMALL_PADDING))
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = "Choose Notebook",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,

                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Surface(
                    modifier = Modifier
                        .height(300.dp)
                        .padding(XLARGE_PADDING)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(4.dp),
                    tonalElevation = 2.dp
                ) {
                    LazyColumn(
                        // contentPadding은 전체를 감싸는 padding
                        contentPadding = PaddingValues(LARGE_PADDING),
                        verticalArrangement = Arrangement.spacedBy(LARGE_PADDING)
                    ) {
                        items(
                            items = notebooks,
                            key = { notebook ->
                                notebook.id
                            }
                        ) {
                            Surface(
                                modifier = Modifier
                                    .height(56.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        onNotebookClick(it.id)
                                    },
                                shape = RoundedCornerShape(4.dp),
                                color = it.priority.color.copy(alpha = 0.4F),
                                tonalElevation = 6.dp,
//                                shadowElevation = 1.dp
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(start = LARGE_PADDING),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (it.title == "") ""
                                        else if (it.title.length > 20) "${
                                            it.title.substring(
                                                startIndex = 0,
                                                endIndex = 20
                                            )
                                        }..."
                                        else it.title
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(start = LARGE_PADDING, bottom = LARGE_PADDING)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(onClick = {
                        onNotebookClick(-1)
                    }) {
                        Text(text = "Use Default Notebook")
                    }
                    // Close 버튼
                    Text(
                        modifier = Modifier
                            .padding(horizontal = XLARGE_PADDING)
                            .clickable {
                                onCloseClick()
                            }
                            .padding(12.dp),
                        text = "Close",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun NotebooksPickerDialogPreview() {
    MaterialTheme {
        NotebooksPickerDialog(
            visible = true,
            onCloseClick = {},
            onDismissRequest = {},
            notebooks = listOf(
                Notebook(
                    id = 1,
                    title = "My Love Note",
                    description = "desc1",
                    priority = Priority.NONE
                ),
                Notebook(
                    id = 2,
                    title = "first notebooksss",
                    description = "desc2",
                    priority = Priority.NONE
                ),
                Notebook(id = 3, title = "test3", description = "desc3", priority = Priority.NONE)
            ),
            onNotebookClick = {

            }
        )
    }
}