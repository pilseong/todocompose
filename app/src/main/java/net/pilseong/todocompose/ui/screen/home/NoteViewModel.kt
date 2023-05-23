package net.pilseong.todocompose.ui.screen.home

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.data.repository.NotebookRepository
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val notebookRepository: NotebookRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var notebooks = MutableStateFlow<List<Notebook>>(emptyList())
        private set

    var title = mutableStateOf("")

    var description = mutableStateOf("")


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
                    notebooks.value = it
                }
        }
    }

    fun handleActions(
        action: NoteAction,
    ) {
        Log.i(
            "PHILIP",
            "[NoteViewModel] handleActions performed with $action"
        )

        when (action) {
            NoteAction.ADD -> {
                viewModelScope.launch {
                    notebookRepository.addNotebook(
                        Notebook(id = -1, title.value, description.value, Priority.NONE)
                    )
                }
                getNotebooks()
            }

            NoteAction.DELETE -> TODO()
            NoteAction.DELETE_ALL -> TODO()
            NoteAction.UNDO -> TODO()
            NoteAction.NO_ACTION -> TODO()
        }
    }
}