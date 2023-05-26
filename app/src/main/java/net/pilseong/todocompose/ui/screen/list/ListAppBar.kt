package net.pilseong.todocompose.ui.screen.list

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import net.pilseong.todocompose.R
import net.pilseong.todocompose.ui.components.DisplayAlertDialog
import net.pilseong.todocompose.ui.components.SimpleDatePickerDialog
import net.pilseong.todocompose.ui.screen.task.CommonAction
import net.pilseong.todocompose.ui.theme.ALPHA_FOCUSED
import net.pilseong.todocompose.ui.theme.ALPHA_NOT_FOCUSED
import net.pilseong.todocompose.ui.theme.TOP_BAR_HEIGHT
import net.pilseong.todocompose.ui.theme.topBarContainerColor
import net.pilseong.todocompose.ui.theme.topBarContentColor
import net.pilseong.todocompose.ui.viewmodel.MemoViewModel
import net.pilseong.todocompose.util.Action
import net.pilseong.todocompose.util.SearchAppBarState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    appbarTitle: String,
    memoViewModel: MemoViewModel,
    searchAppBarState: SearchAppBarState,
    searchText: String,
    onImportClick: () -> Unit,
    onAppBarTitleClick: () -> Unit,
    selectedItemsCount: Int,
    onDeleteSelectedClicked: () -> Unit
) {
    Log.i("PHILIP", "selectedItems $selectedItemsCount")
    when (searchAppBarState) {
        SearchAppBarState.CLOSE -> {
            if (selectedItemsCount == 0) {
                DefaultListAppBar(
                    scrollBehavior = scrollBehavior,
                    appbarTitle = appbarTitle,
                    onSearchIconClicked = {
                        // 초기 로딩 을 위한 데이터 검색
                        memoViewModel.refreshAllTasks()
                        memoViewModel.searchAppBarState.value = SearchAppBarState.OPEN
                    },
                    onDeleteAllClicked = {
                        Log.i("PHILIP", "onDeleteAllClicked")
                        memoViewModel.handleActions(Action.DELETE_ALL)
                    },
                    onDatePickConfirmed = { start, end ->
                        memoViewModel.handleActions(
                            action = Action.SEARCH_WITH_DATE_RANGE,
                            startDate = start,
                            endDate = end
                        )
                    },
                    onImportClick = {
                        onImportClick()
                    },
                    onExportClick = {
                        memoViewModel.exportData()
                    },
                    onAppBarTitleClick = onAppBarTitleClick
                )
            } else {
                MultiSelectAppbar(
                    scrollBehavior = scrollBehavior,
                    selectedItemsCount = selectedItemsCount,
                    onDeleteSelectedClicked = onDeleteSelectedClicked
                )
            }
        }

        else -> {
            SearchAppBar(
                text = searchText,
                onCloseClicked = {
                    memoViewModel.onCloseSearchBar()
                },
                onSearchClicked = {
                    memoViewModel.searchAppBarState.value = SearchAppBarState.TRIGGERED
                    memoViewModel.refreshAllTasks()
                },
                onTextChange = { text ->
                    memoViewModel.searchAppBarState.value = SearchAppBarState.TRIGGERED
                    memoViewModel.searchTextString = text
                    memoViewModel.refreshAllTasks()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTextApi::class)
@Composable
fun DefaultListAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    appbarTitle: String = "Default",
    onSearchIconClicked: () -> Unit,
    onDeleteAllClicked: () -> Unit,
    onDatePickConfirmed: (Long?, Long?) -> Unit,
    onImportClick: () -> Unit,
    onExportClick: () -> Unit,
    onAppBarTitleClick: () -> Unit,
) {
    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onAppBarTitleClick()
                    },
            ) {

                val textMeasurer = rememberTextMeasurer()

                var textSize: IntSize
                var boxWidth = LocalDensity.current.run { maxWidth.toPx() }.toInt()
                val localStyle = LocalTextStyle.current

                // 글자를 하나씩 더해 가면서 좌우 크기를 계산 하여 전체 박스에 들어갈 수 있는지 판단
                // title 과 박스 크기가 변하지 않는 이상 계산 하지 않는다.
                var index = remember(appbarTitle, boxWidth) {
                    var i = 0
                    while (i < appbarTitle.length) {
                        val textLayoutResult =
                            textMeasurer.measure(
                                text = AnnotatedString(appbarTitle.substring(0, i)),
                                style = localStyle
                            )
                        textSize = textLayoutResult.size
                        if (boxWidth < textSize.width) break
                        i++
                    }
                    i
                }

                Text(
                    text = if (appbarTitle.length == index) appbarTitle
                    else appbarTitle.substring(startIndex = 0, endIndex = index - 3) + "...",
                    color = MaterialTheme.colorScheme.topBarContentColor
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.topBarContainerColor
        ),
        actions = {
            ListAppBarActions(
                onSearchClicked = onSearchIconClicked,
                onDeleteAllClicked = onDeleteAllClicked,
                onDatePickConfirmed = onDatePickConfirmed,
                onImportClick = onImportClick,
                onExportClick = onExportClick
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewDefaultListAppBar() {
    DefaultListAppBar(
        scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
        onSearchIconClicked = { /*TODO*/ },
        onDeleteAllClicked = { /*TODO*/ },
        onDatePickConfirmed = { start, end ->
        },
        onImportClick = { /*TODO*/ },
        onExportClick = { /*TODO*/ }) {

    }
}

@Composable
fun ListAppBarActions(
    onSearchClicked: () -> Unit,
    onDeleteAllClicked: () -> Unit,
    onDatePickConfirmed: (Long?, Long?) -> Unit,
    onImportClick: () -> Unit,
    onExportClick: () -> Unit
) {
    // 다이얼 로그 박스 에 대한 상태
    var deleteAlertExpanded by remember { mutableStateOf(false) }

    // 모두 삭제 하기의 confirm 용도의 alert dialog 생성
    DisplayAlertDialog(
        title = stringResource(id = R.string.delete_all_task_dialog_title),
        message = stringResource(id = R.string.delete_all_tasks_dialog_confirmation),
        openDialog = deleteAlertExpanded,
        onYesClicked = onDeleteAllClicked,
        onCloseDialog = { deleteAlertExpanded = false }
    )

    // 다이얼 로그 박스 에 대한 상태
    var exportAlertExpanded by remember { mutableStateOf(false) }

    // 모두 삭제 하기의 confirm 용도의 alert dialog 생성
    DisplayAlertDialog(
        title = stringResource(id = R.string.export_task_dialog_confirmation),
        message = stringResource(id = R.string.export_all_tasks_dialog_confirmation),
        openDialog = exportAlertExpanded,
        onYesClicked = onExportClick,
        onCloseDialog = { exportAlertExpanded = false }
    )


    var datePickerExpanded by remember { mutableStateOf(false) }
    SimpleDatePickerDialog(
        enabled = datePickerExpanded,
        onDismiss = {
            datePickerExpanded = false
        },
        onConfirmClick = onDatePickConfirmed
    )

    SearchAction(onSearchClicked)
    CommonAction(
        icon = Icons.Default.DateRange,
        onClicked = { datePickerExpanded = true },
        description = "date picker icon"
    )
    MenuAction(
        onDeleteAllClicked = {
            deleteAlertExpanded = true
        },
        onImportClick = onImportClick,
        onExportClick = {
            exportAlertExpanded = true
        }
    )
}


@Composable
fun SearchAction(
    onSearchClicked: () -> Unit
) {
    IconButton(onClick = { onSearchClicked() }) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = stringResource(R.string.search_bar_visible_action_icon),
            tint = MaterialTheme.colorScheme.topBarContentColor
        )
    }
}

@Composable
fun MenuAction(
    onDeleteAllClicked: () -> Unit,
    onImportClick: () -> Unit,
    onExportClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Filled.Menu,
            contentDescription = stringResource(R.string.delete_all_action),
            tint = MaterialTheme.colorScheme.topBarContentColor
        )
    }
    // offset 은 메뉴와 아이템 의 위치를 보정 하기 위함. 기본적 으로 우측의 경계를 넘어 가면
    // 위치가 완전히 틀어 진다. 여기 서는 최대로 82 dp 만큼 우측 으로 옮김
    DropdownMenu(
        expanded = expanded,
        offset = DpOffset(x = 30.dp, y = 0.dp),
        onDismissRequest = { expanded = false },
    ) {
        DropdownMenuItem(
//            modifier = Modifier.padding(start = LARGE_PADDING),
            text = { Text(text = stringResource(id = R.string.delete_all_menu_text)) },
            onClick = {
                expanded = false
                onDeleteAllClicked()
            })
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.import_menu_label)) },
            onClick = {
                expanded = false
                onImportClick()
            })
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.export_menu_label)) },
            onClick = {
                expanded = false
                onExportClick()
            })
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.settings_menu_label)) },
            onClick = {
                expanded = false
                onDeleteAllClicked()
            })
    }
}

@Composable
fun SearchAppBar(
    text: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(TOP_BAR_HEIGHT),
        color = MaterialTheme.colorScheme.topBarContainerColor
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = text,
            singleLine = true,
            onValueChange = { text ->
                onTextChange(text)
            },
            placeholder = {
                Text(
                    modifier = Modifier
                        .alpha(ALPHA_NOT_FOCUSED),
                    text = stringResource(id = R.string.search_placeholder),
                    color = MaterialTheme.colorScheme.topBarContentColor
                )
            },
            leadingIcon = {
                IconButton(onClick = {
                    onSearchClicked(text)
                }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(
                            id = R.string.search_execution_icon
                        ),
                        modifier = if (text.isNotEmpty()) Modifier.alpha(ALPHA_FOCUSED)
                        else Modifier.alpha(ALPHA_NOT_FOCUSED),
                        tint = MaterialTheme.colorScheme.topBarContentColor
                    )
                }
            },
            trailingIcon = {
                IconButton(onClick = {
                    if (text.isNotEmpty()) {
                        onTextChange("")
                    } else {
                        onTextChange("")
                        onCloseClicked()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(
                            id = R.string.close_icon
                        ),
                        tint = MaterialTheme.colorScheme.topBarContentColor
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchClicked(text)
                }
            ),
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.topBarContentColor,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.topBarContentColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTextApi::class)
@Composable
fun MultiSelectAppbar(
    scrollBehavior: TopAppBarScrollBehavior,
    selectedItemsCount: Int,
    onDeleteSelectedClicked: () -> Unit,
) {
    TopAppBar(
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            CommonAction(
                onClicked = { },
                icon = Icons.Default.ArrowBack,
                description = "Arrow backwards Icon"
            )
        },
        title = {
            Text(
                text = "$selectedItemsCount",
                color = MaterialTheme.colorScheme.topBarContentColor
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.topBarContainerColor
        ),
        actions = {
            MultiSelectAppbarActions(
                onDeleteSelectedClicked = onDeleteSelectedClicked,
            )
        }
    )
}

@Composable
fun MultiSelectAppbarActions(
    onDeleteSelectedClicked: () -> Unit,
) {
    // 다이얼 로그 박스 에 대한 상태
    var deleteAlertExpanded by remember { mutableStateOf(false) }

    // 모두 삭제 하기의 confirm 용도의 alert dialog 생성
    DisplayAlertDialog(
        title = stringResource(id = R.string.delete_selected_task_dialog_title),
        message = stringResource(id = R.string.delete_seleccted_tasks_dialog_confirmation),
        openDialog = deleteAlertExpanded,
        onYesClicked = onDeleteSelectedClicked,
        onCloseDialog = { deleteAlertExpanded = false }
    )

    CommonAction(
        icon = Icons.Default.Remove,
        onClicked = { deleteAlertExpanded = true },
        description = "date picker icon"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewMultiSelectAppbar() {
    MultiSelectAppbar(
        scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
        selectedItemsCount = 1
    ) {

    }
}