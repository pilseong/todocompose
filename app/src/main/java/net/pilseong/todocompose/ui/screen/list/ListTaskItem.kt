package net.pilseong.todocompose.ui.screen.list

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.ColorUtils
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.MemoTask
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.Photo
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.data.model.ui.State
import net.pilseong.todocompose.ui.components.ComposeGallery
import net.pilseong.todocompose.ui.components.ZoomableImage
import net.pilseong.todocompose.ui.theme.FavoriteYellow
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.MEDIUM_PADDING
import net.pilseong.todocompose.ui.theme.PRIORITY_INDICATOR_SIZE
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.TodoComposeTheme
import net.pilseong.todocompose.ui.theme.onPrimaryElevation
import net.pilseong.todocompose.ui.theme.taskItemContentColor
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskItem(
    modifier: Modifier = Modifier,
    index: Int = 0,
    drawEndEdge: Boolean = false,
    todoTask: MemoWithNotebook,
    toTaskScreen: (Long) -> Unit,
    headerEnabled: Boolean = true,
    datetime: ZonedDateTime,
    onFavoriteClick: () -> Unit,
    onLongClickReleased: (Long) -> Unit,
    onLongClickApplied: (Long) -> Unit,
    selectedItemsIds: SnapshotStateList<Long>,
    onStateSelected: (MemoWithNotebook, State) -> Unit,
) {

    val todoInside by rememberUpdatedState(todoTask)

    var selected = remember { mutableStateOf(selectedItemsIds.contains(todoInside.memo.id)) }
        .apply {
            value = selectedItemsIds.contains(todoInside.memo.id)
        }

    // 현재 리스트 에서 변경된 내용이 그대로 남아 있게 하기 위하여 snapshot을 변경하고 있다.
    var favoriteOn by remember { mutableStateOf(todoInside.memo.favorite) }
        .apply {
            value = todoInside.memo.favorite
        }

    var stateState by remember { mutableStateOf(todoInside.memo.progression) }
        .apply {
            value = todoInside.memo.progression
        }

    val localDensity = LocalDensity.current
    var componentHeight by remember { mutableStateOf(0.dp) }
    var stateDialogExpanded by remember { mutableStateOf(false) }

    val primaryElevation = MaterialTheme.colorScheme.onPrimaryElevation

    val drawEndEdgeState by remember {
        mutableStateOf(drawEndEdge)
    }

    val innerTime by remember { mutableStateOf(datetime) }

    Column {
        if (headerEnabled) {
            DateHeader(innerTime)
        }

        Row(modifier = modifier) {
            // item 을 감싸는 라인을 그려 준다
            Box(modifier = Modifier
                .width(10.dp)
                .height(LARGE_PADDING + componentHeight)
                .drawBehind {
                    val borderSize = 1.dp.toPx()
                    val y = size.height// - borderSize / 2
                    // 하나의 일자의 마지막 item 의 경우는 에지를 그린다
                    if (drawEndEdgeState) {
                        drawLine(
                            color = primaryElevation,
                            start = Offset(0f, y),
                            end = Offset(size.width, y), strokeWidth = borderSize
                        )
                    }
                    drawLine(
                        color = primaryElevation,
                        start = Offset(0f, 0f),
                        end = Offset(0f, y), strokeWidth = borderSize
                    )
                }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Surface(
                modifier = Modifier
                    .onGloballyPositioned { layoutPosition ->
                        componentHeight = with(localDensity) {
                            layoutPosition.size.height.toDp()
                        }
                    }
                    .combinedClickable(
                        onClick = {
                            if (selectedItemsIds.size > 0) {
                                onLongClickApplied(todoInside.memo.id)
                            } else {
                                toTaskScreen(todoInside.memo.id)
                            }
                        },
                        onLongClick = {
                            selected.value = !selected.value
                            onLongClickApplied(todoInside.memo.id)
                        }
                    ),
                shape = RoundedCornerShape(4.dp),
                tonalElevation = if (stateState == State.NONE) 0.5.dp else 0.dp,
                shadowElevation = if (stateState == State.NONE) 1.dp else 0.dp,
                color = if (selected.value) MaterialTheme.colorScheme.primaryContainer
                else if (stateState == State.NONE) MaterialTheme.colorScheme.surface
                else stateState.color.copy(alpha = 0.5F)
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = MEDIUM_PADDING)
                        .fillMaxWidth(),
//                        .height(56.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(modifier = Modifier.height(56.dp)) {
                        // 시간 + 중요성
                        Column(
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .weight(2 / 12f),
//                            .fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                modifier = Modifier
//                                .weight(6 / 12F)
//                                .fillMaxSize()
                                    .wrapContentHeight(CenterVertically),
                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                textAlign = TextAlign.Center,
                                text = innerTime.toLocalTime()
                                    .format(DateTimeFormatter.ofPattern("HH:mm")),
                                color = Color(
                                    ColorUtils.blendARGB(
                                        MaterialTheme.colorScheme.onSurface.toArgb(),
                                        Color.White.toArgb(),
                                        0.1f
                                    )
                                ).copy(0.9f),
                            )
                            Spacer(modifier = modifier.height(4.dp))
                            Icon(
                                imageVector = if (selected.value)
                                    Icons.Default.CheckCircle
                                else
                                    Icons.Default.Circle,
                                contentDescription = if (selected.value) "Checked Circle" else "Circle",
                                tint = if (selected.value) MaterialTheme.colorScheme.primary else todoInside.memo.priority.color
                            )
                        }

                        //  제목 내용
                        Column(
                            modifier = Modifier
                                .weight(7 / 12f),
                        ) {
                            Text(
                                text = todoInside.memo.title,
                                color = MaterialTheme.colorScheme.taskItemContentColor,
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 1,
                            )
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = todoInside.memo.description,
                                color = MaterialTheme.colorScheme.taskItemContentColor,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // favorite and state
                        Column(
                            modifier = Modifier
                                .padding(PaddingValues(end = SMALL_PADDING))
                                .weight(3 / 12f),
//                            .fillMaxHeight(),
                            verticalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(
                                modifier = Modifier
//                                .weight(6 / 12F)
                                    .fillMaxWidth(),
                                verticalAlignment = CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CompositionLocalProvider(
                                    LocalMinimumInteractiveComponentEnforcement provides false,
                                ) {
                                    IconButton(
                                        modifier = Modifier.size(24.dp),
                                        onClick = {
                                            Log.i("PHILIP", "test")
                                            favoriteOn = !favoriteOn
                                            onFavoriteClick()
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = stringResource(id = R.string.task_item_star_description),
                                            tint = if (favoriteOn) FavoriteYellow else Color.White
                                        )
                                    }
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .padding(top = 4.dp)
//                                .weight(6 / 12F),
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .width(80.dp)
                                        .clickable {
                                            stateDialogExpanded = !stateDialogExpanded
                                        },
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        text = stringResource(id = stateState.label),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                    )
                                }
                            }
                            // 상태를 선택할 수 있는 DropDownMenu
                            DropdownMenu(
                                expanded = stateDialogExpanded,
                                onDismissRequest = { stateDialogExpanded = false },
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
                                            stateState = state
                                            onStateSelected(todoInside, state)
                                            stateDialogExpanded = false
                                        })
                                }
                            }
                        }
                    }


                    if (todoTask.photos.isNotEmpty()) {
                        var photoOpen by remember {
                            mutableStateOf(false)
                        }

                        var selectedGalleryImage: Photo? by remember {
                            mutableStateOf(null)
                        }
                        Divider(
                            modifier = Modifier
                                .padding(top = SMALL_PADDING)
                                .height(0.5.dp),
//                        color = MaterialTheme.colorScheme.onSurface,
                        )
                        Row {
                            ComposeGallery(
                                modifier = Modifier.padding(
                                    top = MEDIUM_PADDING,
                                    start = LARGE_PADDING,
                                    end = LARGE_PADDING
                                ),
                                photos = todoTask.photos,
                                imageSize = 30.dp,
                                onImageClicked = {
                                    selectedGalleryImage = it
                                    photoOpen = true
                                },
                            )
                        }
                        if (photoOpen && selectedGalleryImage != null) {
                            Dialog(
                                onDismissRequest = {
                                    photoOpen = false
                                },
                                properties = DialogProperties(
                                    dismissOnBackPress = true,
                                    dismissOnClickOutside = false,
                                    usePlatformDefaultWidth = false
                                )
                            ) {
                                Surface(
                                    color = Color.Black
                                ) {
                                    Column(modifier = Modifier.fillMaxSize()) {
                                        ZoomableImage(
                                            selectedGalleryImage = selectedGalleryImage!!,
                                            onCloseClicked = {
                                                selectedGalleryImage = null
                                                photoOpen = false
                                            },
                                            onDeleteClicked = { },
                                            onCameraClick = {},
                                            onUseClicked = {}
                                        )
                                    }
                                }
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
fun TaskItemPreview() {
    TodoComposeTheme {
        TaskItem(
            todoTask = MemoWithNotebook(
                memo = MemoTask(
                    1, "필성 힘내!!!",
                    "할 수 있어. 다 와 간다. 힘내자 다 할 수 있어 잘 될 거야",
                    Priority.HIGH,
                    notebookId = -1,
                    progression = State.COMPLETED
                ),
                notebook = Notebook.instance(),
                total = 1,
                photos = emptyList(),
            ),
            toTaskScreen = {},
            datetime = ZonedDateTime.now(),
            onFavoriteClick = {},
            onLongClickReleased = {},
            onLongClickApplied = {},
            selectedItemsIds = SnapshotStateList(),
            onStateSelected = { _, _ -> }
        )
    }
}

