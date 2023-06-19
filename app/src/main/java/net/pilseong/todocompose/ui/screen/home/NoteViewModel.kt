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
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.data.model.UserData
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

    var isLoading = true

//    var noteSortingOptionState by mutableStateOf(NoteSortingOption.ACCESS_AT)

    private fun observeMemoCount() {
        viewModelScope.launch {
            memoRepository.getMemoCount(-1).stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = DefaultNoteMemoCount()
            ).collectLatest {
                defaultMemoCount.value = it
            }
        }

    }

    var defaultMemoCount = mutableStateOf(DefaultNoteMemoCount())

    fun appendMultiSelectedNotebook(id: Int) {
        if (selectedNotebooks.contains(id)) {
            selectedNotebooks.remove(id)
        } else {
            selectedNotebooks.add(id)
        }
    }

    fun deleteSelectedNotebooks() {
        val userData = (uiState as UiState.Success).userData
        viewModelScope.launch(Dispatchers.IO) {
            notebookRepository.deleteMultipleNotebooks(selectedNotebooks)
            if (selectedNotebooks.contains(userData.notebookIdState)) persistNotebookIdState(-1)
            selectedNotebooks.clear()
        }
    }

    var notebooks = MutableStateFlow<List<NotebookWithCount>>(emptyList())

    val id = mutableStateOf(Int.MIN_VALUE)

    val title = mutableStateOf("")

    val description = mutableStateOf("")

    val priority = mutableStateOf(Priority.NONE)

    private val createdAt = mutableStateOf(ZonedDateTime.now())

    val currentNotebook = mutableStateOf(NotebookWithCount.instance())
    val firstRecentNotebook = mutableStateOf<NotebookWithCount?>(null)
    val secondRecentNotebook = mutableStateOf<NotebookWithCount?>(null)


    fun setEditProperties(targetId: Int) {
        val notebook = notebooks.value.find { notebook ->
            notebook.id == targetId
        }

        notebook?.let { it ->
            id.value = it.id
            title.value = it.title
            description.value = it.description
            priority.value = notebook.priority
            createdAt.value = notebook.createdAt
        }
    }

    private suspend fun getNotebooks(userData: UserData) {
        Log.i("PHILIP", "[NoteViewModel] getNotebooks() called")
        notebooks.value = notebookRepository.getNotebooks(userData.noteSortingOptionState)

        if (isLoading) isLoading = false
    }

    private fun getCurrentNote(userData: UserData) {
        Log.i(
            "PHILIP",
            "[NoteViewModel] getCurrentNote() start observing with ${userData.notebookIdState} and currentNote: $currentNotebook"
        )
        viewModelScope.launch(Dispatchers.IO) {
            if (userData.notebookIdState >= 0) {
                notebookRepository.updateAccessTime(userData.notebookIdState)
                currentNotebook.value =
                    notebookRepository.getNotebookWithCount(userData.notebookIdState)
                Log.i(
                    "PHILIP",
                    "[NoteViewModel] getCurrentNote() getNotebookWithCount execute with ${userData.notebookIdState} and currentNote: $currentNotebook"
                )
            } else {
//                val countsOfMemos = memoRepository.getMemoCount(-1)
                Log.i(
                    "PHILIP",
                    "[NoteViewModel] getCurrentNote() getMemoCount execute with ${userData.notebookIdState} and currentNote: $currentNotebook"
                )
                currentNotebook.value = NotebookWithCount.instance(
                    id = -1,
                    title = context.resources.getString(R.string.default_note_title),
                    memoCount = defaultMemoCount.value.total
                )
            }
        }
    }

    private suspend fun getFirstNote(userData: UserData) {
        Log.i(
            "PHILIP",
            "[NoteViewModel] getFirstNote() start observing with ${userData.firstRecentNotebookId} and firstNote ${firstRecentNotebook.value}"
        )
        if (userData.firstRecentNotebookId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                if (userData.firstRecentNotebookId >= 0) {
                    firstRecentNotebook.value =
                        notebookRepository.getNotebookWithCount(userData.firstRecentNotebookId)
                    Log.i(
                        "PHILIP",
                        "[NoteViewModel] getFirstNote() getNotebookWithCount execute with ${userData.firstRecentNotebookId} and firstNote ${firstRecentNotebook.value}"
                    )
                } else {
                    Log.i(
                        "PHILIP",
                        "[NoteViewModel] getFirstNote() getMemoCount execute with ${userData.firstRecentNotebookId} and firstNote ${firstRecentNotebook.value}"
                    )
                    firstRecentNotebook.value = NotebookWithCount.instance(
                        id = -1,
                        title = context.resources.getString(R.string.default_note_title),
                        memoCount = defaultMemoCount.value.total
                    )
                }
            }
        } else {
            firstRecentNotebook.value = null
        }
    }

    private suspend fun getSecondNote(userData: UserData) {
        Log.i(
            "PHILIP",
            "[NoteViewModel] getSecondNote() start observing with ${userData.secondRecentNotebookId} and secondNote: ${secondRecentNotebook.value}"
        )
        if (userData.secondRecentNotebookId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                if (userData.secondRecentNotebookId >= 0) {
                    secondRecentNotebook.value =
                        notebookRepository.getNotebookWithCount(userData.secondRecentNotebookId)
                    Log.i(
                        "PHILIP",
                        "[NoteViewModel] getSecondNote() getNotebookWithCount execute with ${userData.secondRecentNotebookId} and secondNote: ${secondRecentNotebook.value}"
                    )
                } else {
                    Log.i(
                        "PHILIP",
                        "[NoteViewModel] getSecondNote() getMemoCount execute with ${userData.secondRecentNotebookId} and secondNote: ${secondRecentNotebook.value}"
                    )
                    secondRecentNotebook.value = NotebookWithCount.instance(
                        id = -1,
                        title = context.resources.getString(R.string.default_note_title),
                        memoCount = defaultMemoCount.value.total
                    )
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
                    notebookRepository.addNotebook(
                        Notebook(
                            title = title.value,
                            description = description.value,
                            priority = priority.value
                        )
                    )
                }
            }

            NoteAction.EDIT -> {
                editNotebook()
            }

            NoteAction.SELECT_NOTEBOOK -> {
                if (userData.notebookIdState != notebookId) {
                    viewModelScope.launch {
                        if (userData.firstRecentNotebookId == null) {
                            persistFirstRecentNotebookIdState(userData.notebookIdState)
                        } else {
                            persistSecondRecentNotebookIdState(userData.firstRecentNotebookId!!)
                            persistFirstRecentNotebookIdState(userData.notebookIdState)
                        }
                        persistNotebookIdState(notebookId = notebookId)
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
        val userData = (uiState as UiState.Success).userData
        viewModelScope.launch {
            notebookRepository.updateNotebook(
                Notebook(
                    id = id.value,
                    title = title.value,
                    description = description.value,
                    priority = priority.value,
                    createdAt = createdAt.value
                )
            )

            val fetched = notebookRepository.getNotebookWithCount(id.value)

            if (userData.notebookIdState == id.value) {
                currentNotebook.value = fetched
            }

            if (userData.firstRecentNotebookId == id.value) {
                firstRecentNotebook.value = fetched
            }

            if (userData.secondRecentNotebookId == id.value) {
                secondRecentNotebook.value = fetched
            }
        }
        selectedNotebooks.clear()
    }


    private fun persistNotebookIdState(notebookId: Int) {
        viewModelScope.launch {
            Log.i("PHILIP", "[NoteViewModel] persistNotebookIdState $dataStoreRepository")
            dataStoreRepository.persistSelectedNotebookId(notebookId)
        }
    }

    private fun persistFirstRecentNotebookIdState(notebookId: Int) {
        viewModelScope.launch {
            dataStoreRepository.persistFirstRecentNotebookId(notebookId)
        }
    }

    private fun persistSecondRecentNotebookIdState(notebookId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.persistSecondRecentNotebookId(notebookId)
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
//                .onEach {
                    Log.i("PHILIP", "[NoteViewModel] observeUiState() executed with $it")
                    when (it) {
                        is UiState.Success -> {
                            uiState = it
                            getCurrentNote(it.userData)
                            getFirstNote(it.userData)
                            getSecondNote(it.userData)
                            getNotebooks(it.userData)
                        }

                        else -> {}
                    }
                }
//                .collect()
        }
    }

    init {
        observeMemoCount()
        observeUiState()

    }


    override fun onCleared() {
        super.onCleared()
        Log.i("PHILIP", "[NoteViewModel] onCleared called")
    }
}


