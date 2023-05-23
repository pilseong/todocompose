package net.pilseong.todocompose.data.repository;

import net.pilseong.todocompose.data.model.database.NotebookDAO;

import javax.inject.Inject;

import dagger.hilt.android.scopes.ActivityRetainedScoped;
import kotlinx.coroutines.flow.Flow
import net.pilseong.todocompose.data.model.Notebook
import net.pilseong.todocompose.data.model.TodoTask

@ActivityRetainedScoped
class NotebookRepository @Inject constructor(
        private val notebookDAO: NotebookDAO
) {
    fun getNotebooks(): Flow<List<Notebook>> {
        return notebookDAO.getNotebooks()
    }

    suspend fun addNotebook(notebook: Notebook) {
        notebookDAO.addNotebook(notebook)
    }
}

