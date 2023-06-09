package net.pilseong.todocompose.data.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.pilseong.todocompose.data.model.MemoTask
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.Photo
import net.pilseong.todocompose.data.repository.ZonedDateTypeConverter

@Database(
    entities = [MemoTask::class, Notebook::class, Photo::class],
    version = 1,
//    version = 2,
//    autoMigrations = [
//        AutoMigration(from = 1, to = 2)
//    ],
    exportSchema = true
)
@TypeConverters(ZonedDateTypeConverter::class)
abstract class MemoDatabase : RoomDatabase() {

    abstract fun getMemoDAO(): MemoDAO

    abstract fun getNotebookDAO(): NotebookDAO

    abstract fun getPhotoDAO(): PhotoDAO

}