package com.watchpoint.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OnboardingAnswersDao {
    @Query("SELECT * FROM onboarding_answers WHERE id = ${OnboardingAnswersEntity.SINGLETON_ID}")
    fun observe(): Flow<OnboardingAnswersEntity?>

    @Query("SELECT * FROM onboarding_answers WHERE synced = 0")
    suspend fun getUnsynced(): List<OnboardingAnswersEntity>

    @Query("UPDATE onboarding_answers SET synced = 1 WHERE id = :id")
    suspend fun markSynced(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: OnboardingAnswersEntity)
}
