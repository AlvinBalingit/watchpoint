package com.watchpoint.app.data.repository

import com.watchpoint.app.data.db.ProgramProgressDao
import com.watchpoint.app.data.db.ProgramProgressEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Persists progress through the 5-day getting-started program. */
class ProgramRepository(private val dao: ProgramProgressDao) {

    fun observeDayCompleted(): Flow<Int> = dao.observe().map { it?.dayCompleted ?: 0 }

    suspend fun setDayCompleted(day: Int) {
        dao.upsert(
            ProgramProgressEntity(
                dayCompleted = day.coerceIn(0, 5),
                synced = false,
                updatedAt = System.currentTimeMillis()
            )
        )
    }
}
