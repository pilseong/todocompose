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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.NotebookWithCount
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.data.repository.DataStoreRepository
import net.pilseong.todocompose.data.repository.NotebookRepository
import net.pilseong.todocompose.data.repository.TodoRepository
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
        private set

    val id = mutableStateOf(Int.MIN_VALUE)

    val title = mutableStateOf("")

    val description = mutableStateOf("")

    val priority = mutableStateOf(Priority.NONE)

    private val createdAt = mutableStateOf(ZonedDateTime.now())

    val currentNotebook = mutableStateOf(NotebookWithCount.instance())
    val firstRecentNotebook = mutableStateOf<NotebookWithCount?>(null)
    val secondRecentNotebook = mutableStateOf<NotebookWithCount?>(null)

    private var firstFetch = true

    val firstRecentNotebookId = mutableStateOf<Int?>(null)
    val secondRecentNotebookId = mutableStateOf<Int?>(null)


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

    fun getNotebooks() {
        Log.i("PHILIP", "getNotebooks")
        viewModelScope.launch(Dispatchers.IO) {
            notebookRepository.getNotebooks()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.Lazily,
                    initialValue = emptyList()
                )
                .collect {
                    Log.i("PHILIP", "getNotebooks inside $it")
                    notebooks.value = it
                }
        }
    }

    fun observeFirstRecentNotebookIdChange() {
        Log.i("PHILIP", "[NoteViewModel] observeFirstRecentNotebookIdChange() executed")

        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.readFirstRecentNotebookId
                .map { it }
                .collect { state ->
                    if (state != firstRecentNotebookId.value || firstFetch) {
                        firstRecentNotebookId.value = state
                        if (firstFetch) firstFetch = false
                        if (state != null) {
                            if (state >= 0) {
                                firstRecentNotebook.value =
                                    notebookRepository.getNotebookWithCount(state)
                            } else {
                                val countsOfMemos = memoRepository.getMemoCount(-1)
                                firstRecentNotebook.value = NotebookWithCount.instance(
                                    id = -1,
                                    title = context.resources.getString(R.string.default_note_title),
                                    memoCount = countsOfMemos.total
                                )
                            }
                        } else {
                            firstRecentNotebook.value = null
                        }
                        Log.i(
                            "PHILIP",
                            "[NoteViewModel] observeRecentFirstNotebookIdChange() executed with ${firstRecentNotebookId.value} and firstNote ${firstRecentNotebook.value}"
                        )
                    }
                }
        }
    }

    fun observeSecondRecentNotebookIdChange() {
        Log.i("PHILIP", "[NoteViewModel] observeSecondRecentNotebookIdChange() executed")

        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.readSecondRecentNotebookId
                .map { it }
                .collect { state ->
                    if (state != secondRecentNotebookId.value || firstFetch) {
                        if (firstFetch) firstFetch = false
                        secondRecentNotebookId.value = state
                        if (state != null) {
                            if (state >= 0) {
                                secondRecentNotebook.value =
                                    notebookRepository.getNotebookWithCount(state)
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
                            "[NoteViewModel] observeRecentSecondNotebookIdChange() executed with ${secondRecentNotebookId.value} and secondNote: ${secondRecentNotebook.value}"
                        )
                    }
                }
        }
    }

    fun observeNotebookIdChange() {
        Log.i("PHILIP", "[NoteViewModel] observeNotebookIdChange() executed")

        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.readSelectedNotebookId
                .map { it }
                .collect { state ->
                    if (state != notebookIdState || firstFetch) {
                        if (firstFetch) firstFetch = false
                        notebookIdState = state
                        if (state >= 0) {
                            currentNotebook.value = notebookRepository.getNotebookWithCount(state)
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
                            "[NoteViewModel] observeNotebookIdChange() executed with $notebookIdState and currentNote: $currentNotebook"
                        )
                    }
                }
        }
    }


    fun handleActions(
        action: NoteAction,
        notebookId: Int = -1
    ) {
        Log.i(
            "PHILIP",
            "[NoteViewModel] handleActions performed with $action"
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
                if (notebookIdState != notebookId) {
                    viewModelScope.launch {
                        persistNotebookIdState(notebookId = notebookId)
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
            selectedNotebooks.clear()
        }
    }

    private fun persistNotebookIdState(notebookId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.persistSelectedNotebookId(notebookId)
        }
    }
}