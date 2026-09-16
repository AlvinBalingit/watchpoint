package com.watchpoint.app.data.repository

import com.watchpoint.app.data.db.ExerciseCompletionDao
import com.watchpoint.app.data.db.ExerciseCompletionEntity
import kotlinx.coroutines.flow.Flow

/** Persists exercise completions - one row per completion, repeats included. */
class ExerciseRepository(private val dao: ExerciseCompletionDao) {

    fun observeCompletions(): Flow<List<ExerciseCompletionEntity>> = dao.observeAll()

    suspend fun recordCompletion(exerciseId: String, noteTitle: String = "", noteText: String = "") {
        val now = System.currentTimeMillis()
        dao.insert(
            ExerciseCompletionEntity(
                exerciseId = exerciseId,
                completedAt = now,
                noteTitle = noteTitle,
                noteText = noteText,
                synced = false,
                updatedAt = now
            )
        )
    }

    suspend fun updateNote(id: Long, noteTitle: String, noteText: String) {
        dao.updateNote(id, noteTitle, noteText, System.currentTimeMillis())
    }

    suspend fun deleteCompletion(id: Long) {
        dao.delete(id)
    }
}
