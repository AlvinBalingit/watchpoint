package com.watchpoint.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeeklyReflectionDao {
    @Query("SELECT * FROM weekly_reflections WHERE weekStart = :weekStart")
    fun observe(weekStart: String): Flow<WeeklyReflectionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: WeeklyReflectionEntity)
}
