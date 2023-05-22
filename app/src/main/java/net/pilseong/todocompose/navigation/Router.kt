package net.pilseong.todocompose.navigation

import androidx.navigation.NavHostController
import net.pilseong.todocompose.data.model.TodoTask
import net.pilseong.todocompose.ui.viewmodel.MemoViewModel
import net.pilseong.todocompose.util.Action
import net.pilseong.todocompose.util.Constants.MEMO_DETAIL
import net.pilseong.todocompose.util.Constants.MEMO_LIST

class Router(
    private val navHostController: NavHostController,
    private val memoViewModel: MemoViewModel
) {
    val toListScreen: (Int?) -> Unit = { note_id ->
//        navHostController.navigate("$MEMO_LIST?/${action.name}") {
        navHostController.navigate("$MEMO_LIST?${note_id ?: ""}") {
            popUpTo(MEMO_LIST) { inclusive = true }
        }
    }
    val toTaskScreen: (tasks: List<TodoTask>) -> Unit = {
        memoViewModel.updateSnapshotTasks(it)
        // 화면 전환 시에는 action 을 초기화 해야 뒤로 가기 버튼을 눌렀을 때 오동작 을 예방할 수 있다.
        memoViewModel.updateAction(Action.NO_ACTION)
        navHostController.navigate(MEMO_DETAIL)
    }
}