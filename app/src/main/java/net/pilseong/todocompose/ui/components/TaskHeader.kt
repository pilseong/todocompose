package net.pilseong.todocompose.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.MemoTask
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.Photo
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.data.model.ui.ReminderType
import net.pilseong.todocompose.data.model.ui.State
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.TodoComposeTheme
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

enum class TaskHeaderType {
    VIEWER,
    CALENDAR
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TaskHeader(
    task: MemoWithNotebook,
    type: TaskHeaderType = TaskHeaderType.VIEWER
) {
    var photoOpen by remember {
        mutableStateOf(false)
    }

    var selectedGalleryImage: Photo? by remember {
        mutableStateOf(null)
    }

    Surface(
        color = if (task.memo.priority == Priority.NONE) MaterialTheme.colorScheme.surface else
            task.memo.priority.color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(
                    start = XLARGE_PADDING,
                    end = XLARGE_PADDING,
                    bottom = SMALL_PADDING,
                    top = SMALL_PADDING
                )
                .fillMaxWidth()
        ) {
            // 헤더 부분
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1.5F / 12)) {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = "Localized description",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8F)
                    )
                    if (task.memo.reminderType != ReminderType.NOT_USED) {
                        Icon(
                            Icons.Filled.Alarm,
                            contentDescription = "Localized description",
                            tint = if (Calendar.getInstance().timeInMillis <
                                (task.memo.dueDate!!.toInstant()
                                    .toEpochMilli() - task.memo.reminderType.timeInMillis)
                            ) Color.Red
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                        )
                    }

//                        Icon(
//                            imageVector = if (expanded) Icons.Filled.ArrowDropDown else Icons.Default.ArrowDropUp,
//                            contentDescription = "Localized description",
//                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8F)
//                        )
                }
                Column(modifier = Modifier.weight(3.5F / 12)) {
                    Column {
                        Text(
                            text = stringResource(id = R.string.task_content_notebook_name),
                            fontSize = MaterialTheme.typography.labelSmall.fontSize,
                            lineHeight = 20.sp
                        )
                        if (type != TaskHeaderType.CALENDAR) {
                            Text(
                                stringResource(id = R.string.badge_priority_label),
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                lineHeight = 20.sp
                            )
                        }
                        if (task.memo.dueDate != null) {
                            Text(
                                stringResource(id = R.string.info_reminder),
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                lineHeight = 20.sp
                            )
                            Text(
                                stringResource(id = R.string.info_due_date),
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                lineHeight = 20.sp
                            )

                        }
                        if (type != TaskHeaderType.CALENDAR) {
                            Text(
                                text = stringResource(id = R.string.info_created_at),
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                lineHeight = 20.sp,
                            )
                            Text(
                                text = stringResource(id = R.string.info_updated_at),
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                lineHeight = 20.sp
                            )
                            if (task.memo.finishedAt != null) {
                                Text(
                                    stringResource(id = R.string.info_finished_at),
                                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                        if (task.memo.progression != State.NONE) {
                            Text(
                                stringResource(id = R.string.badge_state_label),
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier.weight(7F / 12),
                    horizontalAlignment = Alignment.End
                ) {
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            fontSize = MaterialTheme.typography.labelSmall.fontSize,
                            text = task.notebook?.title
                                ?: stringResource(id = R.string.default_note_title),
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 20.sp,
                            maxLines = 1
                        )
                        if (type != TaskHeaderType.CALENDAR) {
                            Text(
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                text = stringResource(id = task.memo.priority.label),
                                lineHeight = 20.sp
                            )
                        }
                        if (task.memo.dueDate != null) {
                            Text(
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                text = stringResource(id = task.memo.reminderType.label),
                                lineHeight = 20.sp
                            )
                            Text(
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                text = task.memo.dueDate.toLocalDateTime()
                                    .format(
                                        DateTimeFormatter.ofPattern(
                                            stringResource(id = R.string.task_content_dateformat)
                                        )
                                    ),
                                lineHeight = 20.sp
                            )
                        }
                        if (type != TaskHeaderType.CALENDAR) {
                            Text(
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                text = task.memo.createdAt.toLocalDateTime()
                                    .format(
                                        DateTimeFormatter.ofPattern(
                                            stringResource(id = R.string.task_content_dateformat)
                                        )
                                    ),
                                lineHeight = 20.sp
                            )
                            Text(
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                text = task.memo.updatedAt.toLocalDateTime()
                                    .format(
                                        DateTimeFormatter.ofPattern(
                                            stringResource(id = R.string.task_content_dateformat)
                                        )
                                    ),
                                lineHeight = 20.sp
                            )
                            if (task.memo.finishedAt != null) {
                                Text(
                                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                    text = task.memo.finishedAt!!.toLocalDateTime()
                                        .format(
                                            DateTimeFormatter.ofPattern(
                                                stringResource(id = R.string.task_content_dateformat)
                                            )
                                        )
                                )
                            }
                        }
                        if (task.memo.progression != State.NONE) {
                            Text(
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                text = stringResource(id = task.memo.progression.label),
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
            if (task.memo.description.isNotBlank()) {
                // 제목
                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .fillMaxWidth()
                        .padding(top = SMALL_PADDING),
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1.5F / 12),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            modifier = Modifier.padding(top = 2.dp),
                            imageVector = Icons.Filled.Title,
                            contentDescription = "Localized description",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8F)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(10.5F / 12),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = task.memo.title,
                            lineHeight = (if (type == TaskHeaderType.CALENDAR)
                                MaterialTheme.typography.bodySmall.fontSize
                            else MaterialTheme.typography.bodyLarge.fontSize).times(1.4f),
                            fontSize =
                            if (type == TaskHeaderType.CALENDAR)
                                MaterialTheme.typography.bodySmall.fontSize
                            else MaterialTheme.typography.bodyLarge.fontSize,
                            fontStyle = MaterialTheme.typography.titleSmall.fontStyle
                        )
                    }

                }
            }

            // 사진
            if (task.photos.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = SMALL_PADDING),
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Column(
                        modifier = Modifier.weight(1.4F / 12),
                    ) {
                        Icon(
                            modifier = Modifier.padding(top = 2.dp),
                            imageVector = Icons.Filled.Photo,
                            contentDescription = "Localized description",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8F)
                        )
                    }
                    Column(modifier = Modifier.weight(10.6F / 12)) {
                        ComposeGallery(
                            photos = task.photos,
                            onAddClicked = { /*TODO*/ },
                            onImageClicked = {
                                selectedGalleryImage = it
                                photoOpen = true
                            },
                            onCameraClicked = {},
                            onImagesSelected = {}
                        )
                    }
                }
            }
        }
    }

    PhotoViewer(
        photoOpen = photoOpen,
        selectedGalleryImage = selectedGalleryImage,
        task = task,
        onDismiss = {
            photoOpen = false
            selectedGalleryImage = null
        }
    )
}



@Preview
@Composable
fun PreviewTaskHeader() {
    TodoComposeTheme {
        TaskHeader(
            task =
            MemoWithNotebook(
                memo = MemoTask(
                    1,
                    "필성 힘내!!!",
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