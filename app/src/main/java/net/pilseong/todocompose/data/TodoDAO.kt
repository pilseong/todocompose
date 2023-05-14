package net.pilseong.todocompose.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import net.pilseong.todocompose.data.model.Priority
import net.pilseong.todocompose.data.model.TodoTask
import java.time.ZonedDateTime

@Dao
abstract class TodoDAO {


    @Query(
        "SELECT * FROM todo_table " +
                "WHERE title LIKE :query OR description LIKE :query " +
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
    abstract suspend fun getTasks(page: Int,
                                  pageSize: Int,
                                  query: String,
                                  sortCondition: Int = 0,
                                  priority: String = "HIGH"
    ): List<TodoTask>

//    @Query("SELECT * FROM todo_table ORDER BY updated_at DESC")
//    abstract fun getAllTasks(): Flow<List<TodoTask>>

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

//    @Query(
//        "SELECT * FROM todo_table WHERE title " +
//                "LIKE :searchQuery OR description LIKE :searchQuery " +
//                "ORDER BY create_at DESC " +
//                "LIMIT :pageSize OFFSET (:page - 1) * :pageSize"
//    )
//    abstract suspend fun searchTasks(
//        searchQuery: String,
//        page: Int,
//        pageSize: Int,
////        datePoint: String
//    ): List<TodoTask>

    @Query(
        "SELECT * FROM todo_table ORDER BY " +
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
                "updated_at DESC LIMIT :pageSize OFFSET (:page - 1 ) * :pageSize"
    )
    abstract suspend fun sortByPriority(page: Int, pageSize: Int, priority: String = "HIGH"): List<TodoTask>

//    @Query(
//        "SELECT * FROM todo_table ORDER BY " +
//                "CASE " +
//                "WHEN priority LIKE 'H%' THEN 1 " +
//                "WHEN priority LIKE 'M%' THEN 2 " +
//                "WHEN priority LIKE 'L%' THEN 3 " +
//                "WHEN priority LIKE 'N%' THEN 4 " +
//                "END, " +
//                "updated_at DESC LIMIT :pageSize OFFSET (:page - 1 ) * :pageSize"
//    )
//    abstract suspend fun sortByHighPriority(page: Int, pageSize: Int): List<TodoTask>
}