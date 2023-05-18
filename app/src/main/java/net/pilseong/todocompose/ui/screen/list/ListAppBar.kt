package net.pilseong.todocompose.ui.screen.list

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.DpOffset
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

@Composable
fun ListAppBar(
    memoViewModel: MemoViewModel,
    searchAppBarState: SearchAppBarState,
    searchText: String
) {
    when (searchAppBarState) {
        SearchAppBarState.CLOSE -> {
            DefaultListAppBar(
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
                onExportClick = {
                    memoViewModel.exportData()
                },
            )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultListAppBar(
    onSearchIconClicked: () -> Unit,
    onDeleteAllClicked: () -> Unit,
    onDatePickConfirmed: (Long?, Long?) -> Unit,
    onExportClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.list_screen_title),
                color = MaterialTheme.colorScheme.topBarContentColor
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.topBarContainerColor
        ),
        actions = {
            ListAppBarActions(
                onSearchClicked = onSearchIconClicked,
                onDeleteAllClicked = onDeleteAllClicked,
                onDatePickConfirmed = onDatePickConfirmed,
                onExportClick = onExportClick
            )
        }
    )
}

@Composable
fun ListAppBarActions(
    onSearchClicked: () -> Unit,
    onDeleteAllClicked: () -> Unit,
    onDatePickConfirmed: (Long?, Long?) -> Unit,
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
                onDeleteAllClicked()
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

