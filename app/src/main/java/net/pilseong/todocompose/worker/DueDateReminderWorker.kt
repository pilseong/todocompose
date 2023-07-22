package net.pilseong.todocompose.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class DueDateReminderWorker(
    private val context: Context,
    private val params: WorkerParameters,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {

        Log.d("PHILIP", "[DueDateReminderWorker] called inside doWork")
//        return withContext(Dispatchers.IO) {
//
            TODO()
//        }

//        return Result.retry()
    }
}