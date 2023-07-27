package net.pilseong.todocompose.ui.screen.list

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.model.ui.MemoDateSortingOption
import net.pilseong.todocompose.data.model.ui.SortOption

// enabled 가 true 일 경우만 팝업이 뜬다
@Composable
fun DisplaySnackBar(
    snackBarHostState: SnackbarHostState,
    memoAction: MemoAction,
    enabled: ByteArray,
    title: String,
    range: Boolean = false,
    duration: SnackbarDuration = SnackbarDuration.Short,
    buttonClicked: (MemoAction, SnackbarResult) -> Unit,
    actionAfterPopup: (MemoAction) -> Unit,
    orderState: SortOption,
    memoDateSortingOption: MemoDateSortingOption,
    startDate: Long?,
    endDate: Long?,
) {

    val message = when (memoAction) {
        MemoAction.ADD ->
            title + " " + stringResource(id = R.string.new_task_added_message)

        MemoAction.UPDATE ->
            title + " " + stringResource(id = R.string.task_updated_message)

        MemoAction.DELETE ->
            title + " " + stringResource(id = R.string.task_deleted_message)

        MemoAction.DELETE_ALL ->
            stringResource(id = R.string.all_tasks_deleted_message)

        MemoAction.UNDO ->
            title + " " + stringResource(id = R.string.all_tasks_restored_message)

        MemoAction.PRIORITY_CHANGE ->
            stringResource(id = R.string.snackbar_message_priority_change)

        MemoAction.SORT_ORDER_CHANGE ->
            when (orderState) {
                SortOption.DESC -> stringResource(id = R.string.snackbar_message_order_desc_change)
                SortOption.ASC -> stringResource(id = R.string.snackbar_message_order_asc_change)
            }

        MemoAction.MEMO_SORT_DATE_BASE_CHANGE ->
            when (memoDateSortingOption) {
                MemoDateSortingOption.CREATED_AT -> stringResource(id = R.string.snackbar_message_date_created_at_change)
                MemoDateSortingOption.UPDATED_AT -> stringResource(id = R.string.snackbar_message_date_updated_at_change)
                MemoDateSortingOption.FINISHED_AT -> stringResource(id = R.string.snackbar_message_date_finished_at_change)
                MemoDateSortingOption.DUE_DATE -> stringResource(id = R.string.snackbar_message_date_due_date_change)
            }

        MemoAction.SEARCH_WITH_DATE_RANGE ->
            if (startDate == null && endDate == null)
                stringResource(id = R.string.snackbar_message_date_range_cancelled)
            else
                stringResource(id = R.string.snackbar_message_date_range_applied)

        MemoAction.SORT_FAVORITE_CHANGE ->
            stringResource(id = R.string.snackbar_favorite_change_message)

        MemoAction.DELETE_SELECTED_ITEMS ->
            stringResource(id = R.string.snackbar_selected_items_deleted_message)

        MemoAction.NOTEBOOK_CHANGE ->
            stringResource(id = R.string.snackbar_changed_notebook_message)

        MemoAction.MOVE_TO ->
            stringResource(id = R.string.snackbar_move_to_message)

        MemoAction.COPY_TO ->
            stringResource(id = R.string.snackbar_copy_to_message)

        MemoAction.SEARCH_RANGE_CHANGE ->
            if (range)
                stringResource(id = R.string.snackbar_all_range_change)
            else
                stringResource(id = R.string.snackbar_single_range_change)

        else -> {
            ""
        }
    }

    val label = if (memoAction == MemoAction.DELETE)
        stringResource(id = R.string.snack_bar_undo_label)
    else "OK"

    // enabled 는 이벤트 가 발생한 경우를 정확 하게 구분 하기 위한 변수
    LaunchedEffect(key1 = enabled) {
        Log.d("PHILIP", "[DisplaySnackBar]snack bar with $memoAction")
        if (memoAction != MemoAction.NO_ACTION) {
            Log.d("PHILIP", "[DisplaySnackBar]snack bar popped up $memoAction")
            actionAfterPopup(MemoAction.NO_ACTION)
            val snackBarResult = snackBarHostState.showSnackbar(
                message = message,
                actionLabel = label,
                duration = duration
            )
            buttonClicked(memoAction, snackBarResult)
        }
    }
}
