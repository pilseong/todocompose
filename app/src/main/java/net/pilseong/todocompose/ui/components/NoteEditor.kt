package net.pilseong.todocompose.ui.components

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import net.pilseong.todocompose.MainActivity
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.Photo
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.data.model.ui.NotebookWithCount
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.data.model.ui.ReminderType
import net.pilseong.todocompose.data.model.ui.State
import net.pilseong.todocompose.ui.screen.task.CameraView
import net.pilseong.todocompose.ui.theme.ALPHA_MEDIUM
import net.pilseong.todocompose.ui.theme.PRIORITY_DROPDOWN_HEIGHT
import net.pilseong.todocompose.ui.theme.XLARGE_PADDING
import net.pilseong.todocompose.ui.viewmodel.TaskDetails
import net.pilseong.todocompose.ui.viewmodel.TaskUiState
import net.pilseong.todocompose.util.Constants
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


enum class NoteEditorMode {
    ADD,
    EDIT,
    CALENDAR_ADD
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalPagerApi::class
)
@Composable
fun NoteEditor(
    mode: NoteEditorMode = NoteEditorMode.ADD,
    task: MemoWithNotebook = MemoWithNotebook.instance(), // edit 모드 시에 데이터 를 보여 주기 위함
    taskUiState: TaskUiState, // 실제 입력 정보를 기록
    notebooks: List<NotebookWithCount> = emptyList(), // 선택할 모트북 목록을 보여 준다.
    notebook: Notebook, // 메모장 선택 시에 현재 선택된 메모장을 초기값으로 설정하기 위한
    onValueChange: (TaskDetails) -> Unit,   // 입력 값 변화에 따른 설정
) {

    Log.d("PHILIP", "[NoteEditor] taskUiState $taskUiState")
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .weight(7.5F / 12F)
            ) {
                Surface(tonalElevation = 1.dp) {
                    if (mode != NoteEditorMode.CALENDAR_ADD) {
                        NotebooksDropDown(
                            notebooks = notebooks,
                            notebookTitle = if (taskUiState.taskDetails.id == Constants.NEW_ITEM_ID) notebook.title else task.notebook?.title,
                            onNotebookSelected = {
                                onValueChange(
                                    taskUiState.taskDetails.copy(
                                        notebookId = it
                                    )
                                )
                            }
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(
                                modifier = Modifier.padding(start = XLARGE_PADDING),
                                text = notebook.title,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
            Row(modifier = Modifier.weight(4.5F / 12F)) {
                Surface(tonalElevation = 1.dp) {
                    PriorityDropDown(
                        isNew = taskUiState.taskDetails.id == Constants.NEW_ITEM_ID,
                        priority = taskUiState.taskDetails.priority,
                        onPrioritySelected = {
                            onValueChange(taskUiState.taskDetails.copy(priority = it))
                        }
                    )
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .height(0.7.dp),
            color = MaterialTheme.colorScheme.onSurface,
        )


        // task switch 를 해제할 때만 실행 되는 부분
        LaunchedEffect(key1 = taskUiState.taskDetails.isTask) {
            if (!taskUiState.taskDetails.isTask && mode != NoteEditorMode.CALENDAR_ADD) {
                onValueChange(
                    taskUiState.taskDetails.copy(
                        progression = State.NONE,
                        dueDate = null,
                        reminderType = ReminderType.NOT_USED
                    )
                )
            }
        }

        if (taskUiState.taskDetails.isTask || mode == NoteEditorMode.CALENDAR_ADD) {

            Surface(tonalElevation = 1.dp) {
                Row {
                    Row(modifier = Modifier.weight(1f)) {
                        StatusDropDown(
                            isNew = taskUiState.taskDetails.id == Constants.NEW_ITEM_ID,
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
                        var expanded by remember { mutableStateOf(false) }
                        val focusManager = LocalFocusManager.current
                        val angle by animateFloatAsState(
                            targetValue = if (expanded) 180F else 0F, label = "expand icon"
                        )
                        ReminderDropDown(
                            modifier = Modifier.height(PRIORITY_DROPDOWN_HEIGHT),
                            isNew = taskUiState.taskDetails.id == Constants.NEW_ITEM_ID,
                            expanded = expanded,
                            enabled = taskUiState.taskDetails.dueDate != null,
                            targetTime = taskUiState.taskDetails.dueDate?.toInstant()
                                ?.toEpochMilli(),
                            onTimeSelected = {
                                expanded = false
                                onValueChange(
                                    taskUiState.taskDetails.copy(
                                        reminderType = it
                                    )
                                )
                            },
                            onButtonClicked = {
                                if (taskUiState.taskDetails.dueDate != null) expanded = true
                            },
                            onDismissRequest = { expanded = false }
                        ) { showInitialValue ->
                            Text(
                                modifier = Modifier
                                    .padding(start = XLARGE_PADDING)
                                    .weight(1F),
                                text = if (!showInitialValue) stringResource(id = taskUiState.taskDetails.reminderType.label)
                                else stringResource(id = R.string.edit_content_reminderlabel),
                                style = MaterialTheme.typography.titleSmall,
                                color = if (taskUiState.taskDetails.dueDate != null)
                                    MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )

                            IconButton(
                                enabled = taskUiState.taskDetails.dueDate != null,
                                modifier = Modifier
                                    .alpha(ALPHA_MEDIUM)
                                    .rotate(angle),
                                onClick = {
                                    if (taskUiState.taskDetails.dueDate != null) expanded = true
                                    focusManager.clearFocus()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = stringResource(R.string.drop_down_menu_icon),
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .height(0.7.dp),
                color = MaterialTheme.colorScheme.onSurface,
            )

            val focusManager = LocalFocusManager.current

            var showDatePicker by remember { mutableStateOf(false) }
            var showTimePicker by remember { mutableStateOf(false) }
            val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
            var timeHours by remember { mutableStateOf<Int?>(taskUiState.taskDetails.dueDate?.hour) }
            var timeMinutes by remember { mutableStateOf<Int?>(taskUiState.taskDetails.dueDate?.minute) }

            val timePickerState = rememberTimePickerState(
                initialHour = taskUiState.taskDetails.dueDate?.hour ?: 0,
                initialMinute = taskUiState.taskDetails.dueDate?.minute ?: 0,
            )
            DefaultDatePickerDialog(
                openDialog = showDatePicker,
                onConfirm = {
                    if (it != null) {
                        val instant = convertToLocalEndTime(it, true)
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
                            text = if (taskUiState.taskDetails.dueDate == null)
                                stringResource(id = R.string.memo_sorting_option_due_date)
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
                        if (mode != NoteEditorMode.CALENDAR_ADD) {
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
                                    stringResource(id = R.string.info_due_time)
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
            HorizontalDivider(
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
                if (it.length <= Constants.MAX_TITLE_LENGTH)
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
                    text = "${taskUiState.taskDetails.title.length} / ${Constants.MAX_TITLE_LENGTH}",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                )
            }
        )

//        val keyboardController = LocalSoftwareKeyboardController.current

        TextField(
            modifier = Modifier
                .imePadding()
                .navigationBarsPadding()
                .weight(1F)
                .fillMaxWidth(),
//            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
//            keyboardActions = KeyboardActions(
//                onDone = { keyboardController?.hide() }
//            ),
            value = taskUiState.taskDetails.description,
            label = {
                Text(
                    text = stringResource(id = R.string.new_task_description_placeholder)
                )
            },
            onValueChange = {
                if (it.length <= Constants.MAX_CONTENT_LENGTH)
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
                    text = "${taskUiState.taskDetails.description.length} / ${Constants.MAX_CONTENT_LENGTH}",
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

                        val initialValue = taskUiState.taskDetails.photos.indexOf(
                            selectedGalleryImage
                        )

                        Column(modifier = Modifier.fillMaxSize()) {
                            if (initialValue != -1) {
                                HorizontalPager(
                                    count = taskUiState.taskDetails.photos.size,
                                    state = rememberPagerState(
                                        initialPage = taskUiState.taskDetails.photos.indexOf(
                                            selectedGalleryImage
                                        )
                                    ),
                                    key = { taskUiState.taskDetails.photos[it].uri }
                                ) { index ->
                                    ZoomableImage(
                                        isEditMode = true,
                                        fromCamera = fromCamera,
                                        selectedGalleryImage = taskUiState.taskDetails.photos[index],
                                        onCloseClicked = {
                                            selectedGalleryImage = null
                                            photoOpen = false
                                            cameraDialog = false
                                        },
                                        onDeleteClicked = {
                                            // delete inside the gallery
                                            val photos =
                                                taskUiState.taskDetails.photos.toMutableList()
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
                                            val newList =
                                                taskUiState.taskDetails.photos.toMutableList()
                                            newList.add(selectedGalleryImage!!)

                                            onValueChange(taskUiState.taskDetails.copy(photos = newList))

                                            photoOpen = false
                                            cameraDialog = false
                                            selectedGalleryImage = null
                                        }
                                    )
                                }
                            } else {
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
}

@Preview
@Composable
fun NoteEditorPreview() {
    MaterialTheme {
        NoteEditor(
            task = MemoWithNotebook.instance(),
            mode = NoteEditorMode.CALENDAR_ADD,
            notebook = Notebook.instance(title = "Default Notebook"),
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