package net.pilseong.todocompose.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import net.pilseong.todocompose.data.model.DefaultNoteMemoCount
import net.pilseong.todocompose.data.model.MemoTask
import net.pilseong.todocompose.data.model.MemoWithNotebook
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.data.model.State
import net.pilseong.todocompose.data.model.database.MemoDAO
import net.pilseong.todocompose.data.paging.TodoPagingSource
import net.pilseong.todocompose.util.Constants.PAGE_SIZE
import javax.inject.Inject

//@ViewModelScoped
@ActivityRetainedScoped
class TodoRepository @Inject constructor(
    private val memoDAO: MemoDAO
) {
    // export 를 위해 작성
    suspend fun getAllTasks(): List<MemoTask> {
        return memoDAO.allMemos()
    }

    fun getTasks(
        query: String,
        searchRangeAll: Boolean = false,
        sortCondition: Int,
        priority: Priority = Priority.NONE,
        startDate: Long? = null,
        endDate: Long? = null,
        isFavoriteOn: Boolean = false,
        notebookId: Int = -1,
        stateCompleted: Boolean = true,
        stateCancelled: Boolean = true,
        stateActive: Boolean = true,
        stateSuspended: Boolean = true,
        stateWaiting: Boolean = true,
        stateNone: Boolean = true,
        priorityHigh: Boolean = true,
        priorityMedium: Boolean = true,
        priorityLow: Boolean = true,
        priorityNone: Boolean = true
    ): Flow<PagingData<MemoWithNotebook>> {
        Log.d("PHILIP", "[TodoRepository] getAllTasks performed notebook_id = $notebookId")
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            pagingSourceFactory = {
                TodoPagingSource(
                    memoDAO = memoDAO,
                    query = query,
                    searchRangeAll = searchRangeAll,
                    sortCondition = sortCondition,
                    priority = priority,
                    startDate = startDate,
                    endDate = endDate,
                    isFavoriteOn = isFavoriteOn,
                    notebookId = notebookId,
                    stateCompleted = stateCompleted,
                    stateCancelled = stateCancelled,
                    stateActive = stateActive,
                    stateSuspended = stateSuspended,
                    stateWaiting = stateWaiting,
                    stateNone = stateNone,
                    priorityHigh = priorityHigh,
                    priorityMedium = priorityMedium,
                    priorityLow = priorityLow,
                    priorityNone = priorityNone,
                )
            }
        ).flow
    }

    suspend fun addMemo(memo: MemoTask) {
        withContext(Dispatchers.IO) {
            memoDAO.addMemo(memo)
        }
    }

    suspend fun updateMemo(memoTask: MemoTask) {
        withContext(Dispatchers.IO) {
            memoDAO.updateTaskWithTimestamp(memoTask)
        }
    }

    suspend fun updateMemoWithoutUpdatedAt(memoTask: MemoTask) {
        withContext(Dispatchers.IO) {
            memoDAO.updateMemo(memoTask)
        }
    }


    suspend fun deleteMemo(memoId: Int) {
        withContext(Dispatchers.IO) {
            memoDAO.deleteMemo(memoId)
        }
    }

    suspend fun deleteAllMemos() {
        withContext(Dispatchers.IO) {
            memoDAO.deleteAllMemos()
        }
    }

    suspend fun deleteAllMemosInNote(notebookId: Int) {
        withContext(Dispatchers.IO) {
            memoDAO.deleteMemosInNote(notebookId)
        }
    }

    suspend fun deleteSelectedMemos(notesIds: List<Int>) {
        withContext(Dispatchers.IO) {
            memoDAO.deleteSelectedMemos(notesIds)
        }
    }

    suspend fun insertMultipleMemos(tasks: List<MemoTask>) {
        withContext(Dispatchers.IO) {
            memoDAO.insertMultipleMemos(tasks)
        }
    }

    suspend fun moveMultipleMemos(tasksIds: List<Int>, destinationNotebookId: Int) {
        withContext(Dispatchers.IO) {
            memoDAO.updateMultipleNotebookIds(tasksIds, destinationNotebookId)
        }
    }

    suspend fun copyMultipleMemosToNote(tasksIds: List<Int>, destinationNotebookId: Int) {
        withContext(Dispatchers.IO) {
            memoDAO.copyMultipleMemosToNote(tasksIds, destinationNotebookId)
        }
    }

    suspend fun updateMultipleMemosWithoutUpdatedAt(tasksIds: List<Int>, state: State) {
        withContext(Dispatchers.IO) {
            memoDAO.updateStateForMultipleMemos(tasksIds, state)
        }
    }

    fun getMemoCount(notebookId: Int): Flow<DefaultNoteMemoCount> =
        memoDAO.getMemoCount(notebookId)

}