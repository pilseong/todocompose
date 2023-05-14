package net.pilseong.todocompose.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import net.pilseong.todocompose.data.model.TodoTask
import java.time.OffsetDateTime
import java.time.ZonedDateTime

@Dao
abstract class TodoDAO {


    @Query(
        "SELECT * FROM todo_table " +
                "WHERE (title LIKE :query OR description LIKE :query) " +
                "AND " +
                "(CASE :sortCondition " +
                "WHEN 0 THEN " +
                "updated_at BETWEEN :startDate AND :endDate " +
                "WHEN 1 THEN " +
                "updated_at BETWEEN :startDate AND :endDate " +
                "WHEN 2 THEN " +
                "create_at BETWEEN :startDate AND :endDate " +
                "WHEN 3 THEN " +
                "create_at BETWEEN :startDate AND :endDate " +
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
                "CASE WHEN :sortCondition = 2 THEN create_at END DESC, " +
                "CASE WHEN :sortCondition = 3 THEN create_at END ASC " +
                "LIMIT :pageSize OFFSET (:page - 1 ) * :pageSize"
    )
    abstract suspend fun getTasks(
        page: Int,
        pageSize: Int,
        query: String,
        sortCondition: Int = 0,
        priority: String = "HIGH",
        startDate: Long = Long.MIN_VALUE,
        endDate: Long = Long.MAX_VALUE
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
}
