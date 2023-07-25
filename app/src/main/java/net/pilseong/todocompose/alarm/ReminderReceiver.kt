package net.pilseong.todocompose.alarm

import android.Manifest
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import net.pilseong.todocompose.R
import net.pilseong.todocompose.data.repository.TodoRepository
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import javax.inject.Inject


@AndroidEntryPoint
class ReminderReceiver(): BroadcastReceiver() {

    @Inject lateinit var notificationManager: NotificationManagerCompat
    @Inject lateinit var notificationCompatBuilder: NotificationCompat.Builder


    override fun onReceive(context: Context?, intent: Intent?) {

        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }


        if (intent != null) {

            val id = intent.getLongExtra("ID", -1)
            val title = intent.getStringExtra("CONTENT")
            val description = intent.getStringExtra("DESCRIPTION")
            val dueDateMilli = intent.getLongExtra("DUE_DATE", 0)

            val format = context.resources.getString(R.string.task_content_dateformat)
            val target =
                ZonedDateTime.ofInstant(Instant.ofEpochMilli(dueDateMilli), ZoneId.systemDefault())
            val reminderText = context.resources.getString(
                R.string.reminder_text,
                target.toLocalDateTime().format(DateTimeFormatter.ofPattern(format))
            )
            Log.d("PHILIP", "[ReminderReceiver] onReceive is called $id")

            notificationManager.notify(
                id.toInt(), notificationCompatBuilder
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(reminderText)
                    .setContentText(title)
                    .setContentInfo(description)
                    .build()
            )
        }
    }


}