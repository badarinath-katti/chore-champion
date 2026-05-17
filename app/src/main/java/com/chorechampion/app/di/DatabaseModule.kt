package com.chorechampion.app.di

import android.content.Context
import androidx.room.Room
import com.chorechampion.app.data.local.ChoreDatabase
import com.chorechampion.app.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
            .build()
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
