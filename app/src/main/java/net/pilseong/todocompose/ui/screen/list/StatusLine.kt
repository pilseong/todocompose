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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.data.model.ui.State
import net.pilseong.todocompose.data.model.ui.UserData
import net.pilseong.todocompose.ui.components.PriorityMenuItems
import net.pilseong.todocompose.ui.components.StateMenuItems
import net.pilseong.todocompose.ui.theme.FavoriteYellowColor
import net.pilseong.todocompose.ui.theme.HighPriorityColor
import net.pilseong.todocompose.ui.theme.LowPriorityColor
import net.pilseong.todocompose.ui.theme.MediumPriorityColor
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import net.pilseong.todocompose.util.Action
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun StatusLine(
    uiState: UserData,
    prioritySortState: Priority,
    orderEnabled: Boolean = false,
    dateEnabled: Boolean = false,
    searchRangeAll: Boolean = false,
    startDate: Long?,
    endDate: Long?,
    favoriteOn: Boolean = false,
    onCloseClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onOrderEnabledClick: () -> Unit,
    onDateEnabledClick: () -> Unit,
    onPrioritySelected: (Action, Priority) -> Unit,
    onStateSelected: (State) -> Unit,
    onRangeAllEnabledClick: (Boolean) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = XLARGE_PADDING)
        ) {
            LazyRow(
                modifier = Modifier
                    .padding(vertical = SMALL_PADDING)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                item {
                    // desc, asc
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

                item {
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
                                text = stringResource(id = R.string.badge_priority_label),
                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            )
                        }
                        DropdownMenu(
                            expanded = priorityMenuExpanded,
                            onDismissRequest = { priorityMenuExpanded = false },
                        ) {
                            PriorityMenuItems(
                                priorityHigh = uiState.priorityHigh,
                                priorityMedium = uiState.priorityMedium,
                                priorityLow = uiState.priorityLow,
                                priorityNone = uiState.priorityNone,
                                onPrioritySelected = { priority ->
                                    onPrioritySelected(Action.PRIORITY_FILTER_CHANGE, priority)
                                }
                            )
                        }
                    }
                }


                item {
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
                                text = stringResource(id = R.string.badge_state_label),
                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            )
                        }
                        DropdownMenu(
                            expanded = stateMenuExpanded,
                            onDismissRequest = { stateMenuExpanded = false },
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
                                })
                        }
                    }

                }

                item {
                    // favorite
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

                // 우선 순위 설정
                item {
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
                                    id = if (prioritySortState == Priority.NONE)
                                        R.string.badge_priority_label else prioritySortState.label
                                ),
                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            )
                        }
                        DropdownMenu(
                            expanded = priortySortMemuExpanded,
                            onDismissRequest = { priortySortMemuExpanded = false },
                        ) {
                            PriorityMenuItems { it ->
                                priortySortMemuExpanded = false
                                onPrioritySelected(Action.PRIORITY_CHANGE, it)
                            }
                        }
                    }

                }

                item {
                    // desc, asc
                    Card(
                        modifier = Modifier
                            .clickable {
                                onOrderEnabledClick()
                            },
                        shape = RoundedCornerShape(4.dp),
                        border = if (orderEnabled)
                            BorderStroke(color = Color.Transparent, width = 0.dp)
                        else
                            BorderStroke(
                                0.5F.dp,
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2F)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (orderEnabled) MaterialTheme.colorScheme
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
                                painter = if (orderEnabled) painterResource(id = R.drawable.ic_baseline_north_24)
                                else painterResource(id = R.drawable.ic_baseline_south_24),
                                contentDescription = "arrow"
                            )
                            Spacer(modifier = Modifier.width(SMALL_PADDING))
                            Text(
                                text = if (orderEnabled) stringResource(id = R.string.badge_order_asc_label)
                                else stringResource(id = R.string.badge_order_desc_label),
                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            )
                        }
                    }
                }

                item {
                    // updated, created
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
            }


            // 날짜 검색 부분 표출
            if (startDate != null || endDate != null) {
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


@Preview
@Composable
fun PreviewStatusLine() {
    MaterialTheme {
        StatusLine(
            uiState = UserData(),
            prioritySortState = Priority.NONE,
            orderEnabled = false,
            dateEnabled = false,
            startDate = 333,
            endDate = 222,
            onCloseClick = { /*TODO*/ },
            onFavoriteClick = { /*TODO*/ },
            onOrderEnabledClick = { /*TODO*/ },
            onDateEnabledClick = { /*TODO*/ },
            onPrioritySelected = { _, _ -> },
            onStateSelected = {},
            onRangeAllEnabledClick = {}
        )
    }
}