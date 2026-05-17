package com.chorechampion.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ChoreChampionApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_REMINDERS,
                    "Daily Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Daily reminders for pending chores"
                },
                NotificationChannel(
                    CHANNEL_EVALUATION,
                    "Weekly Evaluation",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Weekly evaluation results and notifications"
                },
                NotificationChannel(
                    CHANNEL_PARTNER_ACTIVITY,
                    "Partner Activity",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Notifications when your partner completes chores"
                }
            )

            val notificationManager = getSystemService(NotificationManager::class.java)
            channels.forEach { notificationManager.createNotificationChannel(it) }
        }
    }

    companion object {
        const val CHANNEL_REMINDERS = "chore_reminders"
        const val CHANNEL_EVALUATION = "weekly_evaluation"
        const val CHANNEL_PARTNER_ACTIVITY = "partner_activity"
    }
}
