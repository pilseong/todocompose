package net.pilseong.todocompose.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.util.Log
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook
import net.pilseong.todocompose.ui.viewmodel.TaskDetails
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderScheduler @Inject constructor(
    private val context: Context
): AlarmScheduler {

    private val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

    override fun start(taskDetails: TaskDetails) {
        Log.d("PHILIP", "[ReminderScheduler] started $taskDetails")
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("CONTENT", taskDetails.title)
            putExtra("DESCRIPTION", taskDetails.description)
            putExtra("DUE_DATE", taskDetails.dueDate!!.toEpochSecond() * 1000)
        }

//        val calendar: Calendar = Calendar.getInstance().apply {
//            timeInMillis = System.currentTimeMillis()
//            set(Calendar.MINUTE, this.get(Calendar.MINUTE) + 1)
//            set(Calendar.SECOND, 0)
//            set(Calendar.MILLISECOND, 0)
//        }

        val target = (taskDetails.dueDate!!.toEpochSecond() * 1000) - taskDetails.reminderType.timeInMillis


        Log.d("PHILIP", "[ReminderScheduler] calendar is ${ZonedDateTime.ofInstant(Instant.ofEpochMilli(target), ZoneId.systemDefault())}")

        alarmManager.setExact(
            AlarmManager.RTC,
            target,
            PendingIntent.getBroadcast(
                context,
                taskDetails.id.toInt(),
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun cancel(taskDetails: TaskDetails) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                taskDetails.id.toInt(),
                Intent(context, ReminderReceiver::class.java),
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )

        Log.d("PHILIP", "ReminderScheduler canceled")
    }


}