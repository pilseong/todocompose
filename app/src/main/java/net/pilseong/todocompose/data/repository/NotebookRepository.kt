package net.pilseong.todocompose.data.repository;

import android.util.Log
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.NotebookWithCount
import net.pilseong.todocompose.data.model.database.NotebookDAO
import net.pilseong.todocompose.util.NoteSortingOption
import javax.inject.Inject

@ActivityRetainedScoped
class NotebookRepository @Inject constructor(
    private val notebookDAO: NotebookDAO
) {
//    fun getNotebooks(): Flow<List<Notebook>> {
//        return notebookDAO.getNotebooks()
//    }

    fun getNotebooks(sortingOption: NoteSortingOption): Flow<List<NotebookWithCount>> {
        Log.i("PHILIP", "[NotebookRepository] getNotebooks with $sortingOption")
        return notebookDAO.getNotebooksWithCount(sortingOption)
    }

    suspend fun getAllNotebooks(): List<Notebook> {
        return notebookDAO.getAllNotebooks()
    }

    fun getNotebook(id: Int): Notebook {
        return notebookDAO.getNotebook(id)
    }

    suspend fun getNotebookWithCount(id: Int): NotebookWithCount {
        return notebookDAO.getNotebookWithCount(id)
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

    suspend fun updateNotebook(notebook: Notebook) {
        notebookDAO.updateNotebookWithTimestamp(notebook)
    }

    suspend fun updateAccessTime(id: Int) {
        Log.i("PHILIP","[NotebookRepository] updateAccessTime $id")
        notebookDAO.updateAccessTime(id)
    }
}

