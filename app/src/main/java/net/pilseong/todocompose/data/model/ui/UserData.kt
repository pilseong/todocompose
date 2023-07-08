package net.pilseong.todocompose.data.model.ui

import net.pilseong.todocompose.util.NoteSortingOption
import net.pilseong.todocompose.util.SortOption

data class UserData(
    val prioritySortState: Priority = Priority.NONE,
    val dateOrderState: SortOption = SortOption.UPDATED_AT_DESC,
    val notebookIdState: Long = -1,
    val firstRecentNotebookId: Long? = null,
    val secondRecentNotebookId: Long? = null,
    val sortFavorite: Boolean = false,
    val stateState: Int = 31,
    val stateNone: Boolean = true,
    val stateWaiting: Boolean = true,
    val stateSuspended: Boolean = true,
    val stateActive: Boolean = true,
    val stateCancelled: Boolean = true,
    val stateCompleted: Boolean = true,
    val priorityFilterState: Int = 16,
    val priorityHigh: Boolean = true,
    val priorityMedium: Boolean = true,
    val priorityLow: Boolean = true,
    val priorityNone: Boolean = true,
    val noteSortingOptionState: NoteSortingOption = NoteSortingOption.ACCESS_AT,
)
