package net.pilseong.todocompose.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import net.pilseong.todocompose.data.model.database.TodoDAO
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.data.model.TodoTask
import net.pilseong.todocompose.data.paging.TodoPagingSource
import net.pilseong.todocompose.util.Constants.PAGE_SIZE
import javax.inject.Inject

//@ViewModelScoped
@ActivityRetainedScoped
class TodoRepository @Inject constructor(
    private val todoDAO: TodoDAO
) {
    suspend fun getAllTasks(): List<TodoTask> {
        return todoDAO.allTasks()
    }

    fun getTasks(
        query: String,
        sortCondition: Int,
        priority: Priority = Priority.NONE,
        startDate: Long? = null,
        endDate: Long? = null,
        isFavoriteOn: Boolean = false
    ): Flow<PagingData<TodoTask>> {
        Log.i("PHILIP", "[TodoRepository] getAllTasks performed")
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            pagingSourceFactory = {
                TodoPagingSource(
                    todoDAO = todoDAO,
                    query = query,
                    sortCondition = sortCondition,
                    priority = priority,
                    startDate = startDate,
                    endDate = endDate,
                    isFavoriteOn = isFavoriteOn
                )
            }
        ).flow
    }


    suspend fun addTask(todoTask: TodoTask) {
        todoDAO.addTask(todoTask)
    }

    suspend fun updateTask(todoTask: TodoTask) {
        todoDAO.updateTaskWithTimestamp(todoTask)
    }

    suspend fun deleteTask(todoId: Int) {
        todoDAO.deleteTask(todoId)
    }

    suspend fun deleteAllTasks() {
        todoDAO.deleteAllTasks()
    }

    suspend fun updateFavorite(todoTask: TodoTask) {
        todoDAO.updateFavorite(todoTask)
    }
}