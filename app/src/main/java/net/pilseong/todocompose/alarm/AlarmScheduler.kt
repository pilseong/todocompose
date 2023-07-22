package net.pilseong.todocompose.alarm

import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.ui.viewmodel.TaskDetails

interface AlarmScheduler {

    fun start(taskDetails: TaskDetails)

    fun cancel(taskDetails: TaskDetails)
}