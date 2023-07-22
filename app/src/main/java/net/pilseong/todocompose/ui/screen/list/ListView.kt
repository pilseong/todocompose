package net.pilseong.todocompose.ui.screen.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.paging.compose.LazyPagingItems
import net.pilseong.todocompose.data.model.ui.MemoDateSortingOption
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.data.model.ui.Priority
import net.pilseong.todocompose.data.model.ui.State
import net.pilseong.todocompose.data.model.ui.UserData
import net.pilseong.todocompose.ui.viewmodel.MemoViewModel
import net.pilseong.todocompose.ui.viewmodel.toMemoTask
import net.pilseong.todocompose.util.Action
import net.pilseong.todocompose.util.StateEntity

@Composable
fun ListView(
    uiState: UserData,
    memoViewModel: MemoViewModel,
    onDateRangeCloseClick: () -> Unit,
    onFavoriteSortClick: (Boolean) -> Unit,
    onOrderEnabledClick: (Boolean) -> Unit,
    onDateSortingChangeClick: (Action, MemoDateSortingOption, Boolean) -> Unit,
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
        startDate = memoViewModel.startDate,
        endDate = memoViewModel.endDate,
        onCloseClick = onDateRangeCloseClick,
        onFavoriteClick = onFavoriteSortClick,
        onOrderEnabledClick = onOrderEnabledClick,
        onDateSortingChangeClick = onDateSortingChangeClick,
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
        onSwipeToEdit = onSwipeToEdit,
        header = true,
        memoDateBaseOption = uiState.memoDateSortingState,
        onFavoriteClick = onFavoriteClick,
        onLongClickApplied = onLongClickApplied,
        selectedItemsIds = selectedItems,
        onStateSelected = onStateChange,
    )
}