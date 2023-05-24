package net.pilseong.todocompose.data.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import net.pilseong.todocompose.data.model.TodoTask
import java.time.ZonedDateTime

@Dao
abstract class TodoDAO {

    @Query("SELECT * FROM todo_table")
    abstract suspend fun allTasks(): List<TodoTask>

    @Query(
        "SELECT * FROM todo_table " +
                "WHERE " + "notebook_id = :notebookId " +
                "AND (title LIKE :query OR description LIKE :query) " +
                "AND " +
                "(CASE :favorite " +
                "WHEN 0 THEN " +
                "1=1 " +
                "when 1 THEN " +
                "favorite = 1 " +
                "END) " +
                "AND " +
                "(CASE :sortCondition " +
                "WHEN 0 THEN " +
                "updated_at BETWEEN :startDate AND :endDate " +
                "WHEN 1 THEN " +
                "updated_at BETWEEN :startDate AND :endDate " +
                "WHEN 2 THEN " +
                "created_at BETWEEN :startDate AND :endDate " +
                "WHEN 3 THEN " +
                "created_at BETWEEN :startDate AND :endDate " +
                "END) " +
                "ORDER BY " +
                "CASE :priority " +
                "WHEN 'LOW' THEN " +
                "   CASE " +
                "   WHEN priority LIKE 'L%' THEN 1 " +
                "   WHEN priority LIKE 'M%' THEN 2 " +
                "   WHEN priority LIKE 'H%' THEN 3 " +
                "   WHEN priority LIKE 'N%' THEN 4 " +
                "   END " +
                "WHEN 'HIGH' THEN" +
                "   CASE " +
                "   WHEN priority LIKE 'H%' THEN 1 " +
                "   WHEN priority LIKE 'M%' THEN 2 " +
                "   WHEN priority LIKE 'L%' THEN 3 " +
                "   WHEN priority LIKE 'N%' THEN 4 " +
                "   END " +
                "END, " +
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
        notebookId: Int = -1
    ): List<TodoTask>

    @Query("SELECT * FROM todo_table WHERE id = :taskId")
    abstract fun getSelectedTask(taskId: Int): TodoTask

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addTask(todo: TodoTask)

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    abstract suspend fun insertTask(todo: TodoTask)

    suspend fun updateTaskWithTimestamp(todo: TodoTask) =
        updateTask(todo.copy(updatedAt = ZonedDateTime.now()))

    @Update
    abstract suspend fun updateTask(todo: TodoTask)

    @Update
    abstract suspend fun updateFavorite(todo: TodoTask)

    @Query("DELETE FROM todo_table WHERE id = :todoId")
    abstract suspend fun deleteTask(todoId: Int)

    @Query("DELETE FROM todo_table")
    abstract suspend fun deleteAllTasks()
}
