package net.pilseong.todocompose.data.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import net.pilseong.todocompose.data.model.Notebook

@Dao
abstract class NotebookDAO(
    val database: MemoDatabase
) {

    @Query("SELECT * FROM note_table  WHERE id = :id")
    abstract fun getNotebook(id: Int): Notebook

    @Query("SELECT * FROM note_table ORDER BY created_at DESC")
    abstract fun getNotebooks(): Flow<List<Notebook>>

    @Query("SELECT * FROM note_table")
    abstract suspend fun getAllNotebooks(): List<Notebook>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addNotebook(notebook: Notebook)

    @Query("DELETE FROM note_table WHERE id = :note_id")
    abstract suspend fun deleteNotebook(note_id: Int)

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
    open suspend fun insertMultipleNotebooks(notebooksIds: List<Notebook>) {
        notebooksIds.forEach{
            addNotebook(it)
        }
    }

}