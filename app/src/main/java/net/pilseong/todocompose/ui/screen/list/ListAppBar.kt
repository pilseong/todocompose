package net.pilseong.todocompose.ui.screen.list

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.ui.components.DisplayAlertDialog
import net.pilseong.todocompose.ui.components.PriorityItem
import net.pilseong.todocompose.ui.components.SortItem
import net.pilseong.todocompose.ui.theme.ALPHA_FOCUSED
import net.pilseong.todocompose.ui.theme.ALPHA_NOT_FOCUSED
import net.pilseong.todocompose.ui.theme.TOP_BAR_HEIGHT
import net.pilseong.todocompose.ui.theme.topBarContainerColor
import net.pilseong.todocompose.ui.theme.topBarContentColor
import net.pilseong.todocompose.ui.viewmodel.SharedViewModel
import net.pilseong.todocompose.util.Action
import net.pilseong.todocompose.util.SearchAppBarState

@Composable
fun ListAppBar(
    sharedViewModel: SharedViewModel,
    searchAppBarState: SearchAppBarState,
    searchText: String
) {
    when (searchAppBarState) {
        SearchAppBarState.CLOSE -> {
            DefaultListAppBar(
                onSearchIconClicked = {
                    // 초기 로딩 을 위한 데이터 검색
                    sharedViewModel.refreshAllTasks()
                    sharedViewModel.searchAppBarState.value = SearchAppBarState.OPEN
                },
                onSortClicked = { priority ->
                    Log.i("PHILIP", "onSortClicked")
                    sharedViewModel.handleActions(
                        Action.PRIORITY_CHANGE,
                        priority = priority
                    )
                },
                onDeleteAllClicked = {
                    Log.i("PHILIP", "onDeleteAllClicked")
                    sharedViewModel.handleActions(Action.DELETE_ALL)
                },
                orderEnabled = sharedViewModel.orderEnabled,
                dateEnabled = sharedViewModel.dateEnabled,
                onOrderEnabledClick = {
                    sharedViewModel.handleActions(
                        action = Action.SORT_ORDER_CHANGE,
                        sortOrderEnabled = !sharedViewModel.orderEnabled
                    )
                },
                onDateEnabledClick = {
                    sharedViewModel.handleActions(
                        action = Action.SORT_DATE_CHANGE,
                        sortDateEnabled = !sharedViewModel.dateEnabled
                    )
                }
            )
        }

        else -> {
            SearchAppBar(
                text = searchText,
                onCloseClicked = {
                    sharedViewModel.onCloseSearchBar()
                },
                onSearchClicked = {
                    sharedViewModel.searchAppBarState.value = SearchAppBarState.TRIGGERED
                    sharedViewModel.refreshAllTasks()
                },
                onTextChange = { text ->
                    sharedViewModel.searchAppBarState.value = SearchAppBarState.TRIGGERED
                    sharedViewModel.searchTextString = text
                    sharedViewModel.refreshAllTasks()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultListAppBar(
    onSearchIconClicked: () -> Unit,
    onSortClicked: (Priority) -> Unit,
    onDeleteAllClicked: () -> Unit,
    orderEnabled: Boolean = false,
    dateEnabled: Boolean = false,
    onOrderEnabledClick: () -> Unit,
    onDateEnabledClick: () -> Unit
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
                onSortClicked = onSortClicked,
                onDeleteAllClicked = onDeleteAllClicked,
                orderEnabled = orderEnabled,
                dateEnabled = dateEnabled,
                onOrderEnabledClick = onOrderEnabledClick,
                onDateEnabledClick = onDateEnabledClick
            )
        }
    )
}

@Composable
fun ListAppBarActions(
    onSearchClicked: () -> Unit,
    onSortClicked: (Priority) -> Unit,
    onDeleteAllClicked: () -> Unit,
    orderEnabled: Boolean,
    dateEnabled: Boolean,
    onOrderEnabledClick: () -> Unit,
    onDateEnabledClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // 모두 삭제 하기의 confirm 용도의 alert dialog 생성
    DisplayAlertDialog(
        title = stringResource(id = R.string.delete_all_task_dialog_title),
        message = stringResource(id = R.string.delete_all_tasks_dialog_confirmation),
        openDialog = expanded,
        onYesClicked = onDeleteAllClicked,
        onCloseDialog = { expanded = false }
    )

    SearchAction(onSearchClicked)
    PriorityAction(onSortClicked)
    SortAction(
        orderEnabled = orderEnabled,
        dateEnabled = dateEnabled,
        onOrderEnabledClick = onOrderEnabledClick,
        onDateEnabledClick = onDateEnabledClick
    )
    DeleteAction(onDeleteAllClicked = { expanded = true })
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
fun PriorityAction(
    onSortClicked: (Priority) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_filter_list_24),
            contentDescription = stringResource(R.string.sort_action),
            tint = MaterialTheme.colorScheme.topBarContentColor
        )
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
            text = { PriorityItem(priority = Priority.HIGH) },
            onClick = {
                expanded = false
                onSortClicked(Priority.HIGH)
            })
        DropdownMenuItem(
            text = { PriorityItem(priority = Priority.LOW) },
            onClick = {
                expanded = false
                onSortClicked(Priority.LOW)
            })
        DropdownMenuItem(
            text = { PriorityItem(priority = Priority.NONE) },
            onClick = {
                expanded = false
                onSortClicked(Priority.NONE)
            })
    }
}

@Composable
fun SortAction(
    orderEnabled: Boolean,
    dateEnabled: Boolean,
    onOrderEnabledClick: () -> Unit,
    onDateEnabledClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_sort_24),
            contentDescription = stringResource(R.string.sort_action),
            tint = MaterialTheme.colorScheme.topBarContentColor
        )
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        Log.i("PHILIP", "orderEnabled $orderEnabled")
        Log.i("PHILIP", "dateEnabled $dateEnabled")
        DropdownMenuItem(
            enabled = false,
            text = {
                SortItem(
                    text = "오름차순",
                    enabled = orderEnabled,
                    onclick = onOrderEnabledClick
                )
            },
            onClick = {
                onOrderEnabledClick()
            })
        DropdownMenuItem(
            text = {
                SortItem(
                    text = "생성기준",
                    enabled = dateEnabled,
                    onclick = onDateEnabledClick
                )
            },
            onClick = {
                onDateEnabledClick()
            })
    }
}

@Composable
fun DeleteAction(
    onDeleteAllClicked: () -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Filled.Menu,
            contentDescription = stringResource(R.string.delete_all_action),
            tint = MaterialTheme.colorScheme.topBarContentColor
        )
    }
    DropdownMenu(
        expanded = expanded,
        offset = DpOffset(x = 32.dp, y = 0.dp),
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
//            modifier = Modifier.padding(start = LARGE_PADDING),
            text = { Text(text = stringResource(id = R.string.delete_all_menu_text)) },
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

