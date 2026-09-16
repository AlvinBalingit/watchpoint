package com.watchpoint.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/** One free-text reflection per week, keyed by that week's Monday (ISO date string). */
@Entity(tableName = "weekly_reflections")
data class WeeklyReflectionEntity(
    @PrimaryKey val weekStart: String,
    val text: String,
    val updatedAt: Long = 0L
)
