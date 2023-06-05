package net.pilseong.todocompose.data.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import net.pilseong.todocompose.data.model.DefaultNoteMemoCount
import net.pilseong.todocompose.data.model.TodoTask
import net.pilseong.todocompose.data.repository.ZonedDateTypeConverter
import java.time.ZonedDateTime

@Dao
abstract class TodoDAO {

    @Query("SELECT * FROM todo_table")
    abstract suspend fun allTasks(): List<TodoTask>

    @Query(
        "SELECT * FROM todo_table " +
                "WHERE " + "notebook_id = :notebookId " +
                "AND (title LIKE :query OR description LIKE :query) " +
                "AND (" +
                "       CASE :favorite " +
                "           WHEN 0 THEN 1=1 " +
                "           WHEN 1 THEN favorite = 1 " +
                "       END) " +
                "AND (" +
                "       CASE :stateClosed " +
                "           WHEN 1 THEN progression = 'CLOSED'" +
                "       END " +
                "OR " +
                "       CASE :stateOnit " +
                "           WHEN 1 THEN progression = 'ONIT' " +
                "       END " +
                "OR " +
                "       CASE :stateSuspended " +
                "           WHEN 1 THEN progression = 'SUSPENDED' " +
                "       END " +
                "OR " +
                "       CASE :stateOpen " +
                "           WHEN 1 THEN progression = 'OPEN' " +
                "       END " +
                "OR " +
                "       CASE :stateNone " +
                "           WHEN 1 THEN progression = 'NONE' " +
                "       END" +
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
    abstract suspend fun getTasks(
        page: Int,
        pageSize: Int,
        query: String,
        sortCondition: Int = 0,
        priority: String = "HIGH",
        startDate: Long = Long.MIN_VALUE,
        endDate: Long = Long.MAX_VALUE,
        favorite: Boolean = false,
        notebookId: Int = -1,
        stateClosed: Boolean = true,
        stateOnit: Boolean = true,
        stateSuspended: Boolean = true,
        stateOpen: Boolean = true,
        stateNone: Boolean = true,
    ): List<TodoTask>

    @Query("SELECT * FROM todo_table WHERE id = :taskId")
    abstract fun getSelectedTask(taskId: Int): TodoTask

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addTask(todo: TodoTask)

    suspend fun updateTaskWithTimestamp(todo: TodoTask) =
        updateTask(todo.copy(updatedAt = ZonedDateTime.now()))

    @Update
    abstract suspend fun updateTask(todo: TodoTask)

    @Query("DELETE FROM todo_table WHERE id = :todoId")
    abstract suspend fun deleteTask(todoId: Int)

    @Query("DELETE FROM todo_table")
    abstract suspend fun deleteAllTasks()

    @Query("DELETE FROM todo_table WHERE notebook_id = :notebookId")
    abstract suspend fun deleteTasksByNotebookId(notebookId: Int)


    @Transaction
    open suspend fun deleteSelectedTasks(notesIds: List<Int>) {
        notesIds.forEach { id ->
            deleteTask(id)
        }
    }

    @Query("SELECT COUNT(*) AS total, " +
            "SUM (CASE priority WHEN 'HIGH' THEN 1 END) AS high, " +
            "SUM (CASE priority WHEN 'MEDIUM' THEN 1 END) AS medium, " +
            "SUM (CASE priority WHEN 'LOW' THEN 1 END) AS low, " +
            "SUM (CASE priority  WHEN 'NONE' THEN 1 END) AS none " +
            "FROM todo_table WHERE notebook_id = :notebookId")
    abstract suspend fun getMemoCount(notebookId: Int): DefaultNoteMemoCount

    @Transaction
    open suspend fun insertMultipleMemos(tasks: List<TodoTask>) {
        tasks.forEach { task ->
            addTask(task)
        }
    }

    @Query("UPDATE todo_table SET notebook_id = :notebookId, updated_at = :updatedAt WHERE id = :taskId")
    abstract suspend fun updateNotebookId(taskId: Int, notebookId: Int, updatedAt: Long)

    @Transaction
    open suspend fun updateMultipleNotebookIds(todosIds: List<Int>, destinationNotebookId: Int) {
        todosIds.forEach { id ->
            updateNotebookId(id, destinationNotebookId, ZonedDateTypeConverter.fromZonedDateTime(
                ZonedDateTime.now()))
        }
    }
}
