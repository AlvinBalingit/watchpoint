package com.watchpoint.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * One free-form journal entry, independent of any specific day's check-in.
 *
 * Deletes are soft (pendingDelete = true, synced = false) so the sync pass can
 * push the removal to Firestore before the row is hard-deleted locally - the
 * Privacy Policy promises erasure, so the online copy must go too, but a
 * device that's offline when the user deletes still needs to remember to.
 */
@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String = "",
    val text: String,
    val createdAt: Long,
    val synced: Boolean = false,
    val updatedAt: Long = 0L,
    val pendingDelete: Boolean = false
)
