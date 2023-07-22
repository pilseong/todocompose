package net.pilseong.todocompose.ui.screen.list

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DriveFileMove
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
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
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.ui.State
import net.pilseong.todocompose.ui.components.DisplayAlertDialog
import net.pilseong.todocompose.ui.components.MultiSelectAppbar
import net.pilseong.todocompose.ui.components.MultiSelectAppbarActions
import net.pilseong.todocompose.ui.components.SimpleDateRangePickerSheet
import net.pilseong.todocompose.ui.screen.task.CommonAction
import net.pilseong.todocompose.ui.theme.ALPHA_NOT_FOCUSED
import net.pilseong.todocompose.ui.theme.PRIORITY_INDICATOR_SIZE
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.TOP_BAR_HEIGHT
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import net.pilseong.todocompose.util.SearchAppBarState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    appbarTitle: String,
    notebookColor: Color,
    searchRangeAll: Boolean = false,
    searchAppBarState: SearchAppBarState,
    searchText: String,
    searchNoFilterState: Boolean = false,
    onImportClick: () -> Unit,
    onAppBarTitleClick: () -> Unit,
    selectedItemsCount: Int,
    onDeleteSelectedClicked: () -> Unit,
    onBackButtonClick: () -> Unit,
    onSearchIconClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    onTextChange: (String) -> Unit,
    onSearchClicked: () -> Unit,
    onMoveMemoClicked: () -> Unit,
    onDeleteAllClicked: () -> Unit,
    onDateRangePickerConfirmed: (Long?, Long?) -> Unit,
    onExportClick: () -> Unit,
    onSearchNoFilterClicked: (Boolean) -> Unit,
    onStateSelectedForMultipleItems: (State) -> Unit,
) {

    if (selectedItemsCount > 0) {

        MultiSelectAppbar(
            scrollBehavior = scrollBehavior,
            selectedItemsCount = selectedItemsCount,
            onBackButtonClick = onBackButtonClick,
        ) {
            var expanded by remember { mutableStateOf(false) }
            MultiSelectAppbarActions(
                onDeleteTitle = R.string.delete_selected_task_dialog_title,
                onDeleteDescription = R.string.delete_selected_tasks_dialog_confirmation,
                onDeleteSelectedClicked = onDeleteSelectedClicked,
                actions = {
                    CommonAction(
                        icon = Icons.Default.PlaylistAddCheck,
                        onClicked = {
                            expanded = true
                        },
                        description = "Move to other box"
                    )
                    CommonAction(
                        icon = Icons.Default.DriveFileMove,
                        onClicked = onMoveMemoClicked,
                        description = "Move to other box"
                    )
                }
            )

            // 멀티 상태 선택 메뉴
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                State.values().reversed().forEach { state ->
                    DropdownMenuItem(
                        leadingIcon = {
                            Canvas(
                                modifier = Modifier
                                    .offset(0.dp, 0.8.dp)
                                    .size(PRIORITY_INDICATOR_SIZE)
                            ) {
                                drawCircle(color = state.color)
                            }
                        },
                        text = {
                            Text(
                                text = stringResource(id = state.label),
                                style = MaterialTheme.typography.labelMedium,
                                fontSize = MaterialTheme.typography.labelMedium.fontSize,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = {
                            onStateSelectedForMultipleItems(state)
                            expanded = false
                        })
                }
            }
        }
    } else {
        when (searchAppBarState) {
            SearchAppBarState.CLOSE -> {
                DefaultListAppBar(
                    scrollBehavior = scrollBehavior,
                    searchRangeAll = searchRangeAll,
                    appbarTitle = appbarTitle,
                    notebookColor = notebookColor,
                    onSearchIconClicked = onSearchIconClicked,
                    onDeleteAllClicked = onDeleteAllClicked,
                    onDateRangePickerConfirmed = onDateRangePickerConfirmed,
                    onImportClick = onImportClick,
                    onExportClick = onExportClick,
                    onAppBarTitleClick = onAppBarTitleClick
                )
            }

            else -> {
                SearchAppBar(
                    text = searchText,
                    searchNoFilterState = searchNoFilterState,
                    onCloseClicked = onCloseClicked,
                    onSearchClicked = onSearchClicked,
                    onTextChange = onTextChange,
                    onSearchNoFilterClicked = onSearchNoFilterClicked,
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultListAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    searchRangeAll: Boolean = false,
    appbarTitle: String = "Default",
    notebookColor: Color = MaterialTheme.colorScheme.surface,
    onSearchIconClicked: () -> Unit,
    onDeleteAllClicked: () -> Unit,
    onDateRangePickerConfirmed: (Long?, Long?) -> Unit,
    onImportClick: () -> Unit,
    onExportClick: () -> Unit,
    onAppBarTitleClick: () -> Unit,
) {
    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                modifier = Modifier
                    .clickable {
                        onAppBarTitleClick()
                    },
                text = if (searchRangeAll) stringResource(id = R.string.badge_search_range_all_label) else appbarTitle,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = notebookColor.copy(alpha = 0.5F)
        ),
        actions = {
            ListAppBarActions(
                notebookName = appbarTitle,
                onSearchClicked = onSearchIconClicked,
                onDeleteAllClicked = onDeleteAllClicked,
                onDatePickConfirmed = onDateRangePickerConfirmed,
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
        onDateRangePickerConfirmed = { _, _ -> },
        onImportClick = { /*TODO*/ },
        onExportClick = { /*TODO*/ }) {

    }
}

@Composable
fun ListAppBarActions(
    notebookName: String = "",
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
        message = stringResource(id = R.string.delete_all_tasks_dialog_confirmation, notebookName),
        openDialog = deleteAlertExpanded,
        onYesClicked = onDeleteAllClicked,
        onCloseDialog = { deleteAlertExpanded = false }
    )

    // Import 다이얼 로그 박스 에 대한 상태
    var importAlertExpanded by remember { mutableStateOf(false) }

    // import 의 confirm 용도의 alert dialog 생성
    DisplayAlertDialog(
        title = stringResource(id = R.string.import_dialog_title),
        message = stringResource(id = R.string.import_dialog_confirmation),
        openDialog = importAlertExpanded,
        onYesClicked = onImportClick,
        onCloseDialog = { importAlertExpanded = false }
    )

    // Export 다이얼 로그 박스 에 대한 상태
    var exportAlertExpanded by remember { mutableStateOf(false) }

    // 모두 삭제 하기의 confirm 용도의 alert dialog 생성
    DisplayAlertDialog(
        title = stringResource(id = R.string.export_memos_dialog_title),
        message = stringResource(id = R.string.export_all_memos_dialog_confirmation),
        openDialog = exportAlertExpanded,
        onYesClicked = onExportClick,
        onCloseDialog = { exportAlertExpanded = false }
    )


    var datePickerExpanded by remember { mutableStateOf(false) }
    SimpleDateRangePickerSheet(
        titleResource = R.string.date_search_range_picker_title,
        datePickerExpanded = datePickerExpanded,
        onDismissRequest = {
            datePickerExpanded = false
        },
        onConfirmClick = onDatePickConfirmed
    )

    SearchAction(onSearchClicked)
    CommonAction(
        icon = Icons.Default.DateRange,
        onClicked = { datePickerExpanded = true },
        description = "date picker icon",
    )
    MenuAction(
        onDeleteAllClicked = {
            deleteAlertExpanded = true
        },
        onImportClick = {
            importAlertExpanded = true
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
            tint = MaterialTheme.colorScheme.onSurface
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
            })
    }
}

@Composable
fun SearchAppBar(
    text: String,
    searchNoFilterState: Boolean = false,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: () -> Unit,
    onSearchNoFilterClicked: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .height(TOP_BAR_HEIGHT)
    ) {
        Surface(
            modifier = Modifier
                .padding(
                    horizontal = XLARGE_PADDING,
                    vertical = SMALL_PADDING
                )
                .fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            tonalElevation = 16.dp
        ) {
            val focusRequester = remember { FocusRequester() }
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = text,
                singleLine = true,
                onValueChange = { text ->
                    onTextChange(text)
                },
                label = {
                    Text(
                        text = if (searchNoFilterState) "전체" else "노트",
                        fontSize = MaterialTheme.typography.labelSmall.fontSize
                    )
                },
                placeholder = {
                    Text(
                        modifier = Modifier
                            .alpha(ALPHA_NOT_FOCUSED),
                        text = stringResource(id = R.string.search_placeholder),
                    )
                },
                leadingIcon = {
                    Checkbox(
                        checked = searchNoFilterState,
                        onCheckedChange = {
                            onSearchNoFilterClicked(!searchNoFilterState)
                        })
                },
                trailingIcon = {
                    IconButton(onClick = {
                        onCloseClicked()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(
                                id = R.string.close_icon
                            ),
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSearchClicked()
                    }
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                )
            )
        }
    }
}

@Preview
@Composable
fun PreviewSearchAppBar() {
    MaterialTheme {
        SearchAppBar(text = "검색",
            onTextChange = {},
            onCloseClicked = { /*TODO*/ },
            onSearchClicked = {},
            onSearchNoFilterClicked = {}
        )
    }

}

