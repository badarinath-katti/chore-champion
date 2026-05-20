package com.chorechampion.app.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.chorechampion.app.data.local.ChoreDatabase
import com.chorechampion.app.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Singleton
import java.util.UUID

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideChoreDatabase(@ApplicationContext context: Context): ChoreDatabase {
        return Room.databaseBuilder(
            context,
            ChoreDatabase::class.java,
            "chore_champion_database"
        )
            .fallbackToDestructiveMigration() // For development, use migrations in production
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Seed default categories
                    CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                        seedDefaultCategories(db)
                    }
                }
            })
            .build()
    }

    private fun seedDefaultCategories(db: SupportSQLiteDatabase) {
        val categories = listOf(
            Triple("🧹", "Cleaning", "#4CAF50"),
            Triple("🍳", "Cooking", "#FF9800"),
            Triple("🛒", "Shopping", "#2196F3"),
            Triple("🧺", "Laundry", "#9C27B0"),
            Triple("🔧", "Maintenance", "#F44336"),
            Triple("📦", "Other", "#607D8B")
        )

        categories.forEach { (icon, name, color) ->
            val id = UUID.randomUUID().toString()
            db.execSQL(
                "INSERT INTO chore_categories (id, name, icon, color, created_at) VALUES (?, ?, ?, ?, ?)",
                arrayOf(id, name, icon, color, System.currentTimeMillis())
            )
        }
    }

    @Provides
    fun provideUserDao(database: ChoreDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideChoreCategoryDao(database: ChoreDatabase): ChoreCategoryDao {
        return database.choreCategoryDao()
    }

    @Provides
    fun provideChoreDao(database: ChoreDatabase): ChoreDao {
        return database.choreDao()
    }

    @Provides
    fun provideWeeklyAssignmentDao(database: ChoreDatabase): WeeklyAssignmentDao {
        return database.weeklyAssignmentDao()
    }

    @Provides
    fun provideChoreCompletionDao(database: ChoreDatabase): ChoreCompletionDao {
        return database.choreCompletionDao()
    }

    @Provides
    fun provideWeeklyEvaluationDao(database: ChoreDatabase): WeeklyEvaluationDao {
        return database.weeklyEvaluationDao()
    }

    @Provides
    fun provideRewardDao(database: ChoreDatabase): RewardDao {
        return database.rewardDao()
    }

    @Provides
    fun provideChallengeDao(database: ChoreDatabase): ChallengeDao {
        return database.challengeDao()
    }
}
