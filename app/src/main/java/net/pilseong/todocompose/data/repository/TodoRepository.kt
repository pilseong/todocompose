package net.pilseong.todocompose.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import net.pilseong.todocompose.data.model.MemoTask
import net.pilseong.todocompose.data.model.database.MemoDAO
import net.pilseong.todocompose.data.model.ui.DefaultNoteMemoCount
import net.pilseong.todocompose.data.model.ui.MemoDateSortingOption
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.data.model.ui.SortOption
import net.pilseong.todocompose.data.model.ui.State
import net.pilseong.todocompose.data.paging.TodoPagingSource
import net.pilseong.todocompose.ui.viewmodel.TaskDetails
import net.pilseong.todocompose.util.Constants.PAGE_SIZE
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Calendar
import javax.inject.Inject

@ViewModelScoped
//@Singleton
class TodoRepository @Inject constructor(
    private val memoDAO: MemoDAO
) {
    // export 를 위해 작성
    suspend fun getAllTasks(): List<MemoTask> {
        return memoDAO.allMemos()
    }

    fun getTasks(
        query: String,
        searchNoFilterState: Boolean = false,
        searchRangeAll: Boolean = false,
        memoDateSortState: MemoDateSortingOption = MemoDateSortingOption.UPDATED_AT,
        memoOrderState: SortOption = SortOption.DESC,
        priority: Priority = Priority.NONE,
        startDate: Long? = null,
        endDate: Long? = null,
        isFavoriteOn: Boolean = false,
        notebookId: Long = -1,
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
                    searchNoFilterState = searchNoFilterState,
                    searchRangeAll = searchRangeAll,
                    memoDateSortState = memoDateSortState,
                    memoOrderState = memoOrderState,
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


    fun getMonthlyTasks(
        yearMonth: YearMonth,
        searchRangeAll: Boolean = false,
        notebookId: Long,
    ): Flow<List<MemoWithNotebook>> {
        Log.d("PHILIP", "[TodoRepository] getMonthlyTasks performed notebook_id = $notebookId")

        val startDateTime = yearMonth.minusMonths(1).atDay(1).atStartOfDay().toInstant(
            ZonedDateTime.now(ZoneId.systemDefault()).offset
        ).epochSecond

        Log.d("PHILIP", "[TodoRepository] getMonthlyTasks start time " +
                "${ZonedDateTime.ofInstant(Instant.ofEpochSecond(startDateTime), ZoneId.systemDefault())}")


        val endDateTime = (yearMonth.plusMonths(2)
            .atDay(1).atStartOfDay().toInstant(
            ZonedDateTime.now(ZoneId.systemDefault()).offset
        ).toEpochMilli() - 1) / 1000

        Log.d("PHILIP", "[TodoRepository] getMonthlyTasks end time " +
                "${ZonedDateTime.ofInstant(Instant.ofEpochSecond(endDateTime), ZoneId.systemDefault())}")

        return memoDAO.getMonthlyTasksAsFlow(
            notebookId = notebookId,
            searchRangeAll= searchRangeAll,
            startDateTime  = startDateTime,
            endDateTime = endDateTime
        )
    }


    suspend fun addMemo(memo: MemoTask) {
        withContext(Dispatchers.IO) {
            memoDAO.addMemo(memo)
        }
    }

    suspend fun addMemo(memo: TaskDetails): Long {
        return withContext(Dispatchers.IO) {
            return@withContext memoDAO.addMemo(memo)
        }
    }

    // updatedAt 이 변경 되는 일반 업 데이트
    suspend fun updateMemo(memoTask: MemoTask) {
        withContext(Dispatchers.IO) {
            memoDAO.updateMemoWithTimestamp(memoTask)
        }
    }

    // updatedAt 이 변경 되는 일반 업 데이트, 메모 내부의 사진도 업데이트 한다.
    suspend fun updateMemo(memoTask: TaskDetails): List<Long> {
        return withContext(Dispatchers.IO) {
            return@withContext memoDAO.updateMemoWithTimestamp(memoTask)
        }
    }

    // updatedAt 을 수정 하지 않는 메모의 업 데이터, 하나의 state를 변경할 때 사용
    suspend fun updateMemoWithoutUpdatedAt(memoTask: MemoTask) {
        withContext(Dispatchers.IO) {
            memoDAO.updateMemo(memoTask)
        }
    }


    suspend fun deleteMemo(memoId: Long) {
        withContext(Dispatchers.IO) {
            memoDAO.deleteMemo(memoId)
        }
    }

    suspend fun deleteAllMemos() {
        withContext(Dispatchers.IO) {
            memoDAO.deleteAllMemos()
        }
    }

    suspend fun deleteAllMemosInNote(notebookId: Long) {
        withContext(Dispatchers.IO) {
            memoDAO.deleteMemosInNote(notebookId)
        }
    }

    suspend fun deleteSelectedMemos(notesIds: List<Long>) {
        withContext(Dispatchers.IO) {
            memoDAO.deleteSelectedMemos(notesIds)
        }
    }

    suspend fun insertMultipleMemos(tasks: List<MemoTask>) {
        withContext(Dispatchers.IO) {
            memoDAO.insertMultipleMemos(tasks)
        }
    }

    suspend fun moveMultipleMemos(tasksIds: List<Long>, destinationNotebookId: Long) {
        withContext(Dispatchers.IO) {
            memoDAO.updateMultipleNotebookIds(tasksIds, destinationNotebookId)
        }
    }

    suspend fun copyMultipleMemosToNote(tasksIds: List<Long>, destinationNotebookId: Long) {
        withContext(Dispatchers.IO) {
            memoDAO.copyMultipleMemosToNote(tasksIds, destinationNotebookId)
        }
    }

    // 여러 메모 상태의 업 데이트
    suspend fun updatesStateForMultipleMemos(tasksIds: List<Long>, state: State) {
        withContext(Dispatchers.IO) {
            memoDAO.updateStateForMultipleMemos(tasksIds, state)
        }
    }

    fun getMemoCount(notebookId: Int): Flow<DefaultNoteMemoCount> =
        memoDAO.getMemoCount(notebookId)


    suspend fun getMemosWithAlarmByNotebookId(notebookId: Long): List<Long> {
        return withContext(Dispatchers.IO) {
            return@withContext memoDAO.getMemosWithAlarmByNotebookId(notebookId = notebookId)
        }
    }
}