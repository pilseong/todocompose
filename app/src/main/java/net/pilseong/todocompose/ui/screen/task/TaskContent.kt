package net.pilseong.todocompose.ui.screen.task

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Card
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDismissState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import net.pilseong.todocompose.MainActivity
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.MemoTask
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.Photo
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.data.model.ui.NotebookWithCount
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.data.model.ui.ReminderTime
import net.pilseong.todocompose.data.model.ui.State
import net.pilseong.todocompose.ui.components.ComposeGallery
import net.pilseong.todocompose.ui.components.DefaultDatePickerDialog
import net.pilseong.todocompose.ui.components.DefaultTimePickerDialog
import net.pilseong.todocompose.ui.components.NotebooksDropDown
import net.pilseong.todocompose.ui.components.PriorityDropDown
import net.pilseong.todocompose.ui.components.ReminderDropDown
import net.pilseong.todocompose.ui.components.StatusDropDown
import net.pilseong.todocompose.ui.components.TaskHeader
import net.pilseong.todocompose.ui.components.TaskHeaderType
import net.pilseong.todocompose.ui.components.ZoomableImage
import net.pilseong.todocompose.ui.components.convertToLocalEndTime
import net.pilseong.todocompose.ui.theme.LARGE_PADDING
import net.pilseong.todocompose.ui.theme.MEDIUM_PADDING
import net.pilseong.todocompose.ui.theme.PRIORITY_DROPDOWN_HEIGHT
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import net.pilseong.todocompose.ui.theme.TodoComposeTheme
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import net.pilseong.todocompose.ui.viewmodel.TaskDetails
import net.pilseong.todocompose.ui.viewmodel.TaskUiState
import net.pilseong.todocompose.util.Constants.MAX_CONTENT_LENGTH
import net.pilseong.todocompose.util.Constants.MAX_TITLE_LENGTH
import net.pilseong.todocompose.util.Constants.NEW_ITEM_ID
import net.pilseong.todocompose.util.TaskAppBarState
import net.pilseong.todocompose.util.deleteFileFromUri
import net.pilseong.todocompose.util.getOutputDirectory
import net.pilseong.todocompose.util.savePhotoToInternalStorage
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskContent(
    task: MemoWithNotebook,
    notebooks: List<NotebookWithCount>,
    notebook: Notebook,
    taskUiState: TaskUiState,
    taskIndex: Int = 0,
    taskSize: Int = 0,
    taskAppBarState: TaskAppBarState = TaskAppBarState.VIEWER,
    onValueChange: (TaskDetails) -> Unit,
    onSwipeRightOnViewer: () -> Unit,
    onSwipeLeftOnViewer: () -> Unit,
) {
    Log.d("PHILIP", "size : $taskSize, taskInded : $taskIndex")
    if (taskAppBarState == TaskAppBarState.VIEWER) {

        // 화면 전환의 기준 점 계산 화면의 3분의 1이상 swipe 할 경우 전환
        val threshold = LocalConfiguration.current.screenWidthDp / 3

        val dismissState = rememberDismissState(
            confirmValueChange = {
                when (it) {
                    DismissValue.Default -> false
                    DismissValue.DismissedToEnd -> {
                        onSwipeRightOnViewer()
                        true

                    }

                    DismissValue.DismissedToStart -> {
                        onSwipeLeftOnViewer()
                        true
                    }
                }
            },
            positionalThreshold = { threshold.dp.toPx() }
        )

        // index 의 이동이 일어난 경우 실행 된다. 동일한 인덱스 로 이동 하는 경우는 없기 때문에
        // 중복 이벤트 발생에 대한 대처를 할 필요가 없다.
        // index 가 변경 된 상태 변경이 확인 되는 경우에 실행 된다.
        LaunchedEffect(key1 = taskIndex) {
            Log.d("PHILIP", "inside effect size : $taskSize, taskInded : $taskIndex")
            if (dismissState.dismissDirection == DismissDirection.StartToEnd) {
                dismissState.snapTo(DismissValue.DismissedToStart)
            } else {
                dismissState.snapTo(DismissValue.DismissedToEnd)
            }
            dismissState.reset()
        }

        SwipeToDismiss(
            state = dismissState,
            background = {},
            dismissContent = {
                ViewerContent(
                    task = task
                )
            },
            directions = getDirections(taskIndex, taskSize - 1)
        )

    } else {
        EditorContent(
            task = task,
            notebooks = notebooks,
            notebook = notebook,
            taskUiState = taskUiState,
            onValueChange = onValueChange,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun getDirections(selectedIndex: Int, endIndex: Int): Set<DismissDirection> {
    val directions = mutableSetOf(
        DismissDirection.StartToEnd, DismissDirection.EndToStart
    )

    if (selectedIndex == 0)
        directions.remove(DismissDirection.StartToEnd)

    if (selectedIndex == endIndex)
        directions.remove(DismissDirection.EndToStart)

    return directions
}

@Composable
private fun ViewerContent(
    task: MemoWithNotebook
) {

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {
        TaskHeader(task = task, type = TaskHeaderType.VIEWER)

        Divider(modifier = Modifier.height(0.2.dp))

//        if (task.memo.description.isNotEmpty()) {
        Spacer(
            modifier = Modifier.height(MEDIUM_PADDING),
        )


        Column(
        ) {
            Card(
                shape = RoundedCornerShape(4.dp)
            ) {
                SelectionContainer {
                    Text(
                        modifier = Modifier
                            .padding(LARGE_PADDING),
                        text = task.memo.description.ifBlank { task.memo.title }
                    )
                }
            }
        }
//        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorContent(
    task: MemoWithNotebook,
    taskUiState: TaskUiState,
    notebooks: List<NotebookWithCount> = emptyList(),
    notebook: Notebook,
    onValueChange: (TaskDetails) -> Unit,
) {

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxSize()
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(7.5F / 12F)) {
                Surface(tonalElevation = 1.dp) {
                    NotebooksDropDown(
                        notebooks = notebooks,
                        notebookTitle = if (taskUiState.taskDetails.id == NEW_ITEM_ID) notebook.title else task.notebook?.title,
                        onNotebookSelected = { onValueChange(taskUiState.taskDetails.copy(notebookId = it)) }
                    )
                }
            }
            Row(modifier = Modifier.weight(4.5F / 12F)) {
                Surface(tonalElevation = 1.dp) {
                    PriorityDropDown(
                        isNew = taskUiState.taskDetails.id == NEW_ITEM_ID,
                        priority = taskUiState.taskDetails.priority,
                        onPrioritySelected = { onValueChange(taskUiState.taskDetails.copy(priority = it)) }
                    )
                }
            }
        }
        Divider(
            modifier = Modifier
                .height(0.7.dp),
            color = MaterialTheme.colorScheme.onSurface,
        )


        // task switch 를 해제할 때만 실행되는 부분
        LaunchedEffect(key1 = taskUiState.taskDetails.isTask) {
            if (!taskUiState.taskDetails.isTask) {
                onValueChange(
                    taskUiState.taskDetails.copy(
                        progression = State.NONE,
                        dueDate = null,
                        reminderType = ReminderTime.NOT_USED
                    )
                )
            }
        }

        if (taskUiState.taskDetails.isTask) {

            Surface(tonalElevation = 1.dp) {
                Row {
                    Row(modifier = Modifier.weight(1f)) {
                        StatusDropDown(
                            isNew = taskUiState.taskDetails.id == NEW_ITEM_ID,
                            state = taskUiState.taskDetails.progression,
                            onStateSelected = {
                                onValueChange(
                                    taskUiState.taskDetails.copy(
                                        progression = it
                                    )
                                )
                            }
                        )
                    }
                    Row(modifier = Modifier.weight(1f)) {
                        ReminderDropDown(
                            isNew = taskUiState.taskDetails.id == NEW_ITEM_ID,
                            enabled = taskUiState.taskDetails.dueDate != null,
                            targetTime = taskUiState.taskDetails.dueDate?.toInstant()
                                ?.toEpochMilli(),
                            reminderTime = taskUiState.taskDetails.reminderType,
                            onTimeSelected = {
                                onValueChange(
                                    taskUiState.taskDetails.copy(
                                        reminderType = it
                                    )
                                )
                            }
                        )
                    }
                }
            }

            Divider(
                modifier = Modifier
                    .height(0.7.dp),
                color = MaterialTheme.colorScheme.onSurface,
            )


            val focusManager = LocalFocusManager.current

            var showDatePicker by remember { mutableStateOf(false) }
            var showTimePicker by remember { mutableStateOf(false) }
            val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
            var timeHours by remember { mutableStateOf<Int?>(null) }
            var timeMinutes by remember { mutableStateOf<Int?>(null) }

            val timePickerState = rememberTimePickerState()
            DefaultDatePickerDialog(
                openDialog = showDatePicker,
                onConfirm = { it ->
                    if (it != null) {
                        var instant = convertToLocalEndTime(it, true)
                        var offset = OffsetDateTime.ofInstant(
                            Instant.ofEpochMilli(instant!!),
                            ZoneId.systemDefault()
                        )
                        Log.d("PHILIP", "current time ${Instant.ofEpochMilli(instant)}")
                        Log.d("PHILIP", "current time $offset")

                        if (timeHours != null && timeMinutes != null) {
                            offset = offset.plusHours(timeHours!!.toLong())
                                .plusMinutes(timeMinutes!!.toLong())
                        }

                        onValueChange(
                            taskUiState.taskDetails.copy(
                                dueDate = ZonedDateTime.ofInstant(
                                    Instant.ofEpochMilli(offset.toEpochSecond() * 1000),
                                    ZoneId.systemDefault()
                                )
                            )
                        )

                        showDatePicker = false
                    }
                },
                onDismissRequest = {
                    showDatePicker = false
                }
            )

            DefaultTimePickerDialog(
                state = timePickerState,
                showTimePicker = showTimePicker,
                onConfirm = {
                    timeHours = timePickerState.hour
                    timeMinutes = timePickerState.minute

                    if (taskUiState.taskDetails.dueDate != null) {
                        var date = ZonedDateTime.of(
                            taskUiState.taskDetails.dueDate.year,
                            taskUiState.taskDetails.dueDate.monthValue,
                            taskUiState.taskDetails.dueDate.dayOfMonth,
                            0,
                            0,
                            0,
                            0,
                            ZoneId.systemDefault()
                        )
                        date =
                            date.plusHours(timeHours!!.toLong())
                                .plusMinutes(timeMinutes!!.toLong())

                        Log.d("PHILIP", "DATE FROM ${date.toOffsetDateTime()}")
                        onValueChange(taskUiState.taskDetails.copy(dueDate = date))
                    }
                    showTimePicker = false
                },
                onDismissRequest = {
                    showTimePicker = false
                }
            )

            Surface(tonalElevation = 1.dp) {
                Row {
                    Row(
                        modifier = Modifier
                            .weight(6 / 12F)
                            .height(PRIORITY_DROPDOWN_HEIGHT),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier = Modifier
                                .weight(1F)
                                .padding(start = XLARGE_PADDING),
                            text = if (taskUiState.taskDetails.dueDate == null) "마감일"
                            else {
                                taskUiState.taskDetails.dueDate
                                    .format(
                                        DateTimeFormatter.ofPattern(
                                            stringResource(id = R.string.datepicker_date_format)
                                        )
                                    )
                            },
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        IconButton(
                            onClick = {
//                            datePickerExpanded = true
                                showDatePicker = true
                                focusManager.clearFocus()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.EditCalendar,
                                contentDescription = "마감일 지정",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .weight(6 / 12F)
                            .height(PRIORITY_DROPDOWN_HEIGHT),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier = Modifier
                                .weight(1F)
                                .padding(start = XLARGE_PADDING),
                            text = if (taskUiState.taskDetails.dueDate == null) {
                                if (timeHours != null && timeMinutes != null) {
                                    val cal = Calendar.getInstance()
                                    cal.set(Calendar.HOUR_OF_DAY, timeHours!!)
                                    cal.set(Calendar.MINUTE, timeMinutes!!)
                                    cal.set(Calendar.SECOND, 0)
                                    cal.set(Calendar.MILLISECOND, 0)
                                    timeFormatter.format(cal.time)
                                } else
                                    "시간"
                            } else {
                                taskUiState.taskDetails.dueDate.toLocalDateTime()
                                    .format(
                                        DateTimeFormatter.ofPattern(
                                            "hh:mma"
                                        )
                                    )
                            },
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        IconButton(
                            onClick = {
                                showTimePicker = true
                                focusManager.clearFocus()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.LockClock,
                                contentDescription = "시간 지정",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
            Divider(
                modifier = Modifier
                    .height(0.7.dp),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding(),
            value = taskUiState.taskDetails.title,
            label = {
                Text(text = stringResource(id = R.string.new_task_title_placeholder))
            },
            onValueChange = {
                if (it.length <= MAX_TITLE_LENGTH)
                    onValueChange(taskUiState.taskDetails.copy(title = it))

            },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                    1.dp
                ),
                focusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                    1.dp
                )
            ),
            singleLine = false,
            maxLines = 3,
            supportingText = {
                Text(
                    text = "${taskUiState.taskDetails.title.length} / $MAX_TITLE_LENGTH",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                )
            }
        )

        TextField(
            modifier = Modifier
                .imePadding()
                .navigationBarsPadding()
                .weight(1F)
                .fillMaxWidth(),
            value = taskUiState.taskDetails.description,
            label = {
                Text(
                    text = stringResource(id = R.string.new_task_description_placeholder)
                )
            },
            onValueChange = {
                if (it.length <= MAX_CONTENT_LENGTH)
                    onValueChange(taskUiState.taskDetails.copy(description = it))

            },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                    1.dp
                ),
                focusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                    1.dp
                )
            ),
            supportingText = {
                Text(
                    text = "${taskUiState.taskDetails.description.length} / $MAX_CONTENT_LENGTH",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                )
            }
        )

        val context = LocalContext.current
        var cameraDialog by remember { mutableStateOf(false) }
        var cameraOpen by remember { mutableStateOf(false) }
        var photoOpen by remember { mutableStateOf(false) }
        var photoUri: Uri? by remember { mutableStateOf(null) }
        var selectedGalleryImage by remember { mutableStateOf<Photo?>(null) }
        var fromCamera by remember { mutableStateOf(false) }


        val activityResultLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                var permissionGranted = true
                permissions.entries.forEach {
                    if (it.key in MainActivity.REQUIRED_PERMISSIONS && !it.value)
                        permissionGranted = false
                }
                if (!permissionGranted) {
                    Toast.makeText(context, "Permission request denied", Toast.LENGTH_SHORT).show()
                } else {
                    cameraDialog = true
                    cameraOpen = true
                    fromCamera = true
                }
            }

        ComposeGallery(
            isEditMode = true,
            photos = taskUiState.taskDetails.photos,
            onAddClicked = { /*TODO*/ },
            onImageClicked = {
                selectedGalleryImage = it
                fromCamera = false
                photoOpen = true
                cameraDialog = true
            },
            onCameraClicked = {
                if (!MainActivity.hasPermissions(context)) {
                    activityResultLauncher.launch(MainActivity.REQUIRED_PERMISSIONS)
                } else {
                    cameraDialog = true
                    cameraOpen = true
                    fromCamera = true
                }

            },
            onImagesSelected = { images ->
                Log.d("PHILIP", "get from the picker $images")
                val newList = taskUiState.taskDetails.photos.toMutableList()

                images.forEach { image ->

                    val newUri = savePhotoToInternalStorage(context = context, uri = image)
                    if (newUri != null) {
                        Log.d("PHILIP", "back pressed")
                        newList.add(
                            Photo(
                                uri = newUri.toString(),
                                filename = "",
                                memoId = taskUiState.taskDetails.id
                            )
                        )
                    }
                }

                onValueChange(taskUiState.taskDetails.copy(photos = newList))
            }
        )

        if (cameraDialog) {
            Dialog(
                onDismissRequest = {
                    if (photoOpen && fromCamera) {
                        Log.d("PHILIP", "back pressed")
                        deleteFileFromUri(selectedGalleryImage!!.uri.toUri())
                        selectedGalleryImage = null
                        photoOpen = false
                        cameraOpen = true
                    } else {
                        cameraDialog = false
                        cameraOpen = false
                        photoOpen = false
                    }
                },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = false,
                    usePlatformDefaultWidth = false
                )
            ) {
                if (cameraOpen) {
                    CameraView(
                        context = context,
                        outputDirectory = getOutputDirectory(LocalContext.current),
                        onImageCaptured = {
                            Log.d("PHILIP", "file captured $it")
                            photoUri = it
                            selectedGalleryImage = Photo(
                                uri = it.toString(),
                                memoId = taskUiState.taskDetails.id,
                                filename = ""
                            )
                            cameraOpen = false
                            photoOpen = true
                        },
                        onDismiss = {
                            cameraDialog = false
                            cameraOpen = false
                        }
                    )
                }

                if (photoOpen && selectedGalleryImage != null) {
                    Surface(
                        color = Color.Black
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            ZoomableImage(
                                isEditMode = true,
                                fromCamera = fromCamera,
                                selectedGalleryImage = selectedGalleryImage,
                                onCloseClicked = {
                                    selectedGalleryImage = null
                                    photoOpen = false
                                    cameraDialog = false
                                },
                                onDeleteClicked = {
                                    // delete inside the gallery
                                    val photos = taskUiState.taskDetails.photos.toMutableList()
                                    photos.remove(selectedGalleryImage)
                                    onValueChange(taskUiState.taskDetails.copy(photos = photos))
                                    // delete captured image
                                    selectedGalleryImage = null
                                    photoOpen = false
                                    cameraDialog = false
                                },
                                onCameraClick = {
                                    Log.d("PHILIP", "camera clicked")
                                    photoOpen = false
                                    cameraOpen = true

                                    // delete captured image
                                    deleteFileFromUri(selectedGalleryImage!!.uri.toUri())
                                    selectedGalleryImage = null
                                },
                                onUseClicked = {
                                    val newList = taskUiState.taskDetails.photos.toMutableList()
                                    newList.add(selectedGalleryImage!!)

                                    onValueChange(taskUiState.taskDetails.copy(photos = newList))

                                    photoOpen = false
                                    cameraDialog = false
                                    selectedGalleryImage = null
                                }
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
fun ViewerContentPreview() {
    TodoComposeTheme {
        TaskContent(
            task = MemoWithNotebook(
                memo = MemoTask(
                    id = -1,
                    title = "필성 힘내!!!",
                    description = "할 수 있어. 다 와 간다. 힘내자 다 할 수 있어 잘 될 거야",
                    priority = Priority.HIGH,
                    progression = State.NONE,
                    notebookId = -1,
                ),
                notebook = Notebook.instance(),
                total = 1,
                photos = listOf(
                    Photo(0, "test", filename = "", memoId = -1L)
                ),
            ),
            taskIndex = 0,
            taskSize = 1,
            taskUiState = TaskUiState(
                taskDetails = TaskDetails(
                    id = -1,
                    title = "필성 힘내!!!",
                    description = "할 수 있어. 다 와 간다. 힘내자 다 할 수 있어 잘 될 거야",
                    priority = Priority.HIGH,
                    progression = State.NONE,
                    notebookId = -1,
                )
            ),
            onValueChange = {},
            onSwipeRightOnViewer = {},
            onSwipeLeftOnViewer = {},
            taskAppBarState = TaskAppBarState.VIEWER,
            notebooks = emptyList(),
            notebook = Notebook.instance()
        )
    }
}


@Preview
@Composable
fun EditorContentPreview() {
    MaterialTheme {
        EditorContent(
            task = MemoWithNotebook.instance(),
            notebook = Notebook.instance(),
            taskUiState = TaskUiState(
                taskDetails = TaskDetails(
                    id = -1,
                    title = "필성 힘내!!!",
                    description = "할 수 있어. 다 와 간다. 힘내자 다 할 수 있어 잘 될 거야",
                    priority = Priority.HIGH,
                    progression = State.NONE,
                    notebookId = -1,
                    isTask = true
                )
            ),
            onValueChange = {},
        )
    }
}