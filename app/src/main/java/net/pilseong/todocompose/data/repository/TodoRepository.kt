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
import net.pilseong.todocompose.data.model.MemoWithNotebook
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.data.model.TodoTask
import net.pilseong.todocompose.data.model.database.TodoDAO
import net.pilseong.todocompose.data.paging.TodoPagingSource
import net.pilseong.todocompose.util.Constants.PAGE_SIZE
import javax.inject.Inject

//@ViewModelScoped
@ActivityRetainedScoped
class TodoRepository @Inject constructor(
    private val todoDAO: TodoDAO
) {
    // export 를 위해 작성
    suspend fun getAllTasks(): List<TodoTask> {
        return todoDAO.allTasks()
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
        Log.i("PHILIP", "[TodoRepository] getAllTasks performed notebook_id = $notebookId")
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            pagingSourceFactory = {
                TodoPagingSource(
                    todoDAO = todoDAO,
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


    suspend fun addTask(todoTask: TodoTask) {
        withContext(Dispatchers.IO) {
            todoDAO.addTask(todoTask)
        }
    }

    suspend fun updateTask(todoTask: TodoTask) {
        withContext(Dispatchers.IO) {
            todoDAO.updateTaskWithTimestamp(todoTask)
        }
    }

    suspend fun updateTaskWithoutUpdatedAt(todoTask: TodoTask) {
        todoDAO.updateTask(todoTask)
    }


    suspend fun deleteTask(todoId: Int) {
        withContext(Dispatchers.IO) {
            todoDAO.deleteTask(todoId)
        }
    }

    suspend fun deleteAllTasks() {
        withContext(Dispatchers.IO) {
            todoDAO.deleteAllTasks()
        }
    }

    suspend fun deleteSelectedTasks(notesIds: List<Int>) {
        withContext(Dispatchers.IO) {
            todoDAO.deleteSelectedTasks(notesIds)
        }
    }

    suspend fun insertMultipleMemos(tasks: List<TodoTask>) {
        withContext(Dispatchers.IO) {
            todoDAO.insertMultipleMemos(tasks)
        }
    }

    suspend fun moveMultipleMemos(tasksIds: List<Int>, destinationNotebookId: Int) {
        withContext(Dispatchers.IO) {
            todoDAO.updateMultipleNotebookIds(tasksIds, destinationNotebookId)
        }
    }

    fun getMemoCount(notebookId: Int): Flow<DefaultNoteMemoCount> =
        todoDAO.getMemoCount(notebookId)

}