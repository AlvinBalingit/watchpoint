package com.watchpoint.app.data.repository

import com.watchpoint.app.checkin.ActivityTag
import com.watchpoint.app.checkin.CheckInEntry
import com.watchpoint.app.checkin.InteractionTag
import com.watchpoint.app.checkin.Mood
import com.watchpoint.app.checkin.Readiness
import com.watchpoint.app.checkin.StressLevel
import com.watchpoint.app.data.db.CheckInDao
import com.watchpoint.app.data.db.CheckInEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

/** Persists daily check-ins. This is the seam a future sync API would sit behind. */
class CheckInRepository(private val dao: CheckInDao) {

    fun observeHistory(): Flow<List<CheckInEntry>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    suspend fun submitCheckIn(entry: CheckInEntry) {
        dao.upsert(entry.toEntity(synced = false))
    }

    private fun CheckInEntry.toEntity(synced: Boolean) = CheckInEntity(
        date = date.toString(),
        mood = mood.name,
        stress = stress.name,
        readiness = readiness.name,
        activityTagsCsv = activityTags.joinToString(",") { it.name },
        interactionTagsCsv = interactionTags.joinToString(",") { it.name },
        reflection = reflection,
        synced = synced,
        updatedAt = System.currentTimeMillis()
    )

    private fun CheckInEntity.toDomain() = CheckInEntry(
        date = LocalDate.parse(date),
        mood = Mood.valueOf(mood),
        stress = StressLevel.valueOf(stress),
        readiness = Readiness.valueOf(readiness),
        activityTags = activityTagsCsv.toEnumSet(),
        interactionTags = interactionTagsCsv.toEnumSet(),
        reflection = reflection
    )

    /**
     * Parses a comma-joined set of enum names, silently dropping any name
     * that no longer matches a constant (e.g. a tag renamed in a later
     * app version) rather than crashing on old persisted rows.
     */
    private inline fun <reified T : Enum<T>> String.toEnumSet(): Set<T> =
        if (isBlank()) {
            emptySet()
        } else {
            split(",").filter { it.isNotBlank() }.mapNotNull { name ->
                enumValues<T>().firstOrNull { it.name == name }
            }.toSet()
        }
}
