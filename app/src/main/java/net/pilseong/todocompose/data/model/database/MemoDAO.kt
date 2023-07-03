package net.pilseong.todocompose.data.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import net.pilseong.todocompose.data.model.MemoTask
import net.pilseong.todocompose.data.model.ui.DefaultNoteMemoCount
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.data.model.ui.State
import net.pilseong.todocompose.data.repository.ZonedDateTypeConverter
import java.time.ZonedDateTime

@Dao
abstract class MemoDAO {

    @Query("SELECT * FROM memo_table")
    abstract suspend fun allMemos(): List<MemoTask>

    @Transaction
    @Query(
        "SELECT *, " +
                "( SELECT COUNT(*) FROM memo_table " +
                "WHERE deleted = 0 " +
                "AND ( " +
                "       CASE :searchRangeAll " +
                "           WHEN 0 THEN notebook_id = :notebookId " +
                "           WHEN 1 THEN 1=1 " +
                "       END) " +
                "AND (title LIKE :query OR description LIKE :query) " +
                "AND (" +
                "       CASE :favorite " +
                "           WHEN 0 THEN 1=1 " +
                "           WHEN 1 THEN favorite = 1 " +
                "       END) " +
                "AND (" +
                "       CASE :stateCompleted " +
                "           WHEN 1 THEN progression = 'COMPLETED'" +
                "       END " +
                "OR " +
                "       CASE :stateCancelled " +
                "           WHEN 1 THEN progression = 'CANCELLED' " +
                "       END " +
                "OR " +
                "       CASE :stateActive " +
                "           WHEN 1 THEN progression = 'ACTIVE' " +
                "       END " +
                "OR " +
                "       CASE :stateSuspended " +
                "           WHEN 1 THEN progression = 'SUSPENDED' " +
                "       END " +
                "OR " +
                "       CASE :stateWaiting " +
                "           WHEN 1 THEN progression = 'WAITING' " +
                "       END " +
                "OR " +
                "       CASE :stateNone " +
                "           WHEN 1 THEN progression = 'NONE' " +
                "       END" +
                ") " +
                "AND (" +
                "       CASE :priorityHigh " +
                "           WHEN 1 THEN priority = 'HIGH'" +
                "       END " +
                "OR " +
                "       CASE :priorityMedium " +
                "           WHEN 1 THEN priority = 'MEDIUM' " +
                "       END " +
                "OR " +
                "       CASE :priorityLow " +
                "           WHEN 1 THEN priority = 'LOW' " +
                "       END " +
                "OR " +
                "       CASE :priorityNone " +
                "           WHEN 1 THEN priority = 'NONE' " +
                "       END " +
                ") " +
                "AND (" +
                "       CASE :sortCondition " +
                "           WHEN 0 THEN updated_at BETWEEN :startDate AND :endDate " +
                "           WHEN 1 THEN updated_at BETWEEN :startDate AND :endDate " +
                "           WHEN 2 THEN created_at BETWEEN :startDate AND :endDate " +
                "           WHEN 3 THEN created_at BETWEEN :startDate AND :endDate " +
                "       END) " +
                "ORDER BY " +
                "CASE :priority " +
                "   WHEN 'LOW' THEN " +
                "       CASE " +
                "           WHEN priority LIKE 'L%' THEN 1 " +
                "           WHEN priority LIKE 'M%' THEN 2 " +
                "           WHEN priority LIKE 'H%' THEN 3 " +
                "           WHEN priority LIKE 'N%' THEN 4 " +
                "       END " +
                "   WHEN 'HIGH' THEN" +
                "       CASE " +
                "           WHEN priority LIKE 'H%' THEN 1 " +
                "           WHEN priority LIKE 'M%' THEN 2 " +
                "           WHEN priority LIKE 'L%' THEN 3 " +
                "           WHEN priority LIKE 'N%' THEN 4 " +
                "       END " +
                "   END, " +
                "CASE WHEN :sortCondition = 0 THEN updated_at END DESC, " +
                "CASE WHEN :sortCondition = 1 THEN updated_at END ASC, " +
                "CASE WHEN :sortCondition = 2 THEN created_at END DESC, " +
                "CASE WHEN :sortCondition = 3 THEN created_at END ASC " +
                ") AS total " +
                "FROM memo_table " +
                "WHERE deleted = 0 " +
                "AND ( " +
                "       CASE :searchRangeAll " +
                "           WHEN 0 THEN notebook_id = :notebookId " +
                "           WHEN 1 THEN 1=1 " +
                "       END) " +
                "AND (title LIKE :query OR description LIKE :query) " +
                "AND (" +
                "       CASE :favorite " +
                "           WHEN 0 THEN 1=1 " +
                "           WHEN 1 THEN favorite = 1 " +
                "       END) " +
                "AND (" +
                "       CASE :stateCompleted " +
                "           WHEN 1 THEN progression = 'COMPLETED'" +
                "       END " +
                "OR " +
                "       CASE :stateCancelled " +
                "           WHEN 1 THEN progression = 'CANCELLED' " +
                "       END " +
                "OR " +
                "       CASE :stateActive " +
                "           WHEN 1 THEN progression = 'ACTIVE' " +
                "       END " +
                "OR " +
                "       CASE :stateSuspended " +
                "           WHEN 1 THEN progression = 'SUSPENDED' " +
                "       END " +
                "OR " +
                "       CASE :stateWaiting " +
                "           WHEN 1 THEN progression = 'WAITING' " +
                "       END " +
                "OR " +
                "       CASE :stateNone " +
                "           WHEN 1 THEN progression = 'NONE' " +
                "       END" +
                ") " +
                "AND (" +
                "       CASE :priorityHigh " +
                "           WHEN 1 THEN priority = 'HIGH'" +
                "       END " +
                "OR " +
                "       CASE :priorityMedium " +
                "           WHEN 1 THEN priority = 'MEDIUM' " +
                "       END " +
                "OR " +
                "       CASE :priorityLow " +
                "           WHEN 1 THEN priority = 'LOW' " +
                "       END " +
                "OR " +
                "       CASE :priorityNone " +
                "           WHEN 1 THEN priority = 'NONE' " +
                "       END " +
                ") " +
                "AND (" +
                "       CASE :sortCondition " +
                "           WHEN 0 THEN updated_at BETWEEN :startDate AND :endDate " +
                "           WHEN 1 THEN updated_at BETWEEN :startDate AND :endDate " +
                "           WHEN 2 THEN created_at BETWEEN :startDate AND :endDate " +
                "           WHEN 3 THEN created_at BETWEEN :startDate AND :endDate " +
                "       END) " +
                "ORDER BY " +
                "CASE :priority " +
                "   WHEN 'LOW' THEN " +
                "       CASE " +
                "           WHEN priority LIKE 'L%' THEN 1 " +
                "           WHEN priority LIKE 'M%' THEN 2 " +
                "           WHEN priority LIKE 'H%' THEN 3 " +
                "           WHEN priority LIKE 'N%' THEN 4 " +
                "       END " +
                "   WHEN 'HIGH' THEN" +
                "       CASE " +
                "           WHEN priority LIKE 'H%' THEN 1 " +
                "           WHEN priority LIKE 'M%' THEN 2 " +
                "           WHEN priority LIKE 'L%' THEN 3 " +
                "           WHEN priority LIKE 'N%' THEN 4 " +
                "       END " +
                "   END, " +
                "CASE WHEN :sortCondition = 0 THEN updated_at END DESC, " +
                "CASE WHEN :sortCondition = 1 THEN updated_at END ASC, " +
                "CASE WHEN :sortCondition = 2 THEN created_at END DESC, " +
                "CASE WHEN :sortCondition = 3 THEN created_at END ASC " +
                "LIMIT :pageSize OFFSET (:page - 1 ) * :pageSize"
    )
    abstract suspend fun getMemosWithNotebooks(
        page: Int,
        pageSize: Int,
        query: String,
        searchRangeAll: Boolean = false,
        sortCondition: Int = 0,
        priority: String = "HIGH",
        startDate: Long = Long.MIN_VALUE,
        endDate: Long = Long.MAX_VALUE,
        favorite: Boolean = false,
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
    ): List<MemoWithNotebook>

    // 메모 리스트 메뉴로 현재 노트북의 있는 메모 삭제
    @Query("UPDATE memo_table SET deleted = 1 WHERE notebook_id = :notebookId")
    abstract fun deleteMemosInNote(notebookId: Int)


    @Query("SELECT * FROM memo_table WHERE deleted = 0 AND id = :memoId")
    abstract fun getSelectedMemo(memoId: Int): MemoTask

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addMemo(memo: MemoTask)

    suspend fun updateTaskWithTimestamp(memo: MemoTask) =
        updateMemo(memo.copy(updatedAt = ZonedDateTime.now()))

    @Update
    abstract suspend fun updateMemo(memo: MemoTask)

    @Query("UPDATE memo_table SET deleted = 1 WHERE id = :memoId")
    abstract suspend fun deleteMemo(memoId: Int)

    @Query("UPDATE memo_table SET deleted = 1")
    abstract suspend fun deleteAllMemos()

    // 노트북 자체를 삭제 할 때 cascade 로 삭제 된다.(현재 수동 삭제)
    @Query("DELETE FROM memo_table WHERE notebook_id = :notebookId")
    abstract suspend fun deleteTasksByNotebookId(notebookId: Int)


    @Transaction
    open suspend fun deleteSelectedMemos(notesIds: List<Int>) {
        notesIds.forEach { id ->
            deleteMemo(id)
        }
    }

    @Query(
        "SELECT COUNT(*) AS total, " +
                "SUM (CASE priority WHEN 'HIGH' THEN 1 END) AS high, " +
                "SUM (CASE priority WHEN 'MEDIUM' THEN 1 END) AS medium, " +
                "SUM (CASE priority WHEN 'LOW' THEN 1 END) AS low, " +
                "SUM (CASE priority  WHEN 'NONE' THEN 1 END) AS none, " +
                "SUM (CASE progression  WHEN 'COMPLETED' THEN 1 END) AS completed, " +
                "SUM (CASE progression  WHEN 'CANCELLED' THEN 1 END) AS cancelled, " +
                "SUM (CASE progression  WHEN 'ACTIVE' THEN 1 END) AS active, " +
                "SUM (CASE progression  WHEN 'SUSPENDED' THEN 1 END) AS suspended, " +
                "SUM (CASE progression  WHEN 'WAITING' THEN 1 END) AS waiting, " +
                "SUM (CASE progression  WHEN 'NONE' THEN 1 END) AS not_assigned " +
                "FROM memo_table WHERE deleted = 0 AND notebook_id = :notebookId"
    )
    abstract fun getMemoCount(notebookId: Int): Flow<DefaultNoteMemoCount>

    @Transaction
    open suspend fun insertMultipleMemos(tasks: List<MemoTask>) {
        tasks.forEach { task ->
            addMemo(task)
        }
    }

    @Query("UPDATE memo_table SET notebook_id = :notebookId, updated_at = :updatedAt WHERE id = :memoId")
    abstract suspend fun updateNotebookId(memoId: Int, notebookId: Int, updatedAt: Long)

    @Transaction
    open suspend fun updateMultipleNotebookIds(memosIds: List<Int>, destinationNotebookId: Int) {
        memosIds.forEach { id ->
            updateNotebookId(
                memoId = id,
                notebookId = destinationNotebookId,
                updatedAt = ZonedDateTypeConverter.fromZonedDateTime(ZonedDateTime.now())!!
            )
        }
    }

    // 메모를 다른 노트로 복사 하는 기능 - 새로 생성 되는 노트의 값은 업데이트 시간만 변경 되도록 현재
    @Transaction
    open suspend fun copyMultipleMemosToNote(memosIds: List<Int>, destinationNotebookId: Int) {
        memosIds.forEach { id ->
            val memoTask = getSelectedMemo(id)
            addMemo(
                memoTask.copy(
                    id = 0,
                    notebookId = destinationNotebookId,
                    updatedAt = ZonedDateTime.now()
                )
            )
        }
    }

    @Query("UPDATE memo_table SET progression = :state WHERE id = :memoId")
    abstract suspend fun updateState(memoId: Int, state: State)

    @Transaction
    open suspend fun updateStateForMultipleMemos(memosIds: List<Int>, state: State) {
        memosIds.forEach { id ->
            updateState(id, state)
        }
    }
}
