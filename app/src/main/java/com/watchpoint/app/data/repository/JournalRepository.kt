package com.watchpoint.app.data.repository

import com.watchpoint.app.data.db.JournalEntryDao
import com.watchpoint.app.data.db.JournalEntryEntity
import kotlinx.coroutines.flow.Flow

/** Persists free-form journal entries, independent of the daily check-in flow. */
class JournalRepository(private val dao: JournalEntryDao) {

    fun observeEntries(): Flow<List<JournalEntryEntity>> = dao.observeAll()

    suspend fun addEntry(title: String, text: String) {
        dao.insert(
            JournalEntryEntity(
                title = title,
                text = text,
                createdAt = System.currentTimeMillis(),
                synced = false,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    /** Soft-deletes locally; the sync pass removes the Firestore copy and then hard-deletes this row. */
    suspend fun deleteEntry(id: Long) {
        dao.markPendingDelete(id)
    }

    suspend fun updateEntry(id: Long, title: String, text: String) {
        dao.update(id, title, text, System.currentTimeMillis())
    }
}
