package net.pilseong.todocompose.data.model.ui

import net.pilseong.todocompose.util.NoteSortingOption
import net.pilseong.todocompose.util.SortOption
import net.pilseong.todocompose.util.StateEntity
import okhttp3.internal.http.StatusLine

data class UserData(
    val prioritySortState: Priority = Priority.NONE,
    val dateOrderState: SortOption = SortOption.UPDATED_AT_DESC,
    val notebookIdState: Long = -1,
    val statusLineOrderState: MutableList<StateEntity> = mutableListOf(
        StateEntity.NOTE_FILTER,
        StateEntity.PRIORITY_FILTER,
        StateEntity.STATE_FILTER,
        StateEntity.FAVORITE_FILTER,
        StateEntity.PRIORITY_ORDER,
        StateEntity.SORTING_ORDER,
        StateEntity.DATE_BASE_ORDER
    ),
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
