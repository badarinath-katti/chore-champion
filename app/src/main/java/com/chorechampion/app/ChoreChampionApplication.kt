package com.chorechampion.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.chorechampion.app.domain.repository.ChoreRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class ChoreChampionApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var choreRepository: ChoreRepository

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        // Always ensure default categories exist on app start
        applicationScope.launch {
            choreRepository.initializeDefaultCategories()
        }
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
