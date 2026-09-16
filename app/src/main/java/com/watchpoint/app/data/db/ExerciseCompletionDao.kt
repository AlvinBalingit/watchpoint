package com.watchpoint.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseCompletionDao {
    @Query("SELECT * FROM exercise_completions ORDER BY completedAt DESC")
    fun observeAll(): Flow<List<ExerciseCompletionEntity>>

    @Query("SELECT * FROM exercise_completions WHERE synced = 0")
    suspend fun getUnsynced(): List<ExerciseCompletionEntity>

    @Query("UPDATE exercise_completions SET synced = 1 WHERE id = :id")
    suspend fun markSynced(id: Long)

    @Insert
    suspend fun insert(entity: ExerciseCompletionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ExerciseCompletionEntity)

    @Query("UPDATE exercise_completions SET noteTitle = :noteTitle, noteText = :noteText, updatedAt = :updatedAt, synced = 0 WHERE id = :id")
    suspend fun updateNote(id: Long, noteTitle: String, noteText: String, updatedAt: Long)

    @Query("DELETE FROM exercise_completions WHERE id = :id")
    suspend fun delete(id: Long)
}
