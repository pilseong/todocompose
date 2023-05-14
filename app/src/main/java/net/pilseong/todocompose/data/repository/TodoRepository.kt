package net.pilseong.todocompose.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import net.pilseong.todocompose.data.TodoDAO
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.data.model.TodoTask
import net.pilseong.todocompose.data.paging.PriorityPagingSource
import net.pilseong.todocompose.data.paging.SearchTodoPagingSource
import net.pilseong.todocompose.data.paging.TodoPagingSource
import net.pilseong.todocompose.util.Constants.PAGE_SIZE
import javax.inject.Inject

@ViewModelScoped
class TodoRepository @Inject constructor(
    private val todoDAO: TodoDAO
) {

    //    val getAllTasks: Flow<List<TodoTask>> = todoDAO.getAllTasks()
//    val sortByLowPriority: Flow<List<TodoTask>> = todoDAO.sortByLowPriority()

//    val sortByHighPriority: Flow<List<TodoTask>> = todoDAO.sortByHighPriority()

    fun sortByPriority(priority: Priority): Flow<PagingData<TodoTask>> {
        Log.i("PHILIP", "[TodoRepository] sortByPriority performed with $priority")
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            pagingSourceFactory = {
                PriorityPagingSource(
                    todoDAO = todoDAO,
                    priority = priority
                )
            }
        ).flow
    }

    fun getAllTasks(
        query: String,
        sortCondition: Int,
        priority: Priority = Priority.NONE
    ): Flow<PagingData<TodoTask>> {
        Log.i("PHILIP", "[TodoRepository] getAllTasks performed")
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            pagingSourceFactory = {
                TodoPagingSource(
                    todoDAO = todoDAO,
                    query = query,
                    sortCondition = sortCondition,
                    priority = priority
                )
            }
        ).flow
    }


//    fun getSelectedTask(taskId: Int): TodoTask {
//        return todoDAO.getSelectedTask(taskId)
//    }

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

    fun searchTasks(searchQuery: String): Flow<PagingData<TodoTask>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            pagingSourceFactory = {
                SearchTodoPagingSource(
                    todoDAO = todoDAO,
                    query = searchQuery
                )
            }
        ).flow
    }
}