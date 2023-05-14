package net.pilseong.todocompose.navigation

import androidx.navigation.NavHostController
import net.pilseong.todocompose.data.model.TodoTask
import net.pilseong.todocompose.ui.viewmodel.SharedViewModel
import net.pilseong.todocompose.util.Action
import net.pilseong.todocompose.util.Constants.LIST_SCREEN
import net.pilseong.todocompose.util.Constants.TASK_SCREEN

class Router(
    private val navHostController: NavHostController,
    private val sharedViewModel: SharedViewModel
) {
    val toListScreen: (Action) -> Unit = { action ->
        navHostController.navigate("list/${action.name}") {
            popUpTo(LIST_SCREEN) { inclusive = true }
        }
    }
    val toTaskScreen: (tasks: List<TodoTask>) -> Unit = {
        sharedViewModel.updateSnapshotTasks(it)
        // 화면 전환 시에는 action 을 초기화 해야 뒤로 가기 버튼을 눌렀을 때 오동작 을 예방할 수 있다.
        sharedViewModel.updateAction(Action.NO_ACTION)
        navHostController.navigate(TASK_SCREEN)
    }
}