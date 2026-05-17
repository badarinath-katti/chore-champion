package com.chorechampion.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.chorechampion.app.data.local.dao.*
import com.chorechampion.app.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        ChoreCategoryEntity::class,
        ChoreEntity::class,
        WeeklyAssignmentEntity::class,
        ChoreCompletionEntity::class,
        WeeklyEvaluationEntity::class,
        RewardEntity::class,
        ChallengeEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class ChoreDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun choreCategoryDao(): ChoreCategoryDao
    abstract fun choreDao(): ChoreDao
    abstract fun weeklyAssignmentDao(): WeeklyAssignmentDao
    abstract fun choreCompletionDao(): ChoreCompletionDao
    abstract fun weeklyEvaluationDao(): WeeklyEvaluationDao
    abstract fun rewardDao(): RewardDao
    abstract fun challengeDao(): ChallengeDao
}
