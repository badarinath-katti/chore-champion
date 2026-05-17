package com.chorechampion.app.di

import com.chorechampion.app.data.repository.*
import com.chorechampion.app.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindChoreRepository(
        choreRepositoryImpl: ChoreRepositoryImpl
    ): ChoreRepository

    @Binds
    @Singleton
    abstract fun bindWeeklyAssignmentRepository(
        weeklyAssignmentRepositoryImpl: WeeklyAssignmentRepositoryImpl
    ): WeeklyAssignmentRepository

    @Binds
    @Singleton
    abstract fun bindEvaluationRepository(
        evaluationRepositoryImpl: EvaluationRepositoryImpl
    ): EvaluationRepository

    @Binds
    @Singleton
    abstract fun bindRewardRepository(
        rewardRepositoryImpl: RewardRepositoryImpl
    ): RewardRepository

    @Binds
    @Singleton
    abstract fun bindChallengeRepository(
        challengeRepositoryImpl: ChallengeRepositoryImpl
    ): ChallengeRepository
}
