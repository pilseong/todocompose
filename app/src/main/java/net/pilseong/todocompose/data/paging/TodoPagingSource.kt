package net.pilseong.todocompose.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.pilseong.todocompose.data.model.database.MemoDAO
import net.pilseong.todocompose.data.model.ui.MemoDateSortingOption
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.data.model.ui.SortOption
import net.pilseong.todocompose.util.Constants
import java.time.Instant

class TodoPagingSource(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val memoDAO: MemoDAO,
    private val query: String,
    private val searchNoFilterState: Boolean = false,
    private val searchRangeAll: Boolean = false,
    private val memoDateSortState: MemoDateSortingOption = MemoDateSortingOption.UPDATED_AT,
    private val memoOrderState: SortOption = SortOption.DESC,
    private val priority: Priority,
    private val startDate: Long? = Instant.now().toEpochMilli(),
    private val endDate: Long? = Instant.now().toEpochMilli(),
    private val isFavoriteOn: Boolean = false,
    private val notebookId: Long = -1,
    private var stateCompleted: Boolean = true,
    private var stateCancelled: Boolean = true,
    private var stateActive: Boolean = true,
    private var stateSuspended: Boolean = true,
    private var stateWaiting: Boolean = true,
    private var stateNone: Boolean = true,
    private var priorityHigh: Boolean = true,
    private var priorityMedium: Boolean = true,
    private var priorityLow: Boolean = true,
    private var priorityNone: Boolean = true
) : PagingSource<Int, MemoWithNotebook>() {

    override fun getRefreshKey(state: PagingState<Int, MemoWithNotebook>): Int? {
//        Log.d("PHILIP", "[TodoPagingSource] state.anchorPosition params ${state.anchorPosition}")
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MemoWithNotebook> {
//        Log.d("PHILIP", "[TodoPagingSource]load params ${params.key}")
        val currentPage = params.key ?: 1

//        Log.d("PHILIP", "[TodoPagingSource]start: $startDate, end: $endDate")
        return try {
            withContext(ioDispatcher) {
                val todoList =
//                    todoDAO.getTasks(
                    memoDAO.getMemosWithNotebooks(
                        page = currentPage,
                        pageSize = Constants.PAGE_SIZE,
                        query = "%$query%",
                        searchNoFilterState = searchNoFilterState,
                        searchRangeAll = searchRangeAll,
                        memoDateSortState = memoDateSortState,
                        memoOrderState = memoOrderState,
                        priority = priority.name,
                        startDate = if (startDate != null) Instant.ofEpochMilli(startDate).epochSecond else 0,
                        endDate = if (endDate != null) Instant.ofEpochMilli(endDate).epochSecond else Long.MAX_VALUE,
                        favorite = isFavoriteOn,
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
                        priorityNone = priorityNone
                    )

//                Log.d("PHILIP", "[TodoPagingSource]load size of todos ${todoList.size}")

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