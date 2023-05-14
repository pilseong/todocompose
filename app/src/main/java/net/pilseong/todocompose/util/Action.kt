package net.pilseong.todocompose.util

enum class Action {
    ADD,
    UPDATE,
    DELETE,
    DELETE_ALL,
    UNDO,
    PRIORITY_CHANGE,
    SORT_ORDER_CHANGE,
    SORT_DATE_CHANGE,
    NO_ACTION
}

fun String?.toAction(): Action {
    return when {
        this == "ADD" -> {
            Action.ADD
        }

        this == "UPDATE" -> {
            Action.UPDATE
        }

        this == "DELETE" -> {
            Action.DELETE
        }

        this == "DELETE_ALL" -> {
            Action.DELETE_ALL
        }

        this == "UNDO" -> {
            Action.UNDO
        }

        this == "PRIORITY_CHANGE" -> {
            Action.PRIORITY_CHANGE
        }

        this == "SORT_ORDER_CHANGE" -> {
            Action.SORT_ORDER_CHANGE
        }

        this == "SORT_DATE_CHANGE" -> {
            Action.SORT_DATE_CHANGE
        }

        else -> {
            Action.NO_ACTION
        }
    }
}