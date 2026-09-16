package com.watchpoint.app.data.repository

import com.watchpoint.app.data.db.StreakGoalDao
import com.watchpoint.app.data.db.StreakGoalEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class StreakGoal(val targetDays: Int)

/** Persists the user's committed streak goal, if any. */
class StreakGoalRepository(private val dao: StreakGoalDao) {

    fun observeGoal(): Flow<StreakGoal?> = dao.observe().map { entity ->
        entity?.takeIf { it.committedAt != null }?.let { StreakGoal(it.targetDays) }
    }

    suspend fun commit(targetDays: Int) {
        dao.upsert(
            StreakGoalEntity(
                targetDays = targetDays,
                committedAt = System.currentTimeMillis(),
                synced = false,
                updatedAt = System.currentTimeMillis()
            )
        )
    }
}
