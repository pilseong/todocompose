package net.pilseong.todocompose.data.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.TodoTask

@Dao
abstract class NotebookDAO {

    @Query("SELECT * FROM note_table  WHERE id = :id")
    abstract fun getNotebook(id: Int): Notebook

    @Query("SELECT * FROM note_table ORDER BY created_at DESC")
    abstract fun getNotebooks(): Flow<List<Notebook>>

    @Query("SELECT * FROM note_table")
    abstract suspend fun getAllNotebooks(): List<Notebook>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addNotebook(notebook: Notebook)

}