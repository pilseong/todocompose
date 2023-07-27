package net.pilseong.todocompose.ui.screen.calendar

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import net.pilseong.todocompose.data.model.MemoTask
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.data.model.ui.ReminderTime
import net.pilseong.todocompose.data.model.ui.State
import net.pilseong.todocompose.ui.components.TaskHeader
import net.pilseong.todocompose.ui.components.TaskHeaderType
import net.pilseong.todocompose.ui.theme.MEDIUM_PADDING
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.TodoComposeTheme
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import net.pilseong.todocompose.ui.theme.taskItemContentColor
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ScheduleListSheet(
    memos: List<MemoWithNotebook>,
    expanded: Boolean = false,
    onDismissRequest: () -> Unit,
) {
    if (expanded) {
        val state = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        ModalBottomSheet(
            onDismissRequest = {
                Log.d("PHILIP", "SimpleDateRangePickerSheet onDismissRequest")
                onDismissRequest()
            },
            sheetState = state,
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(700.dp)
                    .padding(horizontal = XLARGE_PADDING),
            ) {
                items(
                    items = memos,
                    key = { item -> item.memo.id },
                ) { item ->
                    ScheduleItem(
                        modifier = Modifier.padding(bottom = SMALL_PADDING),
                        item = item
                    )
                }
            }
        }
    }
}

@Composable
private fun ScheduleItem(
    modifier: Modifier = Modifier,
    item: MemoWithNotebook
) {
    val cornerRadius = 8.dp
    val cutCornerSize = 20.dp
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.clickable {
            expanded = !expanded
        },
        shape = RoundedCornerShape(cornerRadius),
        tonalElevation = if (expanded && item.memo.priority == Priority.NONE) 8.dp else 0.dp,
        shadowElevation = if (expanded && item.memo.priority == Priority.NONE) 2.dp else 0.dp,
        color = if (expanded) {
            if (item.memo.priority == Priority.NONE) MaterialTheme.colorScheme.surface
            else item.memo.priority.color.copy(alpha = 0.2F)
        } else Color.Transparent
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth(),
//            verticalArrangement = Arrangement.Center
        ) {
            if (!expanded) {
                Canvas(
                    modifier = Modifier
                        .matchParentSize()
                ) {
                    val clipPath = Path().apply {
                        lineTo(size.width - cutCornerSize.toPx(), 0f)
                        lineTo(size.width, cutCornerSize.toPx())
                        lineTo(size.width, size.height)
                        lineTo(0f, size.height)
                        close()
                    }

                    clipPath(clipPath) {
                        drawRoundRect(
                            color = item.memo.priority.color.copy(
                                alpha =
                                if (item.memo.priority == Priority.NONE) 0.2F else 0.3F
                            ),
                            size = size,
                            cornerRadius = CornerRadius(cornerRadius.toPx())
                        )
                        drawRoundRect(
                            color = Color(
                                ColorUtils.blendARGB(
                                    item.memo.priority.color.copy(
                                        alpha =
                                        if (item.memo.priority == Priority.NONE) 0.2F else 0.3F
                                    ).toArgb(), 0x000000, 0.2f
                                )
                            ),
                            topLeft = Offset(size.width - cutCornerSize.toPx(), -100f),
                            size = Size(cutCornerSize.toPx() + 100f, cutCornerSize.toPx() + 100f),
                            cornerRadius = CornerRadius(cornerRadius.toPx())
                        )
                    }
                }
            }
            // 분기를 사용한 이유는 IntrinsicSize
            // Sub compose Layout 를 사용 하는 lazy lists, BoxWithConstraints, TabRow,
            // 가 하위에 포함 되면 안되기 때문 이다.
            if (!expanded) {
                Row(
                    modifier = Modifier.height(IntrinsicSize.Min)
                ) {
                    // 시간 + 중요성
                    Column(
                        modifier = Modifier
                            .padding(vertical = MEDIUM_PADDING + 6.dp)
                            .weight(2 / 12f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        item.memo.dueDate?.toLocalTime()?.let {
                            Text(
                                modifier = Modifier
                                    .wrapContentHeight(Alignment.CenterVertically),
                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                textAlign = TextAlign.Center,
                                text = it
                                    .format(DateTimeFormatter.ofPattern("HH:mm")),
                                color = Color(
                                    ColorUtils.blendARGB(
                                        MaterialTheme.colorScheme.onSurface.toArgb(),
                                        Color.White.toArgb(),
                                        0.1f
                                    )
                                ).copy(0.9f),
                            )
                        }
//                    Spacer(modifier = Modifier.height(4.dp))
                        Surface(color = Color.Transparent) {
                            Icon(
                                modifier = Modifier
                                    .padding(top = 4.dp, start = 4.dp)
                                    .size(16.dp),
                                imageVector = Icons.Default.Alarm,
                                contentDescription = "alarm icon",
                                tint = if (item.memo.reminderType != ReminderTime.NOT_USED &&
                                    Calendar.getInstance().timeInMillis <
                                    (item.memo.dueDate!!.toInstant()
                                        .toEpochMilli() - item.memo.reminderType.timeInMillis)
                                ) Color.Red
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                            if (item.memo.reminderType != ReminderTime.NOT_USED) {
                                Icon(
                                    modifier = Modifier
                                        .padding(top = 4.dp, start = 4.dp)
                                        .size(16.dp),
                                    imageVector = Icons.Default.Alarm,
                                    contentDescription = "alarm icon",
                                    tint = if (Calendar.getInstance().timeInMillis <
                                        (item.memo.dueDate!!.toInstant()
                                            .toEpochMilli() - item.memo.reminderType.timeInMillis)
                                    ) Color.Red
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }

                    //  제목 내용
                    Column(
                        modifier = Modifier
                            .padding(vertical = MEDIUM_PADDING)
                            .fillMaxHeight()
                            .weight(if (item.memo.progression != State.NONE) 7 / 12f else 9 / 12f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = item.memo.title,
                            color = MaterialTheme.colorScheme.taskItemContentColor,
                            style = if (item.memo.description.isNotBlank())
                                MaterialTheme.typography.bodyLarge
                            else MaterialTheme.typography.bodySmall,
                            maxLines = if (item.memo.description.isNotBlank()) 1 else 4
                        )

                        if (item.memo.description.isNotBlank()) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = item.memo.description,
                                color = MaterialTheme.colorScheme.taskItemContentColor,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            } else {
                Row(
                ) {

                    Column {
                        TaskHeader(task = item, type = TaskHeaderType.CALENDAR)
                        Column(
                            modifier = Modifier.padding(horizontal = XLARGE_PADDING)
                        ) {
                            SelectionContainer {
                                Text(
                                    modifier = Modifier
                                        .padding(SMALL_PADDING),
                                    text = item.memo.description.ifBlank { item.memo.title },
                                    lineHeight = MaterialTheme.typography.labelSmall.fontSize.times(
                                        1.4f
                                    ),
                                    fontStyle = MaterialTheme.typography.labelSmall.fontStyle,
                                    fontSize = MaterialTheme.typography.labelSmall.fontSize
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewScheduleItem() {
    TodoComposeTheme {
        ScheduleItem(
            item =
            MemoWithNotebook(
                memo = MemoTask(
                    1,
                    "필성 힘내!!!필성 힘내!!!필성 힘내!!!필성 힘내!!!필성 힘내!!!필성 힘내!!!필성 힘내!!!필성 힘내!!!",
                    "할 수 있어. 다 와 간다. 힘내자 다 할 수 있어 잘 될 거야",
                    Priority.NONE,
                    notebookId = -1,
                    dueDate = ZonedDateTime.now()
                ),
                notebook = Notebook.instance(),
                total = 1,
                photos = emptyList()
            )
        )
    }

}

@Preview
@Composable
fun PreviewScheduleListSheet() {
    MaterialTheme {
        ScheduleListSheet(
            memos = listOf(
                MemoWithNotebook(
                    memo = MemoTask(
                        1,
                        "필성 힘내!!!",
                        "할 수 있어. 다 와 간다. 힘내자 다 할 수 있어 잘 될 거야",
                        Priority.HIGH,
                        notebookId = -1,
                        dueDate = ZonedDateTime.now()
                    ),
                    notebook = Notebook.instance(),
                    total = 1,
                    photos = emptyList()
                )
            ),
            expanded = true,
            onDismissRequest = { },
        )
    }
}
