package net.pilseong.todocompose.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import net.pilseong.todocompose.util.Constants.DATABASE_TABLE
import java.time.ZonedDateTime

@Entity(tableName = DATABASE_TABLE)
data class TodoTask @RequiresApi(Build.VERSION_CODES.O) constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val priority: Priority,
    @ColumnInfo(name = "favorite", defaultValue = "false")
    val favorite: Boolean = false,
    @ColumnInfo(name = "create_at")
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: ZonedDateTime = ZonedDateTime.now()
) {

}