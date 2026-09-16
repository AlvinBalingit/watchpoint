package com.watchpoint.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/** One row per exercise completion, so repeats are preserved and both a count and a distinct-ids view are derivable. */
@Entity(tableName = "exercise_completions")
data class ExerciseCompletionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseId: String,
    val completedAt: Long,
    val noteTitle: String = "",
    val noteText: String = "",
    val synced: Boolean = false,
    val updatedAt: Long = 0L
)
