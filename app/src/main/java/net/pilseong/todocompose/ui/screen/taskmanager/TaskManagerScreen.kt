package net.pilseong.todocompose.ui.screen.taskmanager

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.flowOf
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.data.model.ui.UserData
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.ui.components.BottomActionBarNavigation
import net.pilseong.todocompose.ui.screen.list.ListContent
import net.pilseong.todocompose.ui.screen.list.ListView
import net.pilseong.todocompose.ui.theme.TodoComposeTheme
import net.pilseong.todocompose.ui.theme.fabContainerColor
import net.pilseong.todocompose.ui.theme.fabContent
import net.pilseong.todocompose.ui.viewmodel.toMemoTask

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TaskManagerScreen(
    tasks: LazyPagingItems<MemoWithNotebook>,
    userData: UserData,
    selectedNotebook: Notebook,
    toScreen: (Screen) -> Unit,
    onAppBarTitleClick: () -> Unit,
    onSearchRangeAllClicked: (Boolean, Boolean) -> Unit,
    onFabClicked: () -> Unit,
    toTaskScreen: (Int) -> Unit,
    onSwipeToEdit: (Int, MemoWithNotebook) -> Unit,
) {
    // multi select 가 된 경우는 헤더를 고정 한다.
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState(
            initialContentOffset = 0F,
            initialHeightOffset = 0F,
            initialHeightOffsetLimit = 0F
        )
    )

    Log.d("PHILIP", "[TaskManagerScreen] tasks ${tasks.itemCount}")

    /**
     * view model 을 통제 코드 종료
     */

    // UI 처리 부분
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        modifier = Modifier
                            .clickable {
                                if (!userData.searchRangeAll)
                                    onAppBarTitleClick()
                            },
                        text = if (userData.searchRangeAll)
                            stringResource(id = R.string.badge_search_range_all_label)
                        else selectedNotebook.title,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = selectedNotebook.priority.color.copy(alpha = 0.5F)
                ),
                actions = {
                    Switch(
                        checked = userData.searchRangeAll,
                        thumbContent = {
                            if (!userData.searchRangeAll) {
                                Text(text = "All")
                            }
                        },
                        onCheckedChange = {
                            onSearchRangeAllClicked(!userData.searchRangeAll, false)
                        }
                    )
                }
            )
        },
        bottomBar = {
            BottomActionBarNavigation(
                currentScreen = Screen.MemoTaskManager,
                onNavigateClick = toScreen,
                onFabClicked = onFabClicked,
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                )
                .fillMaxSize(),
        ) {
            var state by remember { mutableIntStateOf(0) }
            val titles = listOf("Imminent Tasks", "State View")
            Column {
                TabRow(selectedTabIndex = state) {
                    titles.forEachIndexed { index, title ->
                        Tab(
                            selected = state == index,
                            onClick = { state = index },
                            text = {
                                Text(
                                    text = title,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        )
                    }
                }
                if (state == 0) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                    ) {
                        ListContent(
                            tasks = tasks,
                            toTaskScreen = toTaskScreen,
                            onSwipeToEdit = onSwipeToEdit,
                            header = true,
                            memoDateBaseOption = userData.memoDateSortingState,
                        )
                    }
                } else {
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = "Text tab ${state + 1} selected",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun BottomActionBarNavPreview() {
    MaterialTheme {
        BottomActionBarNavigation(
            currentScreen = Screen.MemoTaskManager,
            onNavigateClick = {},
            onFabClicked = {},
        )
    }
}


// Floating Action Button
@Composable
fun AddMemoFab(
    paddingEnd: Dp = 0.dp,
    size: Dp = 56.dp,
    onFabClicked: () -> Unit,
    icon: ImageVector
) {
    FloatingActionButton(
        modifier = Modifier.size(size),
        onClick = {
            onFabClicked()
        },
        shape = RoundedCornerShape(paddingEnd),
        containerColor = MaterialTheme.colorScheme.fabContainerColor,
        contentColor = MaterialTheme.colorScheme.fabContent
    ) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(id = R.string.add_button_icon),
        )
    }
}

@Preview
@Composable
fun PreviewAddMenuFab() {
    MaterialTheme {
        AddMemoFab(onFabClicked = { /*TODO*/ }, icon = Icons.Default.Create)
    }
}

@Composable
@Preview
private fun ListScreenPreview() {
    TodoComposeTheme {
        TaskManagerScreen(
            tasks = flowOf(
                PagingData.from<MemoWithNotebook>(
                    listOf(
//                    MemoTask(
//                        1,
//                        "필성 힘내!!!",
//                        "할 수 있어. 다 와 간다. 힘내자 다 할 수 있어 잘 될 거야",
//                        Priority.HIGH,
//                        notebookId = -1
//                    )
                    )
                )
            ).collectAsLazyPagingItems(),
            userData = UserData(),
            selectedNotebook = Notebook.instance(),
            toScreen = {},
            onAppBarTitleClick = {},
            onSearchRangeAllClicked = { _, _ -> },
            onFabClicked = {},
            toTaskScreen = {},
            onSwipeToEdit = {_, _ -> },
        )
    }
}