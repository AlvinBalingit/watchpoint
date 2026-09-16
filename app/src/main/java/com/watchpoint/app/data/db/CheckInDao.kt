package com.watchpoint.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckInDao {
    @Query("SELECT * FROM check_ins ORDER BY date DESC")
    fun observeAll(): Flow<List<CheckInEntity>>

    @Query("SELECT COUNT(*) FROM check_ins")
    suspend fun count(): Int

    @Query("SELECT mood FROM check_ins ORDER BY date DESC LIMIT 1")
    suspend fun getLatestMood(): String?

    @Query("SELECT * FROM check_ins WHERE synced = 0")
    suspend fun getUnsynced(): List<CheckInEntity>

    @Query("UPDATE check_ins SET synced = 1 WHERE date = :date")
    suspend fun markSynced(date: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CheckInEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<CheckInEntity>)
}
