package net.pilseong.todocompose.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class MemoWithNotebook(
    @Embedded val memo: TodoTask,
    val total: Int,
    @Relation(
        parentColumn = "notebook_id",
        entityColumn = "id"
    )
    val notebook: Notebook?
)
