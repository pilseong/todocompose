package net.pilseong.todocompose.data.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.ui.NotebookWithCount
import net.pilseong.todocompose.util.NoteSortingOption
import java.time.ZonedDateTime

@Dao
abstract class NotebookDAO(
    val database: MemoDatabase
) {

    @Query("SELECT * FROM note_table  WHERE id = :id")
    abstract suspend fun getNotebook(id: Long): Notebook

    @Query("SELECT id, title, description, priority, created_at, updated_at, accessed_at, " +
            "(SELECT COUNT(*) FROM memo_table WHERE deleted = 0 AND note_table.id = memo_table.notebook_id) as memoTotalCount, " +
            "(SELECT COUNT(*) FROM memo_table WHERE deleted = 0 AND note_table.id = memo_table.notebook_id AND memo_table.progression = 'COMPLETED') as completedCount, " +
            "(SELECT COUNT(*) FROM memo_table WHERE deleted = 0 AND note_table.id = memo_table.notebook_id AND memo_table.progression = 'CANCELLED') as cancelledCount, " +
            "(SELECT COUNT(*) FROM memo_table WHERE deleted = 0 AND note_table.id = memo_table.notebook_id AND memo_table.progression = 'ACTIVE') as activeCount, " +
            "(SELECT COUNT(*) FROM memo_table WHERE deleted = 0 AND note_table.id = memo_table.notebook_id AND memo_table.progression = 'SUSPENDED') as suspendedCount, " +
            "(SELECT COUNT(*) FROM memo_table WHERE deleted = 0 AND note_table.id = memo_table.notebook_id AND memo_table.progression = 'WAITING') as waitingCount, " +
            "(SELECT COUNT(*) FROM memo_table WHERE deleted = 0 AND note_table.id = memo_table.notebook_id AND memo_table.progression = 'NONE') as noneCount, " +
            "(SELECT COUNT(*) FROM memo_table WHERE deleted = 0 AND note_table.id = memo_table.notebook_id AND memo_table.priority = 'HIGH') as highPriorityCount, " +
            "(SELECT COUNT(*) FROM memo_table WHERE deleted = 0 AND note_table.id = memo_table.notebook_id AND memo_table.priority = 'MEDIUM') as mediumPriorityCount, " +
            "(SELECT COUNT(*) FROM memo_table WHERE deleted = 0 AND note_table.id = memo_table.notebook_id AND memo_table.priority = 'LOW') as lowPriorityCount, " +
            "(SELECT COUNT(*) FROM memo_table WHERE deleted = 0 AND note_table.id = memo_table.notebook_id AND memo_table.priority = 'NONE') as nonePriorityCount " +
            "FROM note_table WHERE id = :id")
    abstract fun getNotebookWithCountAsFlow(id: Long): Flow<NotebookWithCount>

    @Query("SELECT * FROM note_table ORDER BY updated_at DESC")
    abstract fun getNotebooks(): Flow<List<Notebook>>

    @Query("SELECT * FROM note_table ORDER BY accessed_at DESC")
    abstract suspend fun getAllNotebooks(): List<Notebook>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addNotebook(notebook: Notebook)

    @Query("DELETE FROM note_table WHERE id = :note_id")
    abstract suspend fun deleteNotebook(note_id: Long)

    @Update
    abstract suspend fun updateNotebook(notebook: Notebook)

    suspend fun updateNotebookWithTimestamp(notebook: Notebook) =
        updateNotebook(notebook.copy(updatedAt = ZonedDateTime.now()))

    @Transaction
    open suspend fun updateAccessTime(id: Long) {
        val note = getNotebook(id)
        updateNotebook(note.copy(accessedAt = ZonedDateTime.now()))
    }

    @Transaction
    open suspend fun deleteMultipleNotebooks(notebooksIds: List<Long>) {
        val todoDAO = database.getMemoDAO()

        notebooksIds.forEach { notebookId ->
            todoDAO.deleteTasksByNotebookId(notebookId)
        }

        notebooksIds.forEach { notebookId ->
            deleteNotebook(notebookId)
        }
    }

    @Transaction
    open suspend fun insertMultipleNotebooks(notebooks: List<Notebook>) {
        notebooks.forEach{ notebook ->
            addNotebook(notebook)
        }
    }

    @Query("SELECT id, title, description, priority, created_at, updated_at, accessed_at, " +
            "(SELECT COUNT(*) FROM memo_table WHERE deleted = 0 AND note_table.id = memo_table.notebook_id) as memoTotalCount, " +
            "(SELECT COUNT(*) FROM memo_table WHERE deleted = 0 AND note_table.id = memo_table.notebook_id AND memo_table.progression = 'COMPLETED') as completedCount, " +
            "(SELECT COUNT(*) FROM memo_table WHERE deleted = 0 AND note_table.id = memo_table.notebook_id AND memo_table.progression = 'CANCELLED') as cancelledCount, " +
            "(SELECT COUNT(*) FROM memo_table WHERE deleted = 0 AND note_table.id = memo_table.notebook_id AND memo_table.progression = 'ACTIVE') as activeCount, " +
            "(SELECT COUNT(*) FROM memo_table WHERE deleted = 0 AND note_table.id = memo_table.notebook_id AND memo_table.progression = 'SUSPENDED') as suspendedCount, " +
            "(SELECT COUNT(*) FROM memo_table WHERE deleted = 0 AND note_table.id = memo_table.notebook_id AND memo_table.progression = 'WAITING') as waitingCount, " +
            "(SELECT COUNT(*) FROM memo_table WHERE deleted = 0 AND note_table.id = memo_table.notebook_id AND memo_table.progression = 'NONE') as noneCount, " +
            "(SELECT COUNT(*) FROM memo_table WHERE deleted = 0 AND note_table.id = memo_table.notebook_id AND memo_table.priority = 'HIGH') as highPriorityCount, " +
            "(SELECT COUNT(*) FROM memo_table WHERE deleted = 0 AND note_table.id = memo_table.notebook_id AND memo_table.priority = 'MEDIUM') as mediumPriorityCount, " +
            "(SELECT COUNT(*) FROM memo_table WHERE deleted = 0 AND note_table.id = memo_table.notebook_id AND memo_table.priority = 'LOW') as lowPriorityCount, " +
            "(SELECT COUNT(*) FROM memo_table WHERE deleted = 0 AND note_table.id = memo_table.notebook_id AND memo_table.priority = 'NONE') as nonePriorityCount " +
            "FROM note_table " +
            "ORDER BY " +
            "CASE WHEN :sortingOption = 'ACCESS_AT' THEN accessed_at END DESC, " +
            "CASE WHEN :sortingOption = 'UPDATED_AT' THEN updated_at END DESC, " +
            "CASE WHEN :sortingOption = 'CREATED_AT' THEN created_at END DESC")
    abstract fun getNotebooksWithCountAsFlow(sortingOption: NoteSortingOption): Flow<List<NotebookWithCount>>
}