package net.pilseong.todocompose.data.model.ui

import androidx.room.Embedded
import androidx.room.Relation
import net.pilseong.todocompose.data.model.MemoTask
import net.pilseong.todocompose.data.model.Notebook

data class MemoWithNotebook(
    @Embedded val memo: MemoTask,
    val total: Int,
    @Relation(
        parentColumn = "notebook_id",
        entityColumn = "id"
    )
    val notebook: Notebook?
)
