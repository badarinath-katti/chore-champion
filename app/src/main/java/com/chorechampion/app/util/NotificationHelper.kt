package com.chorechampion.app.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.chorechampion.app.presentation.MainActivity
import com.chorechampion.app.R

object NotificationHelper {

    fun sendEvaluationNotification(
        context: Context,
        winnerName: String,
        winnerPoints: Float,
        loserPoints: Float,
        weekStartDate: Long
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "evaluation")
            putExtra("week_start", weekStartDate)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            Constants.NOTIFICATION_EVALUATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, Constants.CHANNEL_EVALUATION)
            .setSmallIcon(R.drawable.ic_trophy)
            .setContentTitle("🏆 Week Complete!")
            .setContentText("$winnerName wins with ${String.format("%.1f", winnerPoints)} points!")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$winnerName: ${String.format("%.1f", winnerPoints)} points\nRunner-up: ${String.format("%.1f", loserPoints)} points")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(Constants.NOTIFICATION_EVALUATION_ID, notification)
    }

    fun sendReminderNotification(
        context: Context,
        title: String,
        message: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            Constants.NOTIFICATION_REMINDER_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, Constants.CHANNEL_REMINDERS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(Constants.NOTIFICATION_REMINDER_ID, notification)
    }

    fun sendPartnerActivityNotification(
        context: Context,
        partnerName: String,
        activity: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            Constants.NOTIFICATION_PARTNER_ACTIVITY_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, Constants.CHANNEL_PARTNER_ACTIVITY)
            .setSmallIcon(R.drawable.ic_partner)
            .setContentTitle("Partner Update")
            .setContentText("$partnerName $activity")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(Constants.NOTIFICATION_PARTNER_ACTIVITY_ID, notification)
    }
}
