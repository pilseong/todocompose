package net.pilseong.todocompose.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.pilseong.todocompose.data.model.TodoTask
import net.pilseong.todocompose.data.repository.ZonedDateTypeConverter

@Database(
    entities = [TodoTask::class],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ],
    exportSchema = true
)
@TypeConverters(ZonedDateTypeConverter::class)
abstract class TodoDatabase : RoomDatabase() {

    abstract fun getTodoDAO(): TodoDAO

}