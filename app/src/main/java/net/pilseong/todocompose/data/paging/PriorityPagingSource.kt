package net.pilseong.todocompose.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import net.pilseong.todocompose.data.TodoDAO
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.data.model.TodoTask
import net.pilseong.todocompose.util.Constants.PAGE_SIZE

class PriorityPagingSource(
    private val todoDAO: TodoDAO,
    private val priority: Priority
) : PagingSource<Int, TodoTask>() {
    override fun getRefreshKey(state: PagingState<Int, TodoTask>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TodoTask> {
        Log.i("PHILIP", "[PriorityPagingSource]search todo params ${params.key}")
        val currentPage = params.key ?: 1
        return try {

            var todoList = emptyList<TodoTask>()
//            if (priority == Priority.HIGH) {
                todoList = todoDAO.sortByPriority(
                    page = currentPage,
                    pageSize = PAGE_SIZE,
                    priority = priority.name
                )
//            } else if (priority == Priority.LOW) {
//                todoList = todoDAO.sortByLowPriority(
//                    page = currentPage,
//                    pageSize = PAGE_SIZE
//                )
//            }

            Log.i("PHILIP", "[PriorityPagingSource]size of todos ${todoList.size}")
            if (todoList.isNotEmpty()) {
                LoadResult.Page(
                    data = todoList,
                    prevKey = if (currentPage == 1) null else currentPage - 1,
                    nextKey = currentPage + 1
                )
            } else {
                LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}