package net.pilseong.todocompose.data.model.ui

import androidx.room.Embedded
import androidx.room.Relation
import net.pilseong.todocompose.data.model.MemoTask
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.Photo
import net.pilseong.todocompose.util.Constants
import java.time.ZonedDateTime

data class MemoWithNotebook(
    @Embedded val memo: MemoTask,
    val total: Int,
    @Relation(
        parentColumn = "notebook_id",
        entityColumn = "id"
    )
    val notebook: Notebook?,

    @Relation(
        parentColumn = "id",
        entityColumn = "memoId"
    )
    val photos: List<Photo>
) {

    companion object {
        @JvmStatic
        fun instance(notebookId: Long = -1): MemoWithNotebook {
            return MemoWithNotebook(
                memo = MemoTask(
                    id = Constants.NEW_ITEM_ID,
                    title = "",
                    description = "",
                    priority = Priority.NONE,
                    favorite = false,
                    progression = State.NONE,
                    createdAt = ZonedDateTime.now(),
                    updatedAt = ZonedDateTime.now(),
                    finishedAt = null,
                    notebookId = notebookId
                ),
                total = 0,
                notebook = Notebook.instance(id = notebookId),
                photos = emptyList())
        }
    }
}

