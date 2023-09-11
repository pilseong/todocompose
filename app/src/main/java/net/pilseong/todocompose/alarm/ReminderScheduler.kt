package net.pilseong.todocompose.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Build
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import net.pilseong.todocompose.ui.viewmodel.TaskDetails
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderScheduler @Inject constructor(
    private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

    override fun start(taskDetails: TaskDetails) {
        Log.d("PHILIP", "[ReminderScheduler] started $taskDetails")
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("ID", taskDetails.id)
            putExtra("CONTENT", taskDetails.title)
            putExtra("DESCRIPTION", taskDetails.description)
            putExtra("DUE_DATE", taskDetails.dueDate!!.toEpochSecond() * 1000)
        }

        val target =
            (taskDetails.dueDate!!.toEpochSecond() * 1000) - taskDetails.reminderType.timeInMillis


        Log.d(
            "PHILIP",
            "[ReminderScheduler] calendar is ${
                ZonedDateTime.ofInstant(
                    Instant.ofEpochMilli(target),
                    ZoneId.systemDefault()
                )
            }"
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            Log.d("PHILIP", "[ReminderScheduler] inside checking permission")
            when {
                // If permission is granted, proceed with scheduling exact alarms.
                // 설정 할 때 과거 에 알람이 등록 된 경우는 취소 하고 등록 한다.
                alarmManager.canScheduleExactAlarms() -> {
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

                else -> {
                    // Ask users to go to exact alarm page in system settings.
                    val intentAlarm = Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    intentAlarm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intentAlarm)
                }
            }
        }
    }

    override fun cancel(id: Long) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                id.toInt(),
                Intent(context, ReminderReceiver::class.java),
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )

        Log.d("PHILIP", "ReminderScheduler canceled")
    }


}