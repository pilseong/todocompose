package net.pilseong.todocompose.data.repository;

import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.database.NotebookDAO
import javax.inject.Inject

@ActivityRetainedScoped
class NotebookRepository @Inject constructor(
        private val notebookDAO: NotebookDAO
) {
    fun getNotebooks(): Flow<List<Notebook>> {
        return notebookDAO.getNotebooks()
    }

    suspend fun getAllNotebooks(): List<Notebook> {
        return notebookDAO.getAllNotebooks()
    }

    fun getNotebook(id: Int): Notebook {
        return notebookDAO.getNotebook(id)
    }

    suspend fun addNotebook(notebook: Notebook) {
        notebookDAO.addNotebook(notebook)
    }

    suspend fun deleteMultipleNotebooks(notebooksIds: List<Int>) {
        notebookDAO.deleteMultipleNotebooks(notebooksIds)
    }

    suspend fun insertMultipleNotebooks(notebooksIds: List<Notebook>) {
        notebookDAO.insertMultipleNotebooks(notebooksIds)
    }
}

