package net.pilseong.todocompose.data.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.NotebookWithCount
import java.time.ZonedDateTime

@Dao
abstract class NotebookDAO(
    val database: MemoDatabase
) {

    @Query("SELECT * FROM note_table  WHERE id = :id")
    abstract fun getNotebook(id: Int): Notebook

    @Query("SELECT id, title, description, priority, created_at, updated_at, accessed_at, " +
            "(SELECT COUNT(*) FROM todo_table WHERE note_table.id = todo_table.notebook_id) as memoCount " +
            "FROM note_table WHERE id = :id")
    abstract suspend fun getNotebookWithCount(id: Int): NotebookWithCount

    @Query("SELECT * FROM note_table ORDER BY created_at DESC")
    abstract fun getNotebooks(): Flow<List<Notebook>>

    @Query("SELECT * FROM note_table")
    abstract suspend fun getAllNotebooks(): List<Notebook>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addNotebook(notebook: Notebook)

    @Query("DELETE FROM note_table WHERE id = :note_id")
    abstract suspend fun deleteNotebook(note_id: Int)

    @Update
    abstract suspend fun updateNotebook(notebook: Notebook)

    suspend fun updateNotebookWithTimestamp(notebook: Notebook) =
        updateNotebook(notebook.copy(updatedAt = ZonedDateTime.now()))

    @Transaction
    open suspend fun deleteMultipleNotebooks(notebooksIds: List<Int>) {
        val todoDAO = database.getTodoDAO()

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
            "(SELECT COUNT(*) FROM todo_table WHERE note_table.id = todo_table.notebook_id) as memoCount " +
            "FROM note_table ORDER BY created_at DESC")
    abstract fun getNotebooksWithCount(): Flow<List<NotebookWithCount>>
}