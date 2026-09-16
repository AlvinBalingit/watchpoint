package com.watchpoint.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Single-row table (fixed [id]) tracking progress through the 5-day getting-started program. */
@Entity(tableName = "program_progress")
data class ProgramProgressEntity(
    @PrimaryKey val id: Int = SINGLETON_ID,
    val dayCompleted: Int,
    val synced: Boolean = false,
    val updatedAt: Long = 0L
) {
    companion object {
        const val SINGLETON_ID = 1
    }
}
