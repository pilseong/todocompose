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
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.data.repository.DataStoreRepository
import net.pilseong.todocompose.data.repository.NotebookRepository
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val notebookRepository: NotebookRepository,
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

    fun removeMultiSelectedNotebook(id: Int) {
        selectedNotebooks.remove(id)
    }

    fun deleteSelectedNotebooks() {
        viewModelScope.launch(Dispatchers.IO) {
            notebookRepository.deleteMultipleNotebooks(selectedNotebooks)
            if (selectedNotebooks.contains(notebookIdState)) persistNotebookIdState(-1)
            selectedNotebooks.clear()
        }
    }

    var notebooks = MutableStateFlow<List<Notebook>>(emptyList())
        private set

    val id = mutableStateOf(Int.MIN_VALUE)

    val title = mutableStateOf("")

    val description = mutableStateOf("")

    val priority = mutableStateOf(Priority.NONE)

    val createdAt = mutableStateOf(ZonedDateTime.now())


    fun setEditProperties() {
        val notebook = notebooks.value.find { notebook ->
            notebook.id == selectedNotebooks[0]
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
                    Log.i("PHILIP", "getNotebooks inside")
                    notebooks.value = it
                }
        }
    }

    fun observeNotebookIdChange() {
        Log.i("PHILIP", "[NoteViewModel] observeNotebookIdChange() executed")
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.readSelectedNotebookId
                .map { it }
                .collect { state ->
                    if (state != notebookIdState) {
                        notebookIdState = state
                        Log.i(
                            "PHILIP",
                            "[NoteViewModel] observeNotebookIdChange() executed with $notebookIdState"
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