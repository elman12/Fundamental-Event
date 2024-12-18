package com.elmansidik.dicodingevent.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.elmansidik.dicodingevent.R

class DailyReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val eventName = inputData.getString(KEY_EVENT_NAME)
        val eventTime = inputData.getString(KEY_EVENT_TIME)

        return if (!eventName.isNullOrEmpty() && !eventTime.isNullOrEmpty()) {
            showNotification(eventName, eventTime)
            Result.success()
        } else {
            Result.failure()
        }
    }

    private fun showNotification(eventName: String, eventTime: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create the notification
        val notification = buildNotification(eventName, eventTime).build() // call build() here

        // Ensure the notification channel is created
        createNotificationChannel(notificationManager)

        // Notify the user
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(eventName: String, eventTime: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(eventName)
            .setContentText(eventTime)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "daily_reminder_channel"
        const val CHANNEL_NAME = "Daily Reminder"
        const val NOTIFICATION_ID = 1

        // Keys for input data
        const val KEY_EVENT_NAME = "event_name"
        const val KEY_EVENT_TIME = "event_time"
    }
}
