package net.pilseong.todocompose.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import net.pilseong.todocompose.data.TodoDAO
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.data.model.TodoTask
import net.pilseong.todocompose.util.Constants

class TodoPagingSource(
    private val todoDAO: TodoDAO,
    private val query: String,
    private val sortCondition: Int,
    private val priority: Priority
) : PagingSource<Int, TodoTask>() {

    override fun getRefreshKey(state: PagingState<Int, TodoTask>): Int? {
        Log.i("PHILIP", "[TodoPagingSource] state.anchorPosition params ${state.anchorPosition}")
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TodoTask> {
        Log.i("PHILIP", "[TodoPagingSource]load params ${params.key}")
        val currentPage = params.key ?: 1
        return try {
            val todoList =
                todoDAO.getTasks(
                    page = currentPage,
                    pageSize = Constants.PAGE_SIZE,
                    query = "%$query%",
                    sortCondition = sortCondition,
                    priority = priority.name
                )
            Log.i("PHILIP", "[TodoPagingSource]load size of todos ${todoList.size}")

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