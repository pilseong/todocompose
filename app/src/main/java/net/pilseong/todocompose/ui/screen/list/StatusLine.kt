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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.data.model.State
import net.pilseong.todocompose.ui.components.PriorityMenuItems
import net.pilseong.todocompose.ui.components.StateMenuItems
import net.pilseong.todocompose.ui.theme.FavoriteYellowColor
import net.pilseong.todocompose.ui.theme.HighPriorityColor
import net.pilseong.todocompose.ui.theme.LowPriorityColor
import net.pilseong.todocompose.ui.theme.MediumPriorityColor
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun StatusLine(
    prioritySortState: Priority,
    orderEnabled: Boolean,
    dateEnabled: Boolean,
    startDate: Long?,
    endDate: Long?,
    favoriteOn: Boolean = false,
    stateClosed: Boolean = true,
    stateOnit: Boolean = true,
    stateSuspended: Boolean = true,
    stateOpen: Boolean = true,
    stateNone: Boolean = true,
    onCloseClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onOrderEnabledClick: () -> Unit,
    onDateEnabledClick: () -> Unit,
    onPrioritySelected: (Priority) -> Unit,
    onStateSelected: (State) -> Unit,
) {
    var containerColor = Color.Transparent
    var priorityIcon = painterResource(id = R.drawable.ic_baseline_menu_24)
    when (prioritySortState) {
        Priority.HIGH -> {
            containerColor = HighPriorityColor
            priorityIcon = painterResource(id = R.drawable.baseline_priority_high_24)
        }

        Priority.MEDIUM -> {
            containerColor = MediumPriorityColor
            priorityIcon = painterResource(id = R.drawable.ic_baseline_menu_24)
        }

        Priority.LOW -> {
            containerColor = LowPriorityColor
            priorityIcon = painterResource(id = R.drawable.ic_baseline_low_priority_24)
        }

        else -> {

        }
    }

    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth(),
//            .height(if (startDate != null || endDate != null) 64.dp else 30.dp),
//        tonalElevation = 1.dp
//        color = MaterialTheme.colorScheme.topBarContainerColor

    ) {
        var menuItemSwitch by remember { mutableStateOf(0) }
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
                // 우선 순위 설정
                item {
                    Card(
                        modifier = Modifier
                            .clickable {
                                menuItemSwitch = 0
                                expanded = true
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
                            containerColor = containerColor,
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
                                painter = priorityIcon,
                                contentDescription = "arrow"
                            )
                            Spacer(modifier = Modifier.width(SMALL_PADDING))
                            Text(
                                text = stringResource(id = prioritySortState.label),
                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            )
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
                                else Icons.TwoTone.Edit,
                                contentDescription = "star"
                            )
                            Spacer(modifier = Modifier.width(SMALL_PADDING))
                            Text(
                                text = if (dateEnabled) stringResource(id = R.string.badge_date_created_at_label)
                                else stringResource(id = R.string.badge_date_updated_at_label),
                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
//                            fontWeight = FontWeight.ExtraBold
                            )
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

                item {
                    Card(
                        modifier = Modifier
                            .clickable {
                                menuItemSwitch = 1
                                expanded = true
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
                            containerColor = containerColor,
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
                                painter = priorityIcon,
                                contentDescription = "arrow"
                            )
                            Spacer(modifier = Modifier.width(SMALL_PADDING))
                            Text(
                                text = "State",
                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            )
                        }
                    }
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = if (menuItemSwitch == 0) DpOffset(0.dp, 0.dp) else DpOffset(250.dp, 0.dp)
            ) {
                if (menuItemSwitch == 0) {
                    PriorityMenuItems { it ->
                        expanded = false
                        onPrioritySelected(it)
                    }
                }
                else if (menuItemSwitch == 1)
                    StateMenuItems(
                        stateClosed = stateClosed,
                        stateOnit = stateOnit,
                        stateSuspended = stateSuspended,
                        stateOpen = stateOpen,
                        stateNone = stateNone,
                        onStateSelected = { state ->
                            onStateSelected(state)
                        })
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
                            DateTimeFormatter.ofPattern("yy/MM/dd")
                        )
                    else stringResource(id = R.string.status_line_date_range_from_the_first_memo_text)

                    val endDateStr = if (endDate != null)
                        ZonedDateTime.ofInstant(
                            Instant.ofEpochMilli(endDate),
                            ZoneId.systemDefault()
                        ).format(
                            DateTimeFormatter.ofPattern("yy/MM/dd")
                        )
                    else stringResource(id = R.string.status_line_date_range_up_to_date_text)
                    Surface(
//                        color = MaterialTheme.colorScheme.onPrimaryElevation
                    ) {
                        Text(
//                            modifier = Modifier
//                                .padding(SMALL_PADDING),
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
            prioritySortState = Priority.NONE,
            orderEnabled = false,
            dateEnabled = false,
            startDate = 333,
            endDate = 222,
            onCloseClick = { /*TODO*/ },
            onFavoriteClick = { /*TODO*/ },
            onOrderEnabledClick = { /*TODO*/ },
            onDateEnabledClick = { /*TODO*/ },
            onPrioritySelected = {},
            onStateSelected = {}
        )
    }
}