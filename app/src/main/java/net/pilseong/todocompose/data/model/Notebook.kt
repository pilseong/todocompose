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
    val updatedAt: ZonedDateTime = ZonedDateTime.now()
) {
    companion object {
        fun instance(title: String = ""): Notebook {
            return Notebook(title = title, description = "", priority = Priority.NONE)
        }
    }
}
