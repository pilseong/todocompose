package net.pilseong.todocompose.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import net.pilseong.todocompose.util.Constants.NOTE_TABLE
import java.time.ZonedDateTime

@Entity(tableName = NOTE_TABLE)
data class Notebook @RequiresApi(Build.VERSION_CODES.O) constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val priority: Priority,
    @ColumnInfo(name = "created_at")
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: ZonedDateTime = ZonedDateTime.now(),
    @ColumnInfo(name = "accessed_at")
    val accessedAt: ZonedDateTime = ZonedDateTime.now()
) {
    companion object {
        fun instance(
            id: Int = Int.MAX_VALUE,
            title: String = "",
            description: String = "",
            priority: Priority = Priority.NONE,
            createdAt: ZonedDateTime = ZonedDateTime.now(),
            updatedAt: ZonedDateTime = ZonedDateTime.now(),
            accessedAt: ZonedDateTime = ZonedDateTime.now(),
        ): Notebook {
            return Notebook(
                id = id,
                title = title, description = description, priority = priority,
                createdAt = createdAt, updatedAt = updatedAt, accessedAt = accessedAt
            )
        }

        fun getNotebook(notebookWithCount: NotebookWithCount): Notebook {
            return Notebook.instance(
                id = notebookWithCount.id,
                title = notebookWithCount.title,
                description = notebookWithCount.description,
                priority = notebookWithCount.priority,
                createdAt = notebookWithCount.createdAt,
                updatedAt = notebookWithCount.updatedAt,
                accessedAt = notebookWithCount.accessedAt,
            )
        }
    }
}
