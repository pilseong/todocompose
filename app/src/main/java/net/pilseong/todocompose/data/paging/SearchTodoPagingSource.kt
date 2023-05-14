package net.pilseong.todocompose.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import net.pilseong.todocompose.data.TodoDAO
import net.pilseong.todocompose.data.model.TodoTask
import net.pilseong.todocompose.util.Constants.PAGE_SIZE

class SearchTodoPagingSource(
    private val todoDAO: TodoDAO,
    private val query: String
) : PagingSource<Int, TodoTask>() {

    override fun getRefreshKey(state: PagingState<Int, TodoTask>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TodoTask> {
        Log.i("PHILIP", "[SearchTodoPagingSource]search todo params ${params.key}")
        val currentPage = params.key ?: 1
        return try {
            val todoList = emptyList<TodoTask>()
//                todoDAO.searchTasks(
//                    searchQuery = query,
//                    page = currentPage,
//                    pageSize = PAGE_SIZE,
////                    datePoint = "created_at"
//                )
            Log.i("PHILIP", "[SearchTodoPagingSource]size of todos ${todoList.size}")

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