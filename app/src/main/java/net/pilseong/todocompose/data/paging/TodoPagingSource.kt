package net.pilseong.todocompose.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.data.model.TodoTask
import net.pilseong.todocompose.data.model.database.TodoDAO
import net.pilseong.todocompose.util.Constants
import java.time.Instant

class TodoPagingSource(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val todoDAO: TodoDAO,
    private val query: String,
    private val searchRangeAll: Boolean = false,
    private val sortCondition: Int,
    private val priority: Priority,
    private val startDate: Long? = Instant.now().toEpochMilli(),
    private val endDate: Long? = Instant.now().toEpochMilli(),
    private val isFavoriteOn: Boolean = false,
    private val notebookId: Int = -1,
    private var stateCompleted: Boolean = true,
    private var stateActive: Boolean = true,
    private var stateSuspended: Boolean = true,
    private var stateWaiting: Boolean = true,
    private var stateNone: Boolean = true,
) : PagingSource<Int, TodoTask>() {

    override fun getRefreshKey(state: PagingState<Int, TodoTask>): Int? {
        Log.i("PHILIP", "[TodoPagingSource] state.anchorPosition params ${state.anchorPosition}")
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TodoTask> {
        Log.i("PHILIP", "[TodoPagingSource]load params ${params.key}")
        val currentPage = params.key ?: 1

        Log.i("PHILIP", "[TodoPagingSource]start: $startDate, end: $endDate")
        return try {
            withContext(ioDispatcher) {
                val todoList =
                    todoDAO.getTasks(
                        page = currentPage,
                        pageSize = Constants.PAGE_SIZE,
                        query = "%$query%",
                        searchRangeAll = searchRangeAll,
                        sortCondition = sortCondition,
                        priority = priority.name,
                        startDate = if (startDate != null) Instant.ofEpochMilli(startDate).epochSecond else 0,
                        endDate = if (endDate != null) Instant.ofEpochMilli(endDate).epochSecond else Long.MAX_VALUE,
                        favorite = isFavoriteOn,
                        notebookId = notebookId,
                        stateCompleted = stateCompleted,
                        stateActive = stateActive,
                        stateSuspended = stateSuspended,
                        stateWaiting = stateWaiting,
                        stateNone = stateNone,
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
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}