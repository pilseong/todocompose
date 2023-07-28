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
import net.pilseong.todocompose.data.model.ui.MemoDateSortingOption
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.data.model.ui.SortOption
import net.pilseong.todocompose.data.model.ui.State
import net.pilseong.todocompose.data.repository.ZonedDateTypeConverter
import net.pilseong.todocompose.ui.viewmodel.TaskDetails
import net.pilseong.todocompose.ui.viewmodel.toMemoTask
import java.time.ZonedDateTime

@Dao
abstract class MemoDAO(
    val database: MemoDatabase
) {

    @Query("SELECT * FROM memo_table")
    abstract suspend fun allMemos(): List<MemoTask>

    @Transaction
    @Query(
        "SELECT *, " +
                "(  SELECT COUNT(*) " +
                "   FROM memo_table " +
                "   WHERE deleted = 0 " +
                "   AND (title LIKE :query OR description LIKE :query) " +
                "   AND ( " +
                "       CASE :searchNoFilterState " +
                "           WHEN 1 THEN 1=1 " +
                "           WHEN 0 THEN " +
                "           ( " +
                "               ( " +
                "                   CASE :searchRangeAll " +
                "                       WHEN 0 THEN notebook_id = :notebookId " +
                "                      WHEN 1 THEN 1=1 " +
                "                   END" +
                "               ) " +
                "               AND (" +
                "                   CASE :favorite " +
                "                       WHEN 0 THEN 1=1 " +
                "                       WHEN 1 THEN favorite = 1 " +
                "                    END" +
                "               ) " +
                "               AND (" +
                "                   CASE :stateCompleted WHEN 1 THEN progression = 'COMPLETED' END " +
                "                   OR CASE :stateCancelled WHEN 1 THEN progression = 'CANCELLED' END " +
                "                   OR CASE :stateActive    WHEN 1 THEN progression = 'ACTIVE' END " +
                "                   OR CASE :stateSuspended WHEN 1 THEN progression = 'SUSPENDED' END " +
                "                   OR CASE :stateWaiting   WHEN 1 THEN progression = 'WAITING' END " +
                "                   OR CASE :stateNone      WHEN 1 THEN progression = 'NONE' END" +
                "              ) " +
                "              AND (" +
                "                   CASE :priorityHigh WHEN 1 THEN priority = 'HIGH' END " +
                "                   OR CASE :priorityMedium WHEN 1 THEN priority = 'MEDIUM' END " +
                "                   OR CASE :priorityLow    WHEN 1 THEN priority = 'LOW' END " +
                "                   OR CASE :priorityNone   WHEN 1 THEN priority = 'NONE' END " +
                "               ) " +
                "               AND (" +
                "                   CASE :memoDateSortState " +
                "                       WHEN 'CREATED_AT' THEN created_at is not NULL AND created_at BETWEEN :startDate AND :endDate " +
                "                       WHEN 'UPDATED_AT' THEN updated_at is not NULL AND updated_at BETWEEN :startDate AND :endDate " +
                "                       WHEN 'FINISHED_AT' THEN completed_at is not NULL AND completed_at BETWEEN :startDate AND :endDate " +
                "                       WHEN 'DUE_DATE' THEN due_date is not NULL AND due_date BETWEEN :startDate AND :endDate " +
                "                   END" +
                "               ) " +
                "           ) " +
                "       END " +
                "   ) " +
                ") AS total " +
                "FROM memo_table " +
                "WHERE deleted = 0 " +
                "   AND (title LIKE :query OR description LIKE :query) " +
                "   AND ( " +
                "       CASE :searchNoFilterState " +
                "           WHEN 1 THEN 1=1 " +
                "           WHEN 0 THEN " +
                "           ( " +
                "               ( " +
                "                   CASE :searchRangeAll " +
                "                       WHEN 0 THEN notebook_id = :notebookId " +
                "                      WHEN 1 THEN 1=1 " +
                "                   END" +
                "               ) " +
                "               AND (" +
                "                   CASE :favorite " +
                "                       WHEN 0 THEN 1=1 " +
                "                       WHEN 1 THEN favorite = 1 " +
                "                    END" +
                "               ) " +
                "               AND (" +
                "                   CASE :stateCompleted WHEN 1 THEN progression = 'COMPLETED' END " +
                "                   OR CASE :stateCancelled WHEN 1 THEN progression = 'CANCELLED' END " +
                "                   OR CASE :stateActive    WHEN 1 THEN progression = 'ACTIVE' END " +
                "                   OR CASE :stateSuspended WHEN 1 THEN progression = 'SUSPENDED' END " +
                "                   OR CASE :stateWaiting   WHEN 1 THEN progression = 'WAITING' END " +
                "                   OR CASE :stateNone      WHEN 1 THEN progression = 'NONE' END" +
                "              ) " +
                "              AND (" +
                "                   CASE :priorityHigh WHEN 1 THEN priority = 'HIGH' END " +
                "                   OR CASE :priorityMedium WHEN 1 THEN priority = 'MEDIUM' END " +
                "                   OR CASE :priorityLow    WHEN 1 THEN priority = 'LOW' END " +
                "                   OR CASE :priorityNone   WHEN 1 THEN priority = 'NONE' END " +
                "               ) " +
                "               AND (" +
                "                   CASE :memoDateSortState " +
                "                       WHEN 'CREATED_AT' THEN created_at is not NULL AND created_at BETWEEN :startDate AND :endDate " +
                "                       WHEN 'UPDATED_AT' THEN updated_at is not NULL AND updated_at BETWEEN :startDate AND :endDate " +
                "                       WHEN 'FINISHED_AT' THEN completed_at is not NULL AND completed_at BETWEEN :startDate AND :endDate " +
                "                       WHEN 'DUE_DATE' THEN due_date is not NULL AND due_date BETWEEN :startDate AND :endDate " +
                "                   END" +
                "               ) " +
                "           ) " +
                "       END " +
                "   ) " +
                "ORDER BY " +
                "   CASE :priority " +
                "       WHEN 'LOW' THEN " +
                "           CASE " +
                "               WHEN priority LIKE 'L%' THEN 1 " +
                "               WHEN priority LIKE 'M%' THEN 2 " +
                "               WHEN priority LIKE 'H%' THEN 3 " +
                "               WHEN priority LIKE 'N%' THEN 4 " +
                "           END " +
                "       WHEN 'HIGH' THEN" +
                "           CASE " +
                "               WHEN priority LIKE 'H%' THEN 1 " +
                "               WHEN priority LIKE 'M%' THEN 2 " +
                "               WHEN priority LIKE 'L%' THEN 3 " +
                "               WHEN priority LIKE 'N%' THEN 4 " +
                "           END " +
                "       END, " +
                "   CASE WHEN :memoOrderState = 'DESC' AND :memoDateSortState = 'CREATED_AT' THEN created_at END DESC, " +
                "   CASE WHEN :memoOrderState = 'DESC' AND :memoDateSortState = 'UPDATED_AT' THEN updated_at END DESC, " +
                "   CASE WHEN :memoOrderState = 'DESC' AND :memoDateSortState = 'FINISHED_AT' THEN completed_at END DESC, " +
                "   CASE WHEN :memoOrderState = 'DESC' AND :memoDateSortState = 'DUE_DATE' THEN due_date END DESC, " +
                "   CASE WHEN :memoOrderState = 'ASC' AND :memoDateSortState = 'CREATED_AT' THEN created_at END ASC, " +
                "   CASE WHEN :memoOrderState = 'ASC' AND :memoDateSortState = 'UPDATED_AT' THEN updated_at END ASC, " +
                "   CASE WHEN :memoOrderState = 'ASC' AND :memoDateSortState = 'FINISHED_AT' THEN completed_at END ASC, " +
                "   CASE WHEN :memoOrderState = 'ASC' AND :memoDateSortState = 'DUE_DATE' THEN due_date END ASC " +
                "LIMIT :pageSize OFFSET (:page - 1 ) * :pageSize"
    )
    abstract suspend fun getMemosWithNotebooks(
        page: Int,
        pageSize: Int,
        query: String,
        searchNoFilterState: Boolean = false,
        searchRangeAll: Boolean = false,
        memoDateSortState: MemoDateSortingOption = MemoDateSortingOption.UPDATED_AT,
        memoOrderState: SortOption = SortOption.DESC,
        priority: String = "HIGH",
        startDate: Long = Long.MIN_VALUE,
        endDate: Long = Long.MAX_VALUE,
        favorite: Boolean = false,
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
    ): List<MemoWithNotebook>


    @Query("SELECT *, " +
            "(  SELECT COUNT(*) " +
            "   FROM   memo_table " +
            "   WHERE deleted = 0 " +
            "       AND ( " +
            "           CASE :searchRangeAll " +
            "               WHEN 0 THEN notebook_id = :notebookId " +
            "               WHEN 1 THEN 1=1 " +
            "           END" +
            "       ) " +
            "       AND due_date is not null " +
            "       AND due_date BETWEEN :startDateTime AND :endDateTime" +
            ") as total " +
            "FROM   memo_table " +
            "WHERE deleted = 0" +
            "   AND ( " +
            "       CASE :searchRangeAll " +
            "           WHEN 0 THEN notebook_id = :notebookId " +
            "           WHEN 1 THEN 1=1 " +
            "       END" +
            "   ) " +
            "   AND due_date is not null " +
            "   AND due_date BETWEEN :startDateTime AND :endDateTime "
    )
    abstract fun getMonthlyTasksAsFlow(
        notebookId: Long,
        searchRangeAll: Boolean = false,
        startDateTime: Long = Long.MIN_VALUE,
        endDateTime: Long = Long.MAX_VALUE,
    ): Flow<List<MemoWithNotebook>>

    // 메모 리스트 메뉴로 현재 노트북의 있는 메모 삭제
    @Query("UPDATE memo_table SET deleted = 1 WHERE notebook_id = :notebookId")
    abstract fun deleteMemosInNote(notebookId: Long)


    @Query("SELECT * FROM memo_table WHERE deleted = 0 AND id = :memoId")
    abstract fun getSelectedMemo(memoId: Long): MemoTask

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addMemo(memo: MemoTask): Long


    @Transaction
    open suspend fun addMemo(memo: TaskDetails): Long {
        val photoDAO = database.getPhotoDAO()

        val memoId = addMemo(memo.toMemoTask())
        val photos = memo.photos

        photos.forEach { photo ->
            photoDAO.addPhoto(photo.copy(memoId = memoId))
        }

        return memoId
    }

    suspend fun updateMemoWithTimestamp(memo: MemoTask) =
        updateMemo(memo.copy(updatedAt = ZonedDateTime.now()))


    // 사진 까지 같이 업 데이트 해야 한다.
    @Transaction
    open suspend fun updateMemoWithTimestamp(memo: TaskDetails): List<Long> {
        // update memo first
        updateMemo(memo.toMemoTask().copy(updatedAt = ZonedDateTime.now()))

        val photoDAO = database.getPhotoDAO()

        // (기존 사진 - 삭제) (추가는 id 을 으로 판단해 데이터 베이스 업 데이트
        val photoIds = mutableListOf<Long>()

        // (기존 사진) 업 데이트 전 메모에 포함 되어 있는 사진 id를 모두 가져 온다.
        val dbPhotosIds = photoDAO.getPhotoIdsByMemoId(memo.id)

        // 수정할 사진 목록을 돌면서 id가 0이면 추가된 사진 이므로 사진을 추가
        // 돌면서 추가 대상이 아닌 아이디 목록을 photoIds 로 만든다.
        memo.photos.forEach { photo ->
            if (photo.id == 0L) {
                photoDAO.addPhoto(photo)
            } else {
                photoIds.add(photo.id)
            }
        }

        // 데이터 베이스 의 메모에 포함된 id가 수정된 목록에 존재 하지 않으면 삭제된 것 이므로
        // 삭제 처리 한다.
        val deletedIds = mutableListOf<Long>()
        dbPhotosIds.forEach { id ->
            if (!photoIds.contains(id)) {
                photoDAO.deletePhotoById(id)
                deletedIds.add(id)
            }
        }

        return deletedIds
    }


    @Update
    abstract suspend fun updateMemo(memo: MemoTask)

    @Query("UPDATE memo_table SET deleted = 1 WHERE id = :memoId")
    abstract suspend fun deleteMemo(memoId: Long)

    @Query("UPDATE memo_table SET deleted = 1")
    abstract suspend fun deleteAllMemos()

    // 노트북 자체를 삭제 할 때 cascade 로 삭제 된다.(현재 수동 삭제)
    @Query("DELETE FROM memo_table WHERE notebook_id = :notebookId")
    abstract suspend fun deleteTasksByNotebookId(notebookId: Long)


    @Transaction
    open suspend fun deleteSelectedMemos(notesIds: List<Long>) {
        notesIds.forEach { id ->
            deleteMemo(id)
        }
    }

    @Transaction
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
    abstract suspend fun updateNotebookId(memoId: Long, notebookId: Long, updatedAt: Long)

    @Transaction
    open suspend fun updateMultipleNotebookIds(memosIds: List<Long>, destinationNotebookId: Long) {
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
    open suspend fun copyMultipleMemosToNote(memosIds: List<Long>, destinationNotebookId: Long) {
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
    abstract suspend fun updateState(memoId: Long, state: State)

    @Query("UPDATE memo_table SET progression = :state, completed_at = :finishedAt WHERE id = :memoId")
    abstract suspend fun updateState(memoId: Long, state: State, finishedAt: Long)

    @Transaction
    open suspend fun updateStateForMultipleMemos(memosIds: List<Long>, state: State) {
        memosIds.forEach { id ->
            if (state == State.COMPLETED || state == State.CANCELLED) {
                updateState(
                    memoId = id,
                    state = state,
                    finishedAt = ZonedDateTypeConverter.fromZonedDateTime(ZonedDateTime.now())!!
                )
            } else {
                updateState(id, state)
            }
        }
    }

    // 알람이 설정된 메모를 가지고 온다.
    @Query(
        " SELECT id " +
                "FROM memo_table " +
                "WHERE notebook_id = :notebookId " +
                "   AND deleted = 0 " +
                "   AND due_date is not null " +
                "   AND reminder_type != 'NOT_USED'"
    )
    abstract suspend fun getMemosWithAlarmByNotebookId(notebookId: Long): List<Long>
}
