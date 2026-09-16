package com.watchpoint.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalEntryDao {
    @Query("SELECT * FROM journal_entries WHERE pendingDelete = 0 ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE synced = 0 AND pendingDelete = 0")
    suspend fun getUnsynced(): List<JournalEntryEntity>

    @Query("SELECT * FROM journal_entries WHERE pendingDelete = 1")
    suspend fun getPendingDeletes(): List<JournalEntryEntity>

    @Query("UPDATE journal_entries SET synced = 1 WHERE id = :id")
    suspend fun markSynced(id: Long)

    @Insert
    suspend fun insert(entity: JournalEntryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: JournalEntryEntity)

    /** Marks the row for deletion; the sync pass removes it from Firestore, then calls [hardDelete]. */
    @Query("UPDATE journal_entries SET pendingDelete = 1, synced = 0 WHERE id = :id")
    suspend fun markPendingDelete(id: Long)

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun hardDelete(id: Long)

    @Query("UPDATE journal_entries SET title = :title, text = :text, updatedAt = :updatedAt, synced = 0 WHERE id = :id")
    suspend fun update(id: Long, title: String, text: String, updatedAt: Long)
}
