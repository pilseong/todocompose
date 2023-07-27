package net.pilseong.todocompose.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.ui.UserData
import net.pilseong.todocompose.data.repository.DataStoreRepository
import net.pilseong.todocompose.data.repository.NotebookRepository
import net.pilseong.todocompose.ui.viewmodel.UiState
import javax.inject.Inject

@HiltViewModel
class MemoTaskManagerViewModel @Inject constructor(
    private val notebookRepository: NotebookRepository,
    private val dataStoreRepository: DataStoreRepository,
    @ApplicationContext private val context: Context,
): ViewModel() {

    var selectedNotebook by mutableStateOf(Notebook.instance())
    var uiState: UserData by mutableStateOf(UserData())

    private val uiStateFlow: StateFlow<UiState> =
        dataStoreRepository.userData.map {
            UiState.Success(it)
        }.stateIn(
            scope = viewModelScope,
            initialValue = UiState.Loading,
            started = SharingStarted.WhileSubscribed(5_000),
        )

    private suspend fun getNotebook(id: Long) {
        // -1 이면 기본 노트 선택 title 이 설정 되어야 하기 때문에 title 를 지정해 준다.
        selectedNotebook = if (id == -1L) Notebook.instance(
            title = context.resources.getString(R.string.default_note_title)
        )
        else notebookRepository.getNotebook(id)
    }


    private fun observeUiState() {
        Log.d("PHILIP", "[MemoViewModel] observeUiState() called")
        viewModelScope.launch {
            uiStateFlow
                .onEach {
                    when (it) {
                        is UiState.Success -> {
                            uiState = it.userData
                            // find the tasks
//                            refreshAllTasks()
                            getNotebook(uiState.notebookIdState)
                        }

                        else -> {}
                    }
                }
                .collect {
                    Log.d("PHILIP", "[MemoTaskManagerViewModel] observeUiState() executed")
                }
        }
    }

    init {
        observeUiState()
    }
}