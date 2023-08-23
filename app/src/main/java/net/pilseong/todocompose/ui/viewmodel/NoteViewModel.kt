package net.pilseong.todocompose.ui.viewmodel

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.pilseong.todocompose.R
import net.pilseong.todocompose.alarm.ReminderScheduler
import net.pilseong.todocompose.data.model.MemoTask
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.ui.DefaultNoteMemoCount
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.data.model.ui.NotebookWithCount
import net.pilseong.todocompose.data.repository.DataStoreRepository
import net.pilseong.todocompose.data.repository.NotebookRepository
import net.pilseong.todocompose.data.repository.TodoRepository
import net.pilseong.todocompose.ui.screen.note.NoteAction
import net.pilseong.todocompose.data.model.ui.NoteSortingOption
import net.pilseong.todocompose.data.repository.ZonedDateTypeAdapter
import java.io.File
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val notebookRepository: NotebookRepository,
    private val memoRepository: TodoRepository,
    private val reminderScheduler: ReminderScheduler,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private fun cancelNotification(id: Long) {
        reminderScheduler.cancel(id)
    }

    var selectedNotebooks = mutableStateListOf<Long>()
    var defaultNotebook = mutableStateOf<NotebookWithCount>(
        NotebookWithCount.instance(
            id = -1,
            title = context.resources.getString(R.string.default_note_title),
            description = context.resources.getString(R.string.default_note_description),
        )
    )

    var notebookUserInput = mutableStateOf(NotebookWithCount.instance())

    fun clearNotebookUserInput() {
        notebookUserInput.value = NotebookWithCount.instance()
    }

    var isLoading = true

    var notebooksJob: Job? = null
    var currentNoteJob: Job? = null
    var firstNoteJob: Job? = null
    var secondNoteJob: Job? = null

    fun appendMultiSelectedNotebook(id: Long) {
        if (selectedNotebooks.contains(id)) {
            selectedNotebooks.remove(id)
        } else {
            selectedNotebooks.add(id)
        }
    }

    // 선택된 노트들 을 삭제 한다. 삭제 시 recent 에 있는 경우 같이 제거 처리 한다.
    fun deleteSelectedNotebooks() {
        val userData = (uiState as UiState.Success).userData

        viewModelScope.launch(Dispatchers.IO) {

            // recent 노트에 있는 경우 제거해 주어야 한다. recentNotes 에 selected, recent 노트들 을 다 집어 넣는다.
            val recentNotes = mutableListOf<String>()

            // 저장 하는 순서가 중요 하댜
            recentNotes.add(userData.notebookIdState.toString())
            if (userData.firstRecentNotebookId != null) recentNotes.add(userData.firstRecentNotebookId.toString())
            if (userData.secondRecentNotebookId != null) recentNotes.add(userData.secondRecentNotebookId.toString())

            val beforeCount = recentNotes.size
            recentNotes.removeIf { selectedNotebooks.contains(it.toLong()) }
            val afterCount = recentNotes.size

            // 설정 된 알람 을 모두 해제 한다.
            selectedNotebooks.forEach { notebookId ->
                val memoIdsWithAlarm = memoRepository.getMemosWithAlarmByNotebookId(notebookId = notebookId)
                memoIdsWithAlarm.forEach {
                    cancelNotification(it)
                }
            }

            // DB 에서 삭제
            notebookRepository.deleteMultipleNotebooks(selectedNotebooks)

            // 삭제된 부분이 있는 경우만 persist 한다.
            if (beforeCount != afterCount) persistNotebookIdState(recentNotes)

            selectedNotebooks.clear()
        }
    }

    var notebooks = MutableStateFlow<List<NotebookWithCount>>(emptyList())

    val currentNotebook = mutableStateOf(NotebookWithCount.instance())
    val firstRecentNotebook = mutableStateOf<NotebookWithCount?>(null)
    val secondRecentNotebook = mutableStateOf<NotebookWithCount?>(null)


    fun setEditProperties(targetId: Long) {
        val notebook = notebooks.value.find { notebook ->
            notebook.id == targetId
        }

        notebookUserInput.value = notebook!!.copy()
    }

    private fun getNotebooksWithCount(noteSortingOption: NoteSortingOption) {
//        Log.d("PHILIP", "[NoteViewModel] getNotebooksWithCount() called")
        if (notebooksJob != null) {
            notebooksJob!!.cancel()
        }

        notebooksJob = viewModelScope.launch {
            // stateIn을 하면 깜박임이 생기고 에니메이션이 생성되지 않는다.
            notebookRepository.getNotebooksAsFlow(noteSortingOption)
                .collectLatest {
//                    Log.d("PHILIP", "[NoteViewModel] getNotebooksWithCount() executed with $it")
                    notebooks.value = it
                    if (isLoading) {
                        delay(50)
                        isLoading = false
                    }
                }
        }

    }

    private fun getCurrentNoteAsFlow(noteId: Long) {
        Log.d(
            "PHILIP",
            "[NoteViewModel] getCurrentNoteAsFlow() start observing with $noteId and currentNote: $currentNotebook"
        )

        if (currentNoteJob != null) currentNoteJob!!.cancel()


        // 기본 노트가 아닌 경우
        currentNoteJob = viewModelScope.launch {
            if (noteId >= 0) {
                notebookRepository.updateAccessTime(noteId)
                notebookRepository.getNotebookWithCountAsFlow(noteId)
                    .stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(5000),
                        initialValue = NotebookWithCount.instance()
                    ).collectLatest {
                        Log.d(
                            "PHILIP",
                            "[NoteViewModel] getCurrentNoteAsFlow() getNotebookWithCountAsFlow execute with $it and currentNote: $currentNotebook"
                        )
                        // 현재 노트북을 삭제할 경우, flow를 통해 순간적으로 null을 수신하게 된다.
                        if (it != null && it.id == noteId)
                            currentNotebook.value = it
                    }
            } else {
                memoRepository.getMemoCount(-1)
                    .stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(5000),
                        initialValue = DefaultNoteMemoCount()
                    ).collectLatest {
//                        Log.d(
//                            "PHILIP",
//                            "[NoteViewModel] getCurrentNoteAsFlow() getMemoCount execute and currentNote: DefaultNoteMemoCount $it"
//                        )
                        currentNotebook.value = defaultNotebook.value.copy(
                            memoTotalCount = it.total,
                            highPriorityCount = it.high,
                            mediumPriorityCount = it.medium,
                            lowPriorityCount = it.low,
                            nonePriorityCount = it.none,
                            completedCount = it.completed,
                            activeCount = it.active,
                            suspendedCount = it.suspended,
                            waitingCount = it.waiting,
                            noneCount = it.not_assigned
                        )
                        defaultNotebook.value = currentNotebook.value
                    }
            }
        }
    }

    private fun getFirstNoteAsFlow(noteId: Long?) {
//        Log.d(
//            "PHILIP",
//            "[NoteViewModel] getFirstNote() start observing with $noteId and firstNote ${firstRecentNotebook.value}"
//        )

        if (firstNoteJob != null) firstNoteJob!!.cancel()

        if (noteId != null) {
            firstNoteJob = viewModelScope.launch {
                if (noteId >= 0) {
                    notebookRepository.getNotebookWithCountAsFlow(noteId)
                        .stateIn(
                            scope = viewModelScope,
                            started = SharingStarted.WhileSubscribed(5000),
                            initialValue = null
                        ).collectLatest {
//                            Log.d(
//                                "PHILIP",
//                                "[NoteViewModel] getFirstNoteAsFlow() getNotebookWithCountAsFlow execute with ${it?.id} and firstNote ${firstRecentNotebook.value}"
//                            )
                            if (noteId == it?.id)
                                firstRecentNotebook.value = it
                        }
                } else {
                    memoRepository.getMemoCount(-1)
                        .stateIn(
                            scope = viewModelScope,
                            started = SharingStarted.WhileSubscribed(),
                            initialValue = DefaultNoteMemoCount()
                        ).collectLatest {
//                            Log.d(
//                                "PHILIP",
//                                "[NoteViewModel] getFirstNoteAsFlow() getMemoCount execute and DefaultNoteMemoCount $it"
//                            )
                            firstRecentNotebook.value = defaultNotebook.value.copy(
                                memoTotalCount = it.total,
                                highPriorityCount = it.high,
                                mediumPriorityCount = it.medium,
                                lowPriorityCount = it.low,
                                nonePriorityCount = it.none,
                                completedCount = it.completed,
                                activeCount = it.active,
                                suspendedCount = it.suspended,
                                waitingCount = it.waiting,
                                noneCount = it.not_assigned
                            )
                            defaultNotebook.value = firstRecentNotebook.value!!
                        }
                }
            }
        } else {
            firstRecentNotebook.value = null
        }
    }

    private fun getSecondNoteAsFlow(noteId: Long?) {
//        Log.d(
//            "PHILIP",
//            "[NoteViewModel] getSecondNoteAsFlow() start observing with $noteId and secondNote: ${secondRecentNotebook.value}"
//        )

        if (secondNoteJob != null) secondNoteJob!!.cancel()

        if (noteId != null) {
            secondNoteJob = viewModelScope.launch {
                if (noteId >= 0) {
                    notebookRepository.getNotebookWithCountAsFlow(noteId)
                        .stateIn(
                            scope = viewModelScope,
                            started = SharingStarted.WhileSubscribed(5000),
                            initialValue = null
                        ).collectLatest {
//                            Log.d(
//                                "PHILIP",
//                                "[NoteViewModel] getSecondNoteAsFlow() getNotebookWithCountAsFlow execute with ${it?.id} and secondNote: ${secondRecentNotebook.value}"
//                            )
                            if (noteId == it?.id)
                                secondRecentNotebook.value = it
                        }
                } else {
                    memoRepository.getMemoCount(-1)
                        .stateIn(
                            scope = viewModelScope,
                            started = SharingStarted.WhileSubscribed(),
                            initialValue = DefaultNoteMemoCount()
                        ).collectLatest {
//                            Log.d(
//                                "PHILIP",
//                                "[NoteViewModel] getSecondNoteAsFlow() getMemoCount execute and DefaultNoteMemoCount $it"
//                            )
                            secondRecentNotebook.value = defaultNotebook.value.copy(
                                memoTotalCount = it.total,
                                highPriorityCount = it.high,
                                mediumPriorityCount = it.medium,
                                lowPriorityCount = it.low,
                                nonePriorityCount = it.none,
                                completedCount = it.completed,
                                activeCount = it.active,
                                suspendedCount = it.suspended,
                                waitingCount = it.waiting,
                                noneCount = it.not_assigned
                            )

                            defaultNotebook.value = secondRecentNotebook.value!!
                        }
                }
            }

        } else {
            secondRecentNotebook.value = null
        }
    }

    fun handleActions(
        action: NoteAction,
        noteSortingOption: NoteSortingOption = NoteSortingOption.ACCESS_AT,
        notebookId: Long = -1
    ) {
//        Log.d(
//            "PHILIP",
//            "[NoteViewModel] handleActions performed with $action, notebookId $notebookId"
//        )

        val userData = (uiState as UiState.Success).userData

        when (action) {
            NoteAction.ADD -> {
                viewModelScope.launch {
                    // 실제 저장할 때 시각으로 저장해야 한다. 추가 이므로 세 시간 모두 현재로 설정
                    notebookRepository.addNotebook(
                        notebookUserInput.value.toNotebook().copy(
                            createdAt = ZonedDateTime.now(),
                            updatedAt = ZonedDateTime.now(),
                            accessedAt = ZonedDateTime.now()
                        )
                    )
                }
            }

            NoteAction.EDIT -> {
                editNotebook()
            }

            NoteAction.SELECT_NOTEBOOK -> {
                if (userData.notebookIdState != notebookId) {
                    val noteIdsList = mutableListOf<String>()
                    noteIdsList.add(notebookId.toString())
                    noteIdsList.add(userData.notebookIdState.toString())

                    if (userData.firstRecentNotebookId != null) {
                        noteIdsList.add(userData.firstRecentNotebookId.toString())
                    }

                    viewModelScope.launch {
                        // 아래의 delay 는 노트 화면 에서 리스트 로 전환될 때 순간적 으로 recent 가 빠르게 변환 되는 것을 지연 하기 위한 것이다.
                        // 현재 flow 로 되어 있어 클릭 하는 순간 바로 데이터 를 받게 되어 제어가 불가능 하다.
                        // delay 를 삭제함 flow 에서 받는 값을 비교 하여 쓸지 않 쓸지를 판단 하도록 변경 수정 완료
                        persistNotebookIdState(noteIdsList)
                    }
                }
            }

            NoteAction.SORT_BY_TIME -> {

                if (userData.noteSortingOptionState != noteSortingOption) {
                    viewModelScope.launch {
                        dataStoreRepository.persistNoteSortingOrderState(noteSortingOption = noteSortingOption)
                    }
                }
            }

            NoteAction.DELETE -> {
                deleteSelectedNotebooks()
            }

            NoteAction.DELETE_ALL -> TODO()
            NoteAction.UNDO -> TODO()
            NoteAction.NO_ACTION -> TODO()
        }
    }

    private fun editNotebook() {
        viewModelScope.launch {
            notebookRepository.updateNotebook(
                notebookUserInput.value.toNotebook()
            )
        }
        selectedNotebooks.clear()
    }


    private fun persistNotebookIdState(noteIds: List<String>) {
        viewModelScope.launch {
//            Log.d("PHILIP", "[NoteViewModel] persistNotebookIdState $dataStoreRepository")
            dataStoreRepository.persistRecentNoteIds(noteIds)
        }
    }

    var uiState: UiState by mutableStateOf(UiState.Loading)

    private val uiStateFlow: StateFlow<UiState> =
        dataStoreRepository.userData.map {
            UiState.Success(it)
        }.stateIn(
            scope = viewModelScope,
            initialValue = UiState.Loading,
            started = SharingStarted.WhileSubscribed(5_000),
        )


    private fun observeUiState() {
//        Log.d("PHILIP", "[NoteViewModel] observeUiState() called")
        viewModelScope.launch {
            uiStateFlow
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(),
                    initialValue = UiState.Loading
                )
                .collect {
//                    Log.d("PHILIP", "[NoteViewModel] observeUiState() executed with $it")
                    when (it) {
                        is UiState.Success -> {
                            uiState = it
                            getCurrentNoteAsFlow(it.userData.notebookIdState)
                            getFirstNoteAsFlow(it.userData.firstRecentNotebookId)
                            getSecondNoteAsFlow(it.userData.secondRecentNotebookId)
                            getNotebooksWithCount(it.userData.noteSortingOptionState)
                        }

                        else -> {}
                    }
                }
        }
    }

    var progressVisible by mutableStateOf(false)
    var openDialog by mutableStateOf(false)
    var infoDialogTitle by mutableStateOf(R.string.info_import_fail_title)
    var infoDialogContent by mutableStateOf(R.string.info_import_fail_content)

    private var gson = GsonBuilder()
        .registerTypeAdapter(
            ZonedDateTime::class.java,
            ZonedDateTypeAdapter()
        )
        .serializeNulls().setPrettyPrinting().create()

    fun handleImport(uri: Uri?) {
        val item = if (uri != null) context.contentResolver.openInputStream(uri) else null
        val bytes = item?.readBytes()

        if (bytes != null) {
            progressVisible = true
            val memoString = String(bytes, Charsets.UTF_8)
            val dbTables = memoString.split("pilseong")

            if (dbTables.size != 2) {
                progressVisible = false

                // 실패 대화박스 표출
                infoDialogTitle = R.string.info_import_fail_title
                infoDialogContent = R.string.info_import_fail_content
                openDialog = true
                Log.d("PHILIP", "error while parsing tables")
                item.close()
                return
            }

            try {
                val memoListType = object : TypeToken<List<MemoTask>>() {}.type
                val noteListType = object : TypeToken<List<Notebook>>() {}.type

                val memos = gson.fromJson<List<MemoTask>>(dbTables[0], memoListType)
                val notes = gson.fromJson<List<Notebook>>(dbTables[1], noteListType)

                Log.d(
                    "PHILIP",
                    "[MemoViewModel] handleImport uri: $uri, size of data: ${memos.size} ${memos[0]}"

                )

                viewModelScope.launch {
                    try {
                        memoRepository.insertMultipleMemos(memos)
                        notebookRepository.insertMultipleNotebooks(notes)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
//                    delay(1000)
//                    refreshAllTasks()

                }
            } catch (e: JsonParseException) {
                progressVisible = false

                infoDialogTitle = R.string.info_import_fail_title
                infoDialogContent = R.string.info_import_fail_content
                openDialog = true

                Log.d("PHILIP", "error while importing ${e.message}")
                item.close()
                return
            }
        }
        item?.close()
    }

    fun exportData() {
        viewModelScope.launch {
            val allMemoData = memoRepository.getAllTasks()
            var memoJson = gson?.toJson(allMemoData)
            val filename = "idea_note.txt"

            val allNotes = notebookRepository.getAllNotebooks()
            val noteJson = gson?.toJson(allNotes)

            memoJson += "pilseong$noteJson"

            context.openFileOutput(filename, Context.MODE_PRIVATE).use {
                it.write(memoJson?.toByteArray())
            }

            sendEmail(
                FileProvider.getUriForFile(
                    context,
                    "net.pilseong.fileprovider",
                    File(context.filesDir, filename)
                )
            )
        }
    }

    private fun sendEmail(file: Uri) {
        Log.d("PHILIP", "Sending Log ...###### ")
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "message/rfc822"
        intent.putExtra(Intent.EXTRA_SUBJECT, "Export Memos")
        intent.putExtra(Intent.EXTRA_STREAM, file)
        intent.putExtra(
            Intent.EXTRA_TEXT,
            "This is an autogenerated message \n The attachment file includes all the memos in JSON format"
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)  //  외부 에서 열려고 할 때
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)  // provider 를 통한 파일 제공
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

        intent.resolveActivity(context.packageManager)?.let {
            try {
                context.startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                Log.d("PHILIP", "No Intent matcher found")
            }
        }
    }

    init {
        observeUiState()
        Log.d("PHILIP", "[NoteViewModel] observeUiState called $this")
    }


    override fun onCleared() {
        super.onCleared()
        Log.d("PHILIP", "[NoteViewModel] onCleared called")
    }
}


fun NotebookWithCount.toNotebook() = Notebook(
    id = id,
    title = title,
    description = description,
    priority = priority,
    createdAt = createdAt,
    updatedAt = updatedAt,
    accessedAt = accessedAt
)