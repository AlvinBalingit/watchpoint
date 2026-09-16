package com.watchpoint.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/** One row per day checked in. [date] is stored as an ISO string (yyyy-MM-dd) and is the primary key. */
@Entity(tableName = "check_ins")
data class CheckInEntity(
    @PrimaryKey val date: String,
    val mood: String,
    val stress: String,
    val readiness: String,
    val activityTagsCsv: String,
    val interactionTagsCsv: String,
    val reflection: String?,
    /** Whether this row has been pushed to Firestore yet. */
    val synced: Boolean = false,
    /** Epoch millis of the last local write - used for last-write-wins sync and as the Firestore doc's own timestamp. */
    val updatedAt: Long = 0L
)
