package com.watchpoint.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Single-row table (fixed [id]) holding the user's committed streak goal, if any. */
@Entity(tableName = "streak_goal")
data class StreakGoalEntity(
    @PrimaryKey val id: Int = SINGLETON_ID,
    val targetDays: Int,
    /** Non-null once a goal has been committed to. */
    val committedAt: Long?,
    val synced: Boolean = false,
    val updatedAt: Long = 0L
) {
    companion object {
        const val SINGLETON_ID = 1
    }
}
