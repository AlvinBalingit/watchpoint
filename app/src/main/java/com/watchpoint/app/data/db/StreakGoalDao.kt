package com.watchpoint.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakGoalDao {
    @Query("SELECT * FROM streak_goal WHERE id = ${StreakGoalEntity.SINGLETON_ID}")
    fun observe(): Flow<StreakGoalEntity?>

    @Query("SELECT * FROM streak_goal WHERE synced = 0")
    suspend fun getUnsynced(): List<StreakGoalEntity>

    @Query("UPDATE streak_goal SET synced = 1 WHERE id = :id")
    suspend fun markSynced(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: StreakGoalEntity)
}
