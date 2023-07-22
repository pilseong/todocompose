package net.pilseong.todocompose.data.model.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Update
import androidx.compose.ui.graphics.vector.ImageVector
import net.pilseong.todocompose.R

enum class MemoDateSortingOption(val label: Int, icon: ImageVector) {
    CREATED_AT(R.string.memo_sorting_option_created_at, Icons.Default.Create),
    UPDATED_AT(R.string.memo_sorting_option_updated_at, Icons.Default.Update),
    FINISHED_AT(R.string.memo_sorting_option_finished_at, Icons.Default.CheckCircle),
    DUE_DATE(R.string.memo_sorting_option_due_date, Icons.Default.LockClock),
}