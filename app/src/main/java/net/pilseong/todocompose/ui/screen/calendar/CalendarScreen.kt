package net.pilseong.todocompose.ui.screen.calendar

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
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
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var dateNotesList by remember(tasks) {
        mutableStateOf(tasks.filter {
            it.memo.dueDate!!.month == selectedDate.month &&
                    it.memo.dueDate.dayOfMonth == selectedDate.dayOfMonth
        })
    }

    Log.d("PHILIP", "[CalendarScreen] selectedMonth $selectedMonth")


    // 아래 처럼 상태를 분리 해서 처리 해야 정상 적으로 동작 한다.
    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Hidden,
        skipHiddenState = false
    )

    val bottomSheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
//            skipPartiallyExpanded = true
//        )
    )

    var sheetStateBeforeEditorPopup by remember {
        mutableStateOf(false)
    }

    Log.d(
        "PHILIP",
        "[CalendarScreen] bottomSheetState ${bottomSheetState.bottomSheetState.currentValue}"
    )
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = bottomSheetState.bottomSheetState.currentValue) {
        if (!bottomSheetState.bottomSheetState.isVisible) {
            onTaskUiStateListClean()
        }
    }


    // UI 처리 부분
    BottomSheetScaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        sheetPeekHeight = 0.dp,
        sheetContent = {
            CalendarNoteList(
                modifier = Modifier.height(700.dp),
                selectedDate = selectedDate,
                taskUiStateList = taskUiStateList,
                notes = dateNotesList,
                onDismissRequest = {
                    scope.launch {
                        bottomSheetState.bottomSheetState.hide()
                    }
                },
                onAddClicked = {
                    sheetStateBeforeEditorPopup = true
                    editorExpanded = true
                    scope.launch {
                        bottomSheetState.bottomSheetState.hide()
                    }
                },
                onEditClicked = onEditClicked,
                onValueChange = onValueChange,
                onDeleteClicked = onDeleteClicked,
            )
        },
        scaffoldState = bottomSheetState,
    ) {
        Scaffold(
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
                } else {
                    EditTaskBar(
                        uiState = taskUiState,
                        mode = EditTaskBarMode.CALENDAR_ADD,
                        onConfirm = {
                            onNewConfirm(CalendarAction.ADD)
                            editorExpanded = false
                        },
                        onBackClick = {
                            onValueChange(
                                TaskDetails(
                                    id = NEW_ITEM_ID,
                                    notebookId = selectedNotebook.id,
                                    dueDate = taskUiState.taskDetails.dueDate!!.toLocalDate()
                                        .plusDays(1)
                                        .atStartOfDay(ZoneId.systemDefault())
                                        .minusMinutes(1)
                                )
                            )
                            editorExpanded = false
                            if (sheetStateBeforeEditorPopup) {
                                scope.launch { bottomSheetState.bottomSheetState.expand() }
                                sheetStateBeforeEditorPopup = false
                            }
                            Log.d(
                                "PHILIP",
                                "sheet status ${bottomSheetState.bottomSheetState.currentValue}"
                            )
//                            scope.launch { bottomSheetState.bottomSheetState.expand() }
                        },
                        clearAddedPhotos = {},
                        onValueChange = onValueChange,
                    )
                }
            },
            bottomBar = {
                Row {
                    BottomActionBarNavigation(
                        currentScreen = Screen.MemoCalendar,
                        onNavigateClick = toScreen,
                        expanded = !editorExpanded,
                        onFabClicked = {
                            // 추가 할 메모의 오염 되었을 경우 에는 신규 내용 으로 초기화 한다.
                            if (taskUiState.taskDetails.id != NEW_ITEM_ID) {
                                onValueChange(
                                    TaskDetails(
                                        id = NEW_ITEM_ID,
                                        notebookId = selectedNotebook.id,
                                        dueDate = taskUiState.taskDetails.dueDate!!.toLocalDate()
                                            .plusDays(1)
                                            .atStartOfDay(ZoneId.systemDefault())
                                            .minusMinutes(1)
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
                        bottom = paddingValues.calculateBottomPadding(),
                    )
                    .fillMaxSize(),
            ) {
                CalendarContent(
                    tasks = tasks,
                    taskUiState = taskUiState,
                    taskUiStateList = taskUiStateList,
                    editorExpanded = editorExpanded,
                    selectedNotebook = selectedNotebook,
                    selectedMonth = selectedMonth,
                    selectedDate = selectedDate,
                    onMonthChange = onMonthChange,
                    onValueChange = onValueChange,
                    onEditorExpanded = { editorExpanded = it },
                    onEditClicked = onEditClicked,
                    onDeleteClicked = onDeleteClicked,
                    onTaskUiStateListClean = onTaskUiStateListClean,
                    onDayClick = { it, notes ->
                        Log.d("PHILIP", "[CalendarScreen] onDayClick $it")
                        onValueChange(
                            TaskDetails().copy(
                                notebookId = selectedNotebook.id,
                                dueDate = it
                                    .plusDays(1)
                                    .atStartOfDay(ZoneId.systemDefault())
                                    .minusMinutes(1)
                            )
                        )
                        dateNotesList = notes
                        selectedDate = it
                    },
                    onDayLongClick = { date, notes ->
                        Log.d("PHILIP", "[CalendarScreen] onDayLongClick ${notes.size}")
                        onValueChange(
                            TaskDetails().copy(
                                notebookId = selectedNotebook.id,
                                dueDate = date
                                    .plusDays(1)
                                    .atStartOfDay(ZoneId.systemDefault())
                                    .minusMinutes(1)
                            )
                        )
                        selectedDate = date
                        dateNotesList = notes
                        scope.launch { bottomSheetState.bottomSheetState.expand() }
                    }
                )
            }
        }
    }

    // 상태 바의 상태가 검색이 열려 있는 경우 뒤로 가기를 하면 기본 상태로 돌아 가게 된다.
    BackHandler(
        enabled = bottomSheetState.bottomSheetState.currentValue == SheetValue.Expanded
    ) {
        scope.launch {
            bottomSheetState.bottomSheetState.hide()
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
    TodoComposeTheme {
        CalendarScreen(
            userData = UserData(),
            taskUiState = TaskUiState(),
            tasks = listOf(),
            selectedMonth = LocalDate.now().yearMonth(),
            selectedNotebook = Notebook.instance(),
            toScreen = {},
            onMonthChange = {},
            onAppBarTitleClick = {},
            onSearchRangeAllClicked = { _, _ -> },
            onValueChange = {},
            onNewConfirm = {},
            onEditClicked = {},
            onTaskUiStateListClean = {},
            onDeleteClicked = {},
        )
    }
}