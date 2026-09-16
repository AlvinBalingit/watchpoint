package com.watchpoint.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgramProgressDao {
    @Query("SELECT * FROM program_progress WHERE id = ${ProgramProgressEntity.SINGLETON_ID}")
    fun observe(): Flow<ProgramProgressEntity?>

    @Query("SELECT * FROM program_progress WHERE synced = 0")
    suspend fun getUnsynced(): List<ProgramProgressEntity>

    @Query("UPDATE program_progress SET synced = 1 WHERE id = :id")
    suspend fun markSynced(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ProgramProgressEntity)
}
