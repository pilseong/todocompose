package net.pilseong.todocompose.ui.screen.calendar

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.data.model.ui.UserData
import net.pilseong.todocompose.navigation.Screen
import net.pilseong.todocompose.ui.components.BottomActionBarNavigation
import net.pilseong.todocompose.ui.screen.task.EditTaskBar
import net.pilseong.todocompose.ui.screen.task.EditTaskBarMode
import net.pilseong.todocompose.ui.theme.TodoComposeTheme
import net.pilseong.todocompose.ui.viewmodel.TaskDetails
import net.pilseong.todocompose.ui.viewmodel.TaskUiState
import net.pilseong.todocompose.util.Constants.NEW_ITEM_ID
import net.pilseong.todocompose.util.yearMonth
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CalendarScreen(
    userData: UserData,
    taskUiState: TaskUiState,
    taskUiStateList: SnapshotStateList<TaskUiState> = SnapshotStateList(),
    selectedMonth: YearMonth,
    tasks: List<MemoWithNotebook>,
    selectedNotebook: Notebook,
    toScreen: (Screen) -> Unit,
    onMonthChange: (YearMonth) -> Unit,
    onValueChange: (TaskDetails) -> Unit,
    onAppBarTitleClick: () -> Unit,
    onSearchRangeAllClicked: (Boolean, Boolean) -> Unit,
    onNewConfirm: (CalendarAction) -> Unit,
    onEditClicked: (MemoWithNotebook) -> Unit,
    onDeleteClicked: (MemoWithNotebook) -> Unit,
    onTaskUiStateListClean: () -> Unit,
) {
    // multi select 가 된 경우는 헤더를 고정 한다.
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState(
            initialContentOffset = 0F,
            initialHeightOffset = 0F,
            initialHeightOffsetLimit = 0F
        )
    )

    var editorExpanded by remember { mutableStateOf(false) }

    Log.d("PHILIP", "[CalendarScreen] selectedMonth $selectedMonth")


    /**
     * view model 을 통제 코드 종료
     */

    // UI 처리 부분
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (!editorExpanded) {
                TopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            modifier = Modifier
                                .clickable {
                                    if (!userData.searchRangeAll)
                                        onAppBarTitleClick()
                                },
                            text = if (userData.searchRangeAll) stringResource(id = R.string.badge_search_range_all_label) else selectedNotebook.title,
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
            } else {
                EditTaskBar(
                    uiState = taskUiState,
                    mode = EditTaskBarMode.CALENDAR_ADD,
                    onConfirm = {
                        onNewConfirm(CalendarAction.ADD)
                        editorExpanded = false
                    },
                    onBackClick = {
                        val selectedDate =
                            taskUiState.taskDetails.dueDate!!.toLocalDate().atStartOfDay(
                                ZoneId.systemDefault()
                            )

                        onValueChange(
                            TaskDetails(
                                id = NEW_ITEM_ID,
                                notebookId = selectedNotebook.id,
                                dueDate = selectedDate
                            )
                        )
                        editorExpanded = false
                    },
                    clearAddedPhotos = {},
                    onValueChange = onValueChange,
                )
            }
        },
        bottomBar = {
            if (!editorExpanded) {
                BottomActionBarNavigation(
                    currentScreen = Screen.MemoCalendar,
                    onNavigateClick = toScreen,
                    onFabClicked = {
                        if (taskUiState.taskDetails.id != NEW_ITEM_ID) {
                            val selectedDate =
                                taskUiState.taskDetails.dueDate!!.toLocalDate().atStartOfDay(
                                    ZoneId.systemDefault()
                                )

                            onValueChange(
                                TaskDetails(
                                    id = NEW_ITEM_ID,
                                    notebookId = selectedNotebook.id,
                                    dueDate = selectedDate
                                )
                            )
                        }
                        editorExpanded = true
                    },
                )
            }
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
            Column {
                CalendarContent(
                    tasks = tasks,
                    taskUiState = taskUiState,
                    taskUiStateList = taskUiStateList,
                    editorExpanded = editorExpanded,
                    selectedNotebook = selectedNotebook,
                    selectedMonth = selectedMonth,
                    onMonthChange = onMonthChange,
                    onValueChange = onValueChange,
                    onEditorExpanded = { editorExpanded = it },
                    onEditClicked = onEditClicked,
                    onDeleteClicked = onDeleteClicked,
                    onTaskUiStateListClean = onTaskUiStateListClean,
                )
            }

        }
    }
}


@Preview
@Composable
fun BottomActionBarNavPreview() {
    MaterialTheme {
        BottomActionBarNavigation(
            currentScreen = Screen.MemoCalendar,
            onNavigateClick = {},
            onFabClicked = {},
        )
    }
}


@Composable
@Preview
private fun CalendarScreenPreview() {
    TodoComposeTheme() {
        CalendarScreen(
            userData = UserData(),
            taskUiState = TaskUiState(),
            tasks = listOf(),
            selectedMonth = LocalDate.now().yearMonth(),
            selectedNotebook = Notebook.instance(),
            toScreen = {},
            onMonthChange = {},
            onAppBarTitleClick = {},
            onSearchRangeAllClicked = { _, _ -> Unit },
            onValueChange = {},
            onNewConfirm = {},
            onEditClicked = {},
            onTaskUiStateListClean = {},
            onDeleteClicked = {},
        )
    }
}