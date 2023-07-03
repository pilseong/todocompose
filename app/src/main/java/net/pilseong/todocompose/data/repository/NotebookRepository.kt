package net.pilseong.todocompose.data.repository;

import android.util.Log
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.database.NotebookDAO
import net.pilseong.todocompose.data.model.ui.NotebookWithCount
import net.pilseong.todocompose.util.NoteSortingOption
import javax.inject.Inject

@ActivityRetainedScoped
class NotebookRepository @Inject constructor(
    private val notebookDAO: NotebookDAO
) {
    fun getNotebooksAsFlow(sortingOption: NoteSortingOption): Flow<List<NotebookWithCount>> {
        Log.d("PHILIP", "[NotebookRepository] getNotebooksAsFlow with $sortingOption")
        return notebookDAO.getNotebooksWithCountAsFlow(sortingOption)
    }

    suspend fun getAllNotebooks(): List<Notebook> {
        return notebookDAO.getAllNotebooks()
    }

    suspend fun getNotebook(id: Int): Notebook {
        return notebookDAO.getNotebook(id)
    }

    fun getNotebookWithCountAsFlow(id: Int): Flow<NotebookWithCount> {
        return notebookDAO.getNotebookWithCountAsFlow(id)
    }

    suspend fun addNotebook(notebook: Notebook) {
        notebookDAO.addNotebook(notebook)
    }

    suspend fun deleteMultipleNotebooks(notebooksIds: List<Int>) {
        notebookDAO.deleteMultipleNotebooks(notebooksIds)
    }

    suspend fun insertMultipleNotebooks(notebooksIds: List<Notebook>) {
        withContext(Dispatchers.IO) {
            notebookDAO.insertMultipleNotebooks(notebooksIds)
        }
    }

    suspend fun updateNotebook(notebook: Notebook) {
        notebookDAO.updateNotebookWithTimestamp(notebook)
    }

    suspend fun updateAccessTime(id: Int) {
        Log.d("PHILIP","[NotebookRepository] updateAccessTime $id")
        notebookDAO.updateAccessTime(id)
    }
}

