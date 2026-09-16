package com.watchpoint.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/** One free-form journal entry, independent of any specific day's check-in. */
@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String = "",
    val text: String,
    val createdAt: Long,
    val synced: Boolean = false,
    val updatedAt: Long = 0L
)
