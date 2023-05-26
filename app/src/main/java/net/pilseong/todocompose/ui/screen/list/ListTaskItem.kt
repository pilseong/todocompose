package net.pilseong.todocompose.ui.screen.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.data.model.TodoTask
import net.pilseong.todocompose.ui.theme.FavoriteYellow
import net.pilseong.todocompose.ui.theme.HighPriorityColor
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.LowPriorityColor
import net.pilseong.todocompose.ui.theme.MediumPriorityColor
import net.pilseong.todocompose.ui.theme.NonePriorityColor
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.TodoComposeTheme
import net.pilseong.todocompose.ui.theme.taskItemContentColor
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskItem(
    modifier: Modifier = Modifier,
    todoTask: TodoTask,
    toTaskScreen: (Int) -> Unit,
//    onLongClick: () -> Unit,
//    onDeselectedClick: (Int) -> Unit,
    datetime: ZonedDateTime,
    onFavoriteClick: () -> Unit,
    onLongClickReleased: (Int) -> Unit,
    onLongClickApplied: (Int) -> Unit
) {
    var selected by remember { mutableStateOf(false) }
    var favoriteOn by remember { mutableStateOf(todoTask.favorite) }
    val localDensity = LocalDensity.current
    var componentHeight by remember { mutableStateOf(0.dp) }

    Row {
        Spacer(modifier = Modifier.width(0.dp))
        Surface(modifier = Modifier
            .width(2.dp)
            .height(LARGE_PADDING + componentHeight),
            tonalElevation = 100000000.dp,
            content = {}
        )
        Spacer(modifier = Modifier.width(20.dp))
        Surface(
            modifier = Modifier
                .onGloballyPositioned {
                    componentHeight = with(localDensity) {
                        it.size.height.toDp()
                    }
                }
                .combinedClickable(
                    onClick = {
                        toTaskScreen(todoTask.id)
                    },
                    onLongClick = {
                        selected = true
                        onLongClickApplied(todoTask.id)
                    }
                ),
            color = Color.Transparent,
            tonalElevation = 2.dp,
        ) {
            val tintColor = when (todoTask.priority) {
                Priority.HIGH -> HighPriorityColor
                Priority.MEDIUM -> MediumPriorityColor
                Priority.LOW -> LowPriorityColor
                Priority.NONE -> NonePriorityColor
            }
            Card(
                modifier = modifier,
                colors = CardDefaults.cardColors(
                    containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(4.dp),
            ) {
                Row(
                    modifier = Modifier
                        .padding(vertical = LARGE_PADDING)
                        .fillMaxWidth()
                        .height(50.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(2f)
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            modifier = Modifier
                                .clickable(enabled = selected) {
                                    selected = false
                                    onLongClickReleased(todoTask.id)
                                },
                            painter = if (selected)
                                painterResource(id = R.drawable.ic_baseline_check_circle_24)
                            else
                                painterResource(id = R.drawable.ic_baseline_circle_24),
                            contentDescription = if (selected) "Checked Circle" else "Circle",
                            tint = if (selected) MaterialTheme.colorScheme.primary else tintColor
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(11f),
                    ) {
                        Text(
                            text = todoTask.title,
                            color = MaterialTheme.colorScheme.taskItemContentColor,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = todoTask.description,
                            color = MaterialTheme.colorScheme.taskItemContentColor,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Column(
                        modifier = Modifier
                            .padding(PaddingValues(end = SMALL_PADDING))
                            .fillMaxHeight()
                            .weight(2.2f),
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1F)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier,
                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                text = datetime.toLocalTime()
                                    .format(DateTimeFormatter.ofPattern("HH:mm"))
                                //                            text = "${datetime.month.name} ${datetime.dayOfMonth}"
                            )
                        }
                        Row(
                            modifier = Modifier
                                .weight(2F)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                modifier = Modifier.clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    onFavoriteClick()
                                    favoriteOn = !favoriteOn
                                },
                                imageVector = Icons.Default.Star,
                                contentDescription = stringResource(id = R.string.task_item_star_description),
                                tint = if (favoriteOn) FavoriteYellow else Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun TaskItemPreview() {
    TodoComposeTheme {
        TaskItem(
            todoTask = TodoTask(
                1, "필성 힘내!!!",
                "할 수 있어. 다 와 간다. 힘내자 다 할 수 있어 잘 될 거야",
                Priority.HIGH,
                notebookId = -1
            ),
            toTaskScreen = {},
            datetime = ZonedDateTime.now(),
            onFavoriteClick = {},
            onLongClickReleased = {},
            onLongClickApplied = {}
        )
    }
}

