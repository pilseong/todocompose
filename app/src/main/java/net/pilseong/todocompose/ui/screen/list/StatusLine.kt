package net.pilseong.todocompose.ui.screen.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ChecklistRtl
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.ui.MemoDateSortingOption
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.data.model.ui.SortOption
import net.pilseong.todocompose.data.model.ui.State
import net.pilseong.todocompose.data.model.ui.UserData
import net.pilseong.todocompose.ui.components.PriorityItem
import net.pilseong.todocompose.ui.components.PriorityMenuItems
import net.pilseong.todocompose.ui.components.StateMenuItems
import net.pilseong.todocompose.ui.theme.FavoriteYellowColor
import net.pilseong.todocompose.ui.theme.HighPriorityColor
import net.pilseong.todocompose.ui.theme.LowPriorityColor
import net.pilseong.todocompose.ui.theme.MediumPriorityColor
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import net.pilseong.todocompose.util.StateEntity
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun StatusLine(
    uiState: UserData,
    startDate: Long?,
    endDate: Long?,
    onCloseClick: () -> Unit,
    onFavoriteClick: (Boolean) -> Unit,
    onOrderEnabledClick: (Boolean) -> Unit,
    onDateSortingChangeClick: (MemoAction, MemoDateSortingOption, Boolean) -> Unit,
    onPrioritySelected: (MemoAction, Priority, Boolean) -> Unit,
    onStateSelected: (State) -> Unit,
    onRangeAllEnabledClick: (Boolean, Boolean) -> Unit,
    onToggleClicked: () -> Unit,
    onSetAllOrNothingClicked: (Boolean) -> Unit,
    onStatusLineUpdate: (StateEntity) -> Unit,
) {

    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = XLARGE_PADDING)
        ) {
            val lazyState = rememberLazyListState()

            // 처음 기본 화면에 들어 가는 status 갯수를 저장 한다.
            var initialSize by remember { mutableStateOf(0) }
            val stateSize by remember {
                derivedStateOf {
                    if (initialSize == 0 || (lazyState.firstVisibleItemIndex == 0 && !lazyState.canScrollBackward))
                        lazyState.layoutInfo.visibleItemsInfo.size
                    else
                        initialSize
                }
            }
            initialSize = stateSize

//            Log.d("PHILIP", "[StatusLine] initialSize $initialSize")

            LazyRow(
                modifier = Modifier
                    .padding(vertical = SMALL_PADDING)
                    .fillMaxWidth(),
                state = lazyState,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                itemsIndexed(
                    items = uiState.statusLineOrderState,
                    key = { _, item -> item }
                ) { index, item ->

                    val shouldUpdate = remember(initialSize) {
//                        Log.d("PHILIP", "[StatusLine] cal index: $index, initialSize: $initialSize")
                        index >= initialSize
                    }

                    when (item) {
                        StateEntity.NOTE_FILTER -> NoteFilterState(
                            searchRangeAll = uiState.searchRangeAll,
                            onRangeAllEnabledClick = { state ->
//                                Log.d("PHILIP", "[StatusLine] performed $index $shouldUpdate")
                                onRangeAllEnabledClick(state, shouldUpdate)
                                if (shouldUpdate) {
                                    scope.launch {
                                        lazyState.animateScrollToItem(0, 0)
                                    }
                                }
                            },
                        )

                        StateEntity.PRIORITY_FILTER -> PriorityFilter(uiState,
                            onPrioritySelected = { state, priority ->
                                // 닫을 때 정렬이 실행되도록 하기 위해 체크 박스 선택 시에는 정렬 없음
//                                Log.d("PHILIP", "[StatusLine] performed $index $shouldUpdate")
                                onPrioritySelected(state, priority, false)
                            },
                            onDismiss = {
                                if (shouldUpdate) {
                                    onStatusLineUpdate(StateEntity.PRIORITY_FILTER)
                                    scope.launch {
                                        lazyState.animateScrollToItem(0, 0)
                                    }
                                }
                            }
                        )

                        StateEntity.STATE_FILTER -> StatusFilter(
                            uiState,
                            onStateSelected = { state ->
                                onStateSelected(state)
                            },
                            onToggleClicked = onToggleClicked,
                            onSetAllOrNothingClicked = { state ->
                                onSetAllOrNothingClicked(state)
                            },
                            onDismiss = {
//                                Log.d("PHILIP", "[StatusLine] performed $index $shouldUpdate")
                                if (shouldUpdate) {
                                    onStatusLineUpdate(StateEntity.STATE_FILTER)
                                    scope.launch {
                                        lazyState.animateScrollToItem(0, 0)
                                    }
                                }
                            }
                        )

                        StateEntity.FAVORITE_FILTER -> FavoriteFilter(
                            uiState.sortFavorite,
                            onFavoriteClick = {
//                                Log.d("PHILIP", "[StatusLine] performed $index $shouldUpdate")
                                onFavoriteClick(shouldUpdate)
                                if (shouldUpdate) {
                                    scope.launch {
                                        lazyState.animateScrollToItem(0, 0)
                                    }
                                }
                            },
                        )

                        StateEntity.PRIORITY_ORDER -> PrioritySort(
                            uiState.prioritySortState,
                            onPrioritySelected = { action, priority ->
//                                Log.d("PHILIP", "[StatusLine] performed $index $shouldUpdate")
                                onPrioritySelected(action, priority, shouldUpdate)
                                if (shouldUpdate) {
                                    scope.launch {
                                        lazyState.animateScrollToItem(0, 0)
                                    }
                                }
                            }
                        )

                        StateEntity.SORTING_ORDER -> SortingOrder(uiState.dateOrderState,
                            onOrderEnabledClick = {
//                                Log.d("PHILIP", "[StatusLine] performed $index $shouldUpdate")
                                onOrderEnabledClick(shouldUpdate)
                                if (shouldUpdate) {
                                    scope.launch {
                                        lazyState.animateScrollToItem(0, 0)
                                    }
                                }
                            }
                        )

                        StateEntity.DATE_BASE_ORDER -> DateBaseOrder(
                            uiState.memoDateSortingState,
                            onSortingSelected = { action, selectedOption ->
//                                Log.d("PHILIP", "[StatusLine] performed $index $shouldUpdate")
                                onDateSortingChangeClick(action, selectedOption, shouldUpdate)
                                if (shouldUpdate) {
                                    scope.launch {
                                        lazyState.animateScrollToItem(0, 0)
                                    }
                                }
                            },
                        )
                    }
                }
            }


            // 날짜 검색 부분 표출
            if (startDate != null || endDate != null) DateFilter(startDate, endDate, onCloseClick)

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05F)
                    )
            )
        }
    }
}

@Composable
private fun DateFilter(
    startDate: Long?,
    endDate: Long?,
    onCloseClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val startDateStr = if (startDate != null)
            ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(startDate),
                ZoneId.systemDefault()
            ).format(
                DateTimeFormatter.ofPattern(stringResource(id = R.string.datepicker_date_format))
            )
        else stringResource(id = R.string.status_line_date_range_from_the_first_memo_text)

        val endDateStr = if (endDate != null)
            ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(endDate),
                ZoneId.systemDefault()
            ).format(
                DateTimeFormatter.ofPattern(stringResource(id = R.string.datepicker_date_format))
            )
        else stringResource(id = R.string.status_line_date_range_up_to_date_text)
        Surface(
//                        color = MaterialTheme.colorScheme.onPrimaryElevation
        ) {
            Text(
                text = stringResource(
                    id = R.string.status_line_date_range_text,
                    startDateStr, endDateStr
                ),
                fontSize = MaterialTheme.typography.titleSmall.fontSize
            )
        }

        Icon(
            modifier = Modifier
                .size(16.dp)
                .border(
                    border = BorderStroke(
                        0.dp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                .clickable {
                    onCloseClick()
                },
            imageVector = Icons.Default.Close,
            contentDescription = "close button",
        )
    }
}

@Composable
private fun DateBaseOrder(dateEnabled: Boolean, onDateEnabledClick: () -> Unit) {
    Card(
        modifier = Modifier
            .clickable {
                onDateEnabledClick()
            },
        shape = RoundedCornerShape(4.dp),
        border = if (dateEnabled)
            BorderStroke(color = Color.Transparent, width = 0.dp)
        else
            BorderStroke(
                0.5F.dp,
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2F)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (dateEnabled) MaterialTheme.colorScheme
                .surfaceColorAtElevation(6.dp)
            else Color.Transparent,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SMALL_PADDING),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.width(12.dp),
                imageVector = if (dateEnabled) Icons.TwoTone.Edit
                else Icons.Default.Update,
                contentDescription = "star"
            )
            Spacer(modifier = Modifier.width(SMALL_PADDING))
            Text(
                text = if (dateEnabled) stringResource(id = R.string.badge_date_created_at_label)
                else stringResource(id = R.string.badge_date_updated_at_label),
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
            )
        }
    }
}

@Composable
private fun DateBaseOrder(
    memoDateSortingOption: MemoDateSortingOption,
    onSortingSelected: (MemoAction, MemoDateSortingOption) -> Unit
) {
    var sortMenuExpanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .clickable {
                sortMenuExpanded = true
            },
        border = BorderStroke(
            width = if (memoDateSortingOption == MemoDateSortingOption.UPDATED_AT) 0F.dp else 0.5F.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2F)
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (memoDateSortingOption == MemoDateSortingOption.UPDATED_AT)
                Color.Transparent
            else
                MaterialTheme.colorScheme
                    .surfaceColorAtElevation(6.dp)
        ),
        shape = RoundedCornerShape(4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SMALL_PADDING),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.width(12.dp),
                imageVector = Icons.Default.Sort,
                contentDescription = "arrow"
            )
            Spacer(modifier = Modifier.width(SMALL_PADDING))
            Text(
                text = stringResource(
                    id = when (memoDateSortingOption) {
                        MemoDateSortingOption.CREATED_AT -> MemoDateSortingOption.CREATED_AT.label
                        MemoDateSortingOption.UPDATED_AT -> MemoDateSortingOption.UPDATED_AT.label
                        MemoDateSortingOption.FINISHED_AT -> MemoDateSortingOption.FINISHED_AT.label
                        MemoDateSortingOption.DUE_DATE -> MemoDateSortingOption.DUE_DATE.label
                    }
                ),
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
            )
        }
        DropdownMenu(
            expanded = sortMenuExpanded,
            onDismissRequest = { sortMenuExpanded = false },
        ) {
            MemoDateSortingOption.values().forEach { menu ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = menu.label),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    onClick = {
                        sortMenuExpanded = false
                        onSortingSelected(MemoAction.MEMO_SORT_DATE_BASE_CHANGE, menu)
                    })
            }
        }
    }
}


@Composable
private fun SortingOrder(sortOption: SortOption, onOrderEnabledClick: () -> Unit) {
    Card(
        modifier = Modifier
            .clickable {
                onOrderEnabledClick()
            },
        shape = RoundedCornerShape(4.dp),
        border = if (sortOption == SortOption.DESC)
            BorderStroke(color = Color.Transparent, width = 0.dp)
        else
            BorderStroke(
                0.5F.dp,
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2F)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (sortOption == SortOption.DESC) MaterialTheme.colorScheme
                .surfaceColorAtElevation(6.dp)
            else Color.Transparent,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SMALL_PADDING),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.width(12.dp),
                imageVector = if (sortOption == SortOption.DESC) Icons.Default.ArrowUpward
                else Icons.Default.ArrowDownward,
                contentDescription = "arrow"
            )
            Spacer(modifier = Modifier.width(SMALL_PADDING))
            Text(
                text = if (sortOption == SortOption.ASC) stringResource(id = R.string.badge_order_asc_label)
                else stringResource(id = R.string.badge_order_desc_label),
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
            )
        }
    }
}

@Composable
private fun PrioritySort(
    prioritySortState: Priority,
    onPrioritySelected: (MemoAction, Priority) -> Unit
) {
    var priortySortMemuExpanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .clickable {
                priortySortMemuExpanded = true
            },
        border = if (prioritySortState != Priority.NONE)
            BorderStroke(color = Color.Transparent, width = 0.dp)
        else
            BorderStroke(
                0.5F.dp,
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2F)
            ),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (prioritySortState) {
                Priority.HIGH -> {
                    HighPriorityColor
                }

                Priority.MEDIUM -> {
                    MediumPriorityColor
                }

                Priority.LOW -> {
                    LowPriorityColor
                }

                else -> {
                    Color.Transparent
                }
            },
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SMALL_PADDING),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.width(12.dp),
                imageVector = Icons.Default.Sort,
                contentDescription = "arrow"
            )
            Spacer(modifier = Modifier.width(SMALL_PADDING))
            Text(
                text = stringResource(
                    id = when (prioritySortState) {
                        Priority.HIGH -> R.string.badge_priority_high_to_low_label
                        Priority.LOW -> R.string.badge_priority_low_to_high_label
                        Priority.NONE -> R.string.badge_priority_sorting_label
                        else -> {
                            R.string.badge_priority_sorting_label
                        }
                    }
                ),
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
            )
        }
        DropdownMenu(
            expanded = priortySortMemuExpanded,
            onDismissRequest = { priortySortMemuExpanded = false },
        ) {
            DropdownMenuItem(
                text = {
                    PriorityItem(
                        priority = Priority.HIGH,
                        label = R.string.badge_priority_high_to_low_label
                    )
                },
                onClick = {
                    priortySortMemuExpanded = false
                    onPrioritySelected(MemoAction.PRIORITY_CHANGE, Priority.HIGH)
                })
            DropdownMenuItem(
                text = {
                    PriorityItem(
                        priority = Priority.LOW,
                        label = R.string.badge_priority_low_to_high_label
                    )
                },
                onClick = {
                    priortySortMemuExpanded = false
                    onPrioritySelected(MemoAction.PRIORITY_CHANGE, Priority.LOW)
                })
            DropdownMenuItem(
                text = { PriorityItem(priority = Priority.NONE) },
                onClick = {
                    priortySortMemuExpanded = false
                    onPrioritySelected(MemoAction.PRIORITY_CHANGE, Priority.NONE)
                })
        }
    }
}

@Composable
private fun FavoriteFilter(favoriteOn: Boolean, onFavoriteClick: () -> Unit) {
    Card(
        modifier = Modifier
            .clickable {
                onFavoriteClick()
            },
        shape = RoundedCornerShape(4.dp),
        border = if (favoriteOn)
            BorderStroke(color = Color.Transparent, width = 0.dp)
        else
            BorderStroke(
                0.5F.dp,
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2F)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (favoriteOn) MaterialTheme.colorScheme.FavoriteYellowColor
            else Color.Transparent,
//                        contentColor = MaterialTheme.colorScheme.topBarContentColor,
        ),
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SMALL_PADDING),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.width(12.dp),
                imageVector = Icons.Default.Star,
                contentDescription = "star"
            )
            Spacer(modifier = Modifier.width(SMALL_PADDING))
            Text(
                text = stringResource(id = R.string.badge_favorite_label),
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
            )
        }
    }
}

@Composable
private fun StatusFilter(
    uiState: UserData,
    onStateSelected: (State) -> Unit,
    onToggleClicked: () -> Unit,
    onSetAllOrNothingClicked: (Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    var stateMenuExpanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .clickable {
                stateMenuExpanded = true
            },
        border =
        BorderStroke(
            0.5F.dp,
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2F)
        ),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SMALL_PADDING),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.width(12.dp),
                imageVector = Icons.Default.ChecklistRtl,
                contentDescription = "Check list icon"
            )
            Spacer(modifier = Modifier.width(SMALL_PADDING))
            Text(
                text = stringResource(id = R.string.badge_state_filter_label),
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
            )
        }
        DropdownMenu(
            expanded = stateMenuExpanded,
            onDismissRequest = {
                stateMenuExpanded = false
                onDismiss()
            },
        ) {
            StateMenuItems(
                stateCompleted = uiState.stateCompleted,
                stateCancelled = uiState.stateCancelled,
                stateActive = uiState.stateActive,
                stateSuspended = uiState.stateSuspended,
                stateWaiting = uiState.stateWaiting,
                stateNone = uiState.stateNone,
                onStateSelected = { state ->
                    onStateSelected(state)
                },
                onToggleClicked = onToggleClicked,
                onSetAllOrNothingClicked = onSetAllOrNothingClicked,
            )
        }
    }
}

@Composable
private fun PriorityFilter(
    uiState: UserData,
    onPrioritySelected: (MemoAction, Priority) -> Unit,
    onDismiss: () -> Unit,
) {
    var priorityMenuExpanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .clickable {
                priorityMenuExpanded = true
            },
        border =
        BorderStroke(
            0.5F.dp,
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2F)
        ),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SMALL_PADDING),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.width(12.dp),
                imageVector = Icons.Default.ChecklistRtl,
                contentDescription = "Check list icon"
            )
            Spacer(modifier = Modifier.width(SMALL_PADDING))
            Text(
                text = stringResource(id = R.string.badge_priority_filter_label),
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
            )
        }
        DropdownMenu(
            expanded = priorityMenuExpanded,
            onDismissRequest = {
                priorityMenuExpanded = false
                onDismiss()
            },
        ) {
            PriorityMenuItems(
                priorityHigh = uiState.priorityHigh,
                priorityMedium = uiState.priorityMedium,
                priorityLow = uiState.priorityLow,
                priorityNone = uiState.priorityNone,
                onPrioritySelected = { priority ->
                    onPrioritySelected(MemoAction.PRIORITY_FILTER_CHANGE, priority)
                }
            )
        }
    }
}

@Composable
private fun NoteFilterState(
    onRangeAllEnabledClick: (Boolean) -> Unit,
    searchRangeAll: Boolean
) {
    Card(
        modifier = Modifier
            .clickable {
                onRangeAllEnabledClick(!searchRangeAll)
            },
        shape = RoundedCornerShape(4.dp),
        border = if (searchRangeAll)
            BorderStroke(color = Color.Transparent, width = 0.dp)
        else
            BorderStroke(
                0.5F.dp,
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2F)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (searchRangeAll) MaterialTheme.colorScheme
                .surfaceColorAtElevation(6.dp)
            else Color.Transparent,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SMALL_PADDING),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.width(12.dp),
                imageVector = if (searchRangeAll) Icons.Default.SelectAll
                else Icons.Default.Search,
                contentDescription = "Check list icon"
            )
            Spacer(modifier = Modifier.width(SMALL_PADDING))
            Text(
                text = if (searchRangeAll) stringResource(id = R.string.badge_search_range_all_label)
                else stringResource(id = R.string.badge_search_range_note_label),
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
            )
        }
    }
}


@Preview
@Composable
fun PreviewStatusLine() {
    MaterialTheme {
        StatusLine(
            uiState = UserData(),
            startDate = 333,
            endDate = 222,
            onCloseClick = { /*TODO*/ },
            onFavoriteClick = { /*TODO*/ },
            onOrderEnabledClick = { /*TODO*/ },
            onDateSortingChangeClick = { _, _, _ -> },
            onPrioritySelected = { _, _, _ -> },
            onStateSelected = {},
            onRangeAllEnabledClick = { _, _ -> },
            onToggleClicked = {},
            onSetAllOrNothingClicked = {},
            onStatusLineUpdate = {},
        )
    }
}