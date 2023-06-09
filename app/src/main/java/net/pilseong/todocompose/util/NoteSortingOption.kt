package net.pilseong.todocompose.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Update
import androidx.compose.ui.graphics.vector.ImageVector
import net.pilseong.todocompose.R

enum class NoteSortingOption(val label: Int, resourceId: ImageVector) {
    ACCESS_AT(R.string.note_sort_accessed_at_label, Icons.Default.AccessTime),
    UPDATED_AT(R.string.note_sort_updated_at_label, Icons.Default.Update),
    CREATED_AT(R.string.note_sort_created_at_label, Icons.Default.Create),
}