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
import net.pilseong.todocompose.util.Action

// enabled 가 true 일 경우만 팝업이 뜬다
@Composable
fun DisplaySnackBar(
    snackBarHostState: SnackbarHostState,
    action: Action,
    enabled: ByteArray,
    title: String,
    range: Boolean = false,
    duration: SnackbarDuration = SnackbarDuration.Short,
    buttonClicked: (Action, SnackbarResult) -> Unit,
    actionAfterPopup: (Action) -> Unit,
    orderState: SortOption,
    memoDateSortingOption: MemoDateSortingOption,
    startDate: Long?,
    endDate: Long?,
) {

    val message = when (action) {
        Action.ADD ->
            title + " " + stringResource(id = R.string.new_task_added_message)

        Action.UPDATE ->
            title + " " + stringResource(id = R.string.task_updated_message)

        Action.DELETE ->
            title + " " + stringResource(id = R.string.task_deleted_message)

        Action.DELETE_ALL ->
            stringResource(id = R.string.all_tasks_deleted_message)

        Action.UNDO ->
            title + " " + stringResource(id = R.string.all_tasks_restored_message)

        Action.PRIORITY_CHANGE ->
            stringResource(id = R.string.snackbar_message_priority_change)

        Action.SORT_ORDER_CHANGE ->
            when (orderState) {
                SortOption.DESC -> stringResource(id = R.string.snackbar_message_order_desc_change)
                SortOption.ASC -> stringResource(id = R.string.snackbar_message_order_asc_change)
            }

        Action.MEMO_SORT_DATE_BASE_CHANGE ->
            when (memoDateSortingOption) {
                MemoDateSortingOption.CREATED_AT -> stringResource(id = R.string.snackbar_message_date_created_at_change)
                MemoDateSortingOption.UPDATED_AT -> stringResource(id = R.string.snackbar_message_date_updated_at_change)
                MemoDateSortingOption.FINISHED_AT -> stringResource(id = R.string.snackbar_message_date_finished_at_change)
                MemoDateSortingOption.DUE_DATE -> stringResource(id = R.string.snackbar_message_date_due_date_change)
            }

        Action.SEARCH_WITH_DATE_RANGE ->
            if (startDate == null && endDate == null)
                stringResource(id = R.string.snackbar_message_date_range_cancelled)
            else
                stringResource(id = R.string.snackbar_message_date_range_applied)

        Action.SORT_FAVORITE_CHANGE ->
            stringResource(id = R.string.snackbar_favorite_change_message)

        Action.DELETE_SELECTED_ITEMS ->
            stringResource(id = R.string.snackbar_selected_items_deleted_message)

        Action.NOTEBOOK_CHANGE ->
            stringResource(id = R.string.snackbar_changed_notebook_message)

        Action.MOVE_TO ->
            stringResource(id = R.string.snackbar_move_to_message)

        Action.COPY_TO ->
            stringResource(id = R.string.snackbar_copy_to_message)

        Action.SEARCH_RANGE_CHANGE ->
            if (range)
                stringResource(id = R.string.snackbar_all_range_change)
            else
                stringResource(id = R.string.snackbar_single_range_change)

        else -> {
            ""
        }
    }

    val label = if (action == Action.DELETE)
        stringResource(id = R.string.snack_bar_undo_label)
    else "OK"

    // enabled 는 이벤트 가 발생한 경우를 정확 하게 구분 하기 위한 변수
    LaunchedEffect(key1 = enabled) {
        Log.d("PHILIP", "[DisplaySnackBar]snack bar with $action")
        if (action != Action.NO_ACTION) {
            Log.d("PHILIP", "[DisplaySnackBar]snack bar popped up $action")
            actionAfterPopup(Action.NO_ACTION)
            val snackBarResult = snackBarHostState.showSnackbar(
                message = message,
                actionLabel = label,
                duration = duration
            )
            buttonClicked(action, snackBarResult)
        }
    }
}
