package com.ruslan.movieapp.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.Calendar

object NotificationScheduler {

    private const val TAG = "Notification"

    fun scheduleReminder(context: Context, time: String, userName: String) {
        Log.d(TAG, "scheduleReminder called: time=$time, userName=$userName")

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("user_name", userName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val parts = time.split(":")
        if (parts.size != 2) {
            Log.e(TAG, "Invalid time format: $time")
            return
        }

        val hour = parts[0].toIntOrNull() ?: run {
            Log.e(TAG, "Invalid hour: ${parts[0]}")
            return
        }
        val minute = parts[1].toIntOrNull() ?: run {
            Log.e(TAG, "Invalid minute: ${parts[1]}")
            return
        }

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
                Log.d(TAG, "Time already passed, rescheduled to tomorrow: $time")
            }
        }

        Log.d(TAG, "Scheduled time: ${calendar.time}")
        Log.d(TAG, "Current time: ${System.currentTimeMillis()}")
        Log.d(TAG, "Difference: ${calendar.timeInMillis - System.currentTimeMillis()} ms")

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
                Log.d(TAG, "Alarm scheduled with setExactAndAllowWhileIdle")
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
                Log.d(TAG, "Alarm scheduled with setExact")
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException when scheduling alarm", e)
            // Fallback
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    fun cancelReminder(context: Context) {
        Log.d(TAG, "cancelReminder called")
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
        Log.d(TAG, "Alarm cancelled")
    }
}