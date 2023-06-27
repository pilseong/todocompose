package net.pilseong.todocompose.ui.screen.home

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import net.pilseong.todocompose.data.model.DefaultNoteMemoCount
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.NotebookWithCount
import net.pilseong.todocompose.data.repository.DataStoreRepository
import net.pilseong.todocompose.data.repository.NotebookRepository
import net.pilseong.todocompose.data.repository.TodoRepository
import net.pilseong.todocompose.ui.viewmodel.UiState
import net.pilseong.todocompose.util.NoteSortingOption
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val notebookRepository: NotebookRepository,
    private val memoRepository: TodoRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
//    var notebookIdState by mutableStateOf(-1)

    var selectedNotebooks = mutableStateListOf<Int>()
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

    fun appendMultiSelectedNotebook(id: Int) {
        if (selectedNotebooks.contains(id)) {
            selectedNotebooks.remove(id)
        } else {
            selectedNotebooks.add(id)
        }
    }

    // 선택된 노트들을 삭제한다. 삭제 시 recent에 있는 경우 같이 제거 처리 한다.
    fun deleteSelectedNotebooks() {
        val userData = (uiState as UiState.Success).userData
        viewModelScope.launch(Dispatchers.IO) {

            // recent 노트에 있는 경우 제거해 주어야 한다.
            val recentNotes = mutableListOf<String>()
            recentNotes.add(userData.notebookIdState.toString())
            if (userData.firstRecentNotebookId != null) recentNotes.add(userData.firstRecentNotebookId.toString())
            if (userData.secondRecentNotebookId != null) recentNotes.add(userData.secondRecentNotebookId.toString())
            val beforeCount = recentNotes.size
            recentNotes.removeIf { it -> selectedNotebooks.contains(it.toInt()) }
            val afterCount = recentNotes.size

            // D B에서 삭제
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


    fun setEditProperties(targetId: Int) {
        val notebook = notebooks.value.find { notebook ->
            notebook.id == targetId
        }

        notebookUserInput.value = notebook!!.copy()
    }

    private fun getNotebooksWithCount(noteSortingOption: NoteSortingOption) {
        Log.i("PHILIP", "[NoteViewModel] getNotebooksWithCount() called")
        if (notebooksJob != null) {
            notebooksJob!!.cancel()
        }

        notebooksJob = viewModelScope.launch {
            notebookRepository.getNotebooksAsFlow(noteSortingOption)
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = emptyList()
                ).collectLatest {
                    Log.i("PHILIP", "[NoteViewModel] getNotebooksWithCount() executed with $it")
                    notebooks.value = it
                    if (isLoading) {
                        delay(50)
                        isLoading = false
                    }
                }
        }

    }

    private fun getCurrentNoteAsFlow(noteId: Int) {
        Log.i(
            "PHILIP",
            "[NoteViewModel] getCurrentNoteAsFlow() start observing with $noteId and currentNote: $currentNotebook"
        )

        if (currentNoteJob != null) currentNoteJob!!.cancel()


        // 기본 노트가 아닌 경우
        currentNoteJob = viewModelScope.launch(Dispatchers.IO) {
            if (noteId >= 0) {
                notebookRepository.updateAccessTime(noteId)
                notebookRepository.getNotebookWithCountAsFlow(noteId)
                    .stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(5000),
                        initialValue = NotebookWithCount.instance()
                    ).collectLatest {
                        Log.i(
                            "PHILIP",
                            "[NoteViewModel] getCurrentNoteAsFlow() getNotebookWithCountAsFlow execute with $it and currentNote: $currentNotebook"
                        )
                        // 현재 노트북을 삭제할 경우, flow를 통해 순간적으로 null을 수신하게 된다.
                        if (it != null)
                            currentNotebook.value = it
                    }
            } else {
                memoRepository.getMemoCount(-1)
                    .stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(),
                        initialValue = DefaultNoteMemoCount()
                    ).collectLatest {
                        Log.i(
                            "PHILIP",
                            "[NoteViewModel] getCurrentNoteAsFlow() getMemoCount execute and currentNote: DefaultNoteMemoCount $it"
                        )
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

    private fun getFirstNoteAsFlow(noteId: Int?) {
        Log.i(
            "PHILIP",
            "[NoteViewModel] getFirstNote() start observing with $noteId and firstNote ${firstRecentNotebook.value}"
        )

        if (firstNoteJob != null) firstNoteJob!!.cancel()

        if (noteId != null) {
            firstNoteJob = viewModelScope.launch(Dispatchers.IO) {
                if (noteId >= 0) {
                    notebookRepository.getNotebookWithCountAsFlow(noteId)
                        .stateIn(
                            scope = viewModelScope,
                            started = SharingStarted.WhileSubscribed(5000),
                            initialValue = null
                        ).collectLatest {
                            Log.i(
                                "PHILIP",
                                "[NoteViewModel] getFirstNoteAsFlow() getNotebookWithCountAsFlow execute with ${it?.id} and firstNote ${firstRecentNotebook.value}"
                            )
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
                            Log.i(
                                "PHILIP",
                                "[NoteViewModel] getFirstNoteAsFlow() getMemoCount execute and DefaultNoteMemoCount $it"
                            )
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

    private suspend fun getSecondNoteAsFlow(noteId: Int?) {
        Log.i(
            "PHILIP",
            "[NoteViewModel] getSecondNoteAsFlow() start observing with $noteId and secondNote: ${secondRecentNotebook.value}"
        )

        if (secondNoteJob != null) secondNoteJob!!.cancel()

        if (noteId != null) {
            secondNoteJob = viewModelScope.launch(Dispatchers.IO) {
                if (noteId >= 0) {
                    notebookRepository.getNotebookWithCountAsFlow(noteId)
                        .stateIn(
                            scope = viewModelScope,
                            started = SharingStarted.WhileSubscribed(5000),
                            initialValue = null
                        ).collectLatest {
                            Log.i(
                                "PHILIP",
                                "[NoteViewModel] getSecondNoteAsFlow() getNotebookWithCountAsFlow execute with ${it?.id} and secondNote: ${secondRecentNotebook.value}"
                            )
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
                            Log.i(
                                "PHILIP",
                                "[NoteViewModel] getSecondNoteAsFlow() getMemoCount execute and DefaultNoteMemoCount $it"
                            )
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
        notebookId: Int = -1
    ) {
        Log.i(
            "PHILIP",
            "[NoteViewModel] handleActions performed with $action, notebookId $notebookId"
        )

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
                        // 아래의 delay는 노트화면에서 리스트로 전환될 때 순간적으로 recent가 빠르게 변환되는 것을 지연하기 위한 것이다.
                        // 현재 flow로 되어 있어 클릭하는 순간 바로 데이터를 받게 되어 제어가 불가능하다.
                        delay(20)
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
            Log.i("PHILIP", "[NoteViewModel] persistNotebookIdState $dataStoreRepository")
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
        Log.i("PHILIP", "[NoteViewModel] observeUiState() called")
        viewModelScope.launch {
            uiStateFlow
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(),
                    initialValue = UiState.Loading
                )
                .collect {
                    Log.i("PHILIP", "[NoteViewModel] observeUiState() executed with $it")
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

    init {
        observeUiState()

    }


    override fun onCleared() {
        super.onCleared()
        Log.i("PHILIP", "[NoteViewModel] onCleared called")
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