package net.pilseong.todocompose.data.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.TodoTask
import net.pilseong.todocompose.data.repository.ZonedDateTypeConverter

@Database(
    entities = [TodoTask::class, Notebook::class],
    version = 1,
//    version = 2,
//    autoMigrations = [
//        AutoMigration(from = 1, to = 2)
//    ],
    exportSchema = true
)
@TypeConverters(ZonedDateTypeConverter::class)
abstract class MemoDatabase : RoomDatabase() {

    abstract fun getTodoDAO(): TodoDAO
    abstract fun getNotebookDAO(): NotebookDAO

}