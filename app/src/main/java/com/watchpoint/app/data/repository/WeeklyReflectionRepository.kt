package com.watchpoint.app.data.repository

import com.watchpoint.app.data.db.WeeklyReflectionDao
import com.watchpoint.app.data.db.WeeklyReflectionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/** Persists the free-text reflection on the Weekly Summary screen, one per calendar week. */
class WeeklyReflectionRepository(private val dao: WeeklyReflectionDao) {

    fun observeForWeekOf(date: LocalDate): Flow<String> =
        dao.observe(weekStartOf(date).toString()).map { it?.text ?: "" }

    suspend fun save(date: LocalDate, text: String) {
        dao.upsert(
            WeeklyReflectionEntity(
                weekStart = weekStartOf(date).toString(),
                text = text,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    private fun weekStartOf(date: LocalDate): LocalDate =
        date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
}
