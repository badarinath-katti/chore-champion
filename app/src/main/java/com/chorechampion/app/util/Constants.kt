package com.chorechampion.app.util

object Constants {
    // Notification IDs
    const val NOTIFICATION_EVALUATION_ID = 1002
    const val NOTIFICATION_REMINDER_ID = 1001
    const val NOTIFICATION_PARTNER_ACTIVITY_ID = 1003
    
    // Notification Channels
    const val CHANNEL_EVALUATION = "evaluation_channel"
    const val CHANNEL_REMINDERS = "reminders_channel"
    const val CHANNEL_PARTNER_ACTIVITY = "partner_activity_channel"
    
    // WorkManager Tags
    const val WORK_TAG_DAILY_REMINDER = "daily_reminder"
    const val WORK_TAG_WEEKLY_EVALUATION = "weekly_evaluation"
    
    // Shared Preferences Keys
    const val PREF_NAME = "chore_champion_prefs"
    const val PREF_CURRENT_USER_ID = "current_user_id"
    const val PREF_ONBOARDING_COMPLETED = "onboarding_completed"
    
    // Scoring Constants
    const val MIN_WEIGHTAGE = 1
    const val MAX_WEIGHTAGE = 100
    const val DEFAULT_WEIGHTAGE = 10
    
    const val MIN_COMPLETION_PERCENTAGE = 0
    const val MAX_COMPLETION_PERCENTAGE = 100
    
    // Image Compression
    const val MAX_IMAGE_WIDTH = 1920
    const val MAX_IMAGE_HEIGHT = 1080
    const val IMAGE_QUALITY = 85
}
