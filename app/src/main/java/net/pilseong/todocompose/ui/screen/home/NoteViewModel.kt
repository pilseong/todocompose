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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.pilseong.todocompose.R
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
    var notebookIdState by mutableStateOf(-1)

    var selectedNotebooks = mutableStateListOf<Int>()

    var noteSortingOptionState by mutableStateOf(NoteSortingOption.ACCESS_AT)


    fun appendMultiSelectedNotebook(id: Int) {
        if (selectedNotebooks.contains(id)) {
            selectedNotebooks.remove(id)
        } else {
            selectedNotebooks.add(id)
        }
    }

    fun deleteSelectedNotebooks() {
        viewModelScope.launch(Dispatchers.IO) {
            notebookRepository.deleteMultipleNotebooks(selectedNotebooks)
            if (selectedNotebooks.contains(notebookIdState)) persistNotebookIdState(-1)
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

    var firstFetch = true


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

    private fun getNotebooks() {
        Log.i("PHILIP", "[NoteViewModel] getNotebooks() called")
        viewModelScope.launch {
            notebookRepository.getNotebooks(noteSortingOptionState)
                .collect() {
                    Log.i(
                        "PHILIP",
                        "[NoteViewModel] getNotebooks() executed $noteSortingOptionState"
                    )
                    notebooks.value = it
                }
        }
    }

    private suspend fun getCurrentNote(userData: UserData) {
        if (userData.notebookIdState >= 0) {
            notebookRepository.updateAccessTime(userData.notebookIdState)
            currentNotebook.value =
                notebookRepository.getNotebookWithCount(userData.notebookIdState)
        } else {
            val countsOfMemos = memoRepository.getMemoCount(-1)
            currentNotebook.value = NotebookWithCount.instance(
                id = -1,
                title = context.resources.getString(R.string.default_note_title),
                memoCount = countsOfMemos.total
            )
        }
        Log.i(
            "PHILIP",
            "[NoteViewModel] observeNotebookIdChange() executed with ${userData.notebookIdState} and currentNote: $currentNotebook"
        )
    }

    private suspend fun getFirstNote(userData: UserData) {
        if (userData.firstRecentNotebookId != null) {
            if (userData.firstRecentNotebookId >= 0) {
                firstRecentNotebook.value =
                    withContext(Dispatchers.IO) {
                        notebookRepository.getNotebookWithCount(userData.firstRecentNotebookId)
                    }
            } else {
                val countsOfMemos = memoRepository.getMemoCount(-1)
                withContext(Dispatchers.IO) {
                    firstRecentNotebook.value = NotebookWithCount.instance(
                        id = -1,
                        title = context.resources.getString(R.string.default_note_title),
                        memoCount = countsOfMemos.total
                    )
                }
            }
        } else {
            firstRecentNotebook.value = null
        }
        Log.i(
            "PHILIP",
            "[NoteViewModel] observeRecentFirstNotebookIdChange() executed with ${userData.firstRecentNotebookId} and firstNote ${firstRecentNotebook.value}"
        )
    }

    private suspend fun getSecondNote(userData: UserData) {
        if (userData.secondRecentNotebookId != null) {
            if (userData.secondRecentNotebookId >= 0) {
                secondRecentNotebook.value =
                    notebookRepository.getNotebookWithCount(userData.secondRecentNotebookId)
            } else {
                val countsOfMemos = memoRepository.getMemoCount(-1)
                secondRecentNotebook.value = NotebookWithCount.instance(
                    id = -1,
                    title = context.resources.getString(R.string.default_note_title),
                    memoCount = countsOfMemos.total
                )
            }
        } else {
            secondRecentNotebook.value = null
        }
        Log.i(
            "PHILIP",
            "[NoteViewModel] observeRecentSecondNotebookIdChange() executed with ${userData.secondRecentNotebookId} and secondNote: ${secondRecentNotebook.value}"
        )
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
                if (uiState.notebookIdState != notebookId) {
                    viewModelScope.launch {
                        if (uiState.firstRecentNotebookId == null) {
                            persistFirstRecentNotebookIdState(uiState.notebookIdState)
                        } else {
                            persistSecondRecentNotebookIdState(uiState.firstRecentNotebookId!!)
                            persistFirstRecentNotebookIdState(uiState.notebookIdState)
                        }
                        persistNotebookIdState(notebookId = notebookId)
                    }
                }
            }

            NoteAction.SORT_BY_TIME -> {
                if (noteSortingOptionState != noteSortingOption) {
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
                Notebook(
                    id = id.value,
                    title = title.value,
                    description = description.value,
                    priority = priority.value,
                    createdAt = createdAt.value
                )
            )

            val fetched = notebookRepository.getNotebookWithCount(id.value)
            if (notebookIdState == id.value) {
                currentNotebook.value = fetched
            }

            if (uiState.firstRecentNotebookId == id.value) {
                firstRecentNotebook.value = fetched
            }

            if (uiState.secondRecentNotebookId == id.value) {
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

    var uiState: UserData by mutableStateOf(UserData())

    private val uiStateFlow: StateFlow<UiState> =
        dataStoreRepository.userData.map {
            UiState.Success(it)
        }.stateIn(
            scope = viewModelScope,
            initialValue = UiState.Loading,
            started = SharingStarted.WhileSubscribed(5_000),
        )


    fun observeUiState() {
        Log.i("PHILIP", "[NoteViewModel] observeUiState() executed")
        firstFetch = false
        viewModelScope.launch {
            uiStateFlow
                .onEach {
                    when (it) {
                        is UiState.Success -> {
                            uiState = it.userData
                            getCurrentNote(it.userData)
                            getFirstNote(it.userData)
                            getSecondNote(it.userData)
                            getNotebooks()
                        }

                        else -> {}
                    }
                }
                .collect()
        }
    }
}


