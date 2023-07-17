package net.pilseong.todocompose.ui.screen.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.paging.compose.LazyPagingItems
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.data.model.ui.State
import net.pilseong.todocompose.data.model.ui.UserData
import net.pilseong.todocompose.ui.viewmodel.MemoViewModel
import net.pilseong.todocompose.ui.viewmodel.toMemoTask
import net.pilseong.todocompose.util.Action
import net.pilseong.todocompose.util.SortOption
import net.pilseong.todocompose.util.StateEntity

@Composable
fun ListView(
    uiState: UserData,
    memoViewModel: MemoViewModel,
    onDateRangeCloseClick: () -> Unit,
    onFavoriteSortClick: (Boolean) -> Unit,
    onOrderEnabledClick: (Boolean) -> Unit,
    onDateEnabledClick: (Boolean) -> Unit,
    onPrioritySelected: (Action, Priority, Boolean) -> Unit,
    onStateSelected: (State) -> Unit,
    onSearchRangeAllClicked: (Boolean, Boolean) -> Unit,
    onToggleClicked: () -> Unit,
    onSetAllOrNothingClicked: (Boolean) -> Unit,
    onStatusLineUpdate: (StateEntity) -> Unit,
    tasks: LazyPagingItems<MemoWithNotebook>,
    toTaskScreen: () -> Unit,
    onSwipeToEdit: (Int, MemoWithNotebook) -> Unit,
    onFavoriteClick: (MemoWithNotebook) -> Unit,
    onLongClickApplied: (Long) -> Unit,
    selectedItems: SnapshotStateList<Long>,
    onStateChange: (MemoWithNotebook, State) -> Unit
) {
    StatusLine(
        uiState = uiState,
        prioritySortState = uiState.prioritySortState,
        orderEnabled = (uiState.dateOrderState == SortOption.CREATED_AT_ASC ||
                uiState.dateOrderState == SortOption.UPDATED_AT_ASC),
        dateEnabled = (uiState.dateOrderState == SortOption.CREATED_AT_ASC ||
                uiState.dateOrderState == SortOption.CREATED_AT_DESC),
        searchRangeAll = uiState.searchRangeAll,
        startDate = memoViewModel.startDate,
        endDate = memoViewModel.endDate,
        favoriteOn = memoViewModel.uiState.sortFavorite,
        onCloseClick = onDateRangeCloseClick,
        onFavoriteClick = onFavoriteSortClick,
        onOrderEnabledClick = onOrderEnabledClick,
        onDateEnabledClick = onDateEnabledClick,
        onPrioritySelected = onPrioritySelected,
        onStateSelected = onStateSelected,
        onRangeAllEnabledClick = onSearchRangeAllClicked,
        onToggleClicked = onToggleClicked,
        onSetAllOrNothingClicked = onSetAllOrNothingClicked,
        onStatusLineUpdate = onStatusLineUpdate,
    )

    ListContent(
        tasks = tasks,
        toTaskScreen = { index ->
            memoViewModel.setTaskScreenToViewerMode(tasks[index]!!.toMemoTask())
            memoViewModel.updateIndex(index)
            toTaskScreen()
        },
//                    onSwipeToDelete = { action, task ->
//                        // undo 처리를 위해서 데이터 동기화 필요
//                        memoViewModel.updateTaskContent(task)
//                        memoViewModel.handleActions(action, task.id)
//                    },
        onSwipeToEdit = onSwipeToEdit,
        header = true,
        dateEnabled = (uiState.dateOrderState == SortOption.CREATED_AT_ASC ||
                uiState.dateOrderState == SortOption.CREATED_AT_DESC),
        onFavoriteClick = onFavoriteClick,
        onLongClickApplied = onLongClickApplied,
        selectedItemsIds = selectedItems,
        onStateSelected = onStateChange,
    )
}