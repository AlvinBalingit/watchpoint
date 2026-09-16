package com.watchpoint.app.data.repository

import com.watchpoint.app.data.db.CheckInDao
import com.watchpoint.app.data.db.CheckInEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

/** Minimal fake - only the methods CheckInRepository actually calls need real behavior. */
private class FakeCheckInDao(private val rows: List<CheckInEntity>) : CheckInDao {
    override fun observeAll(): Flow<List<CheckInEntity>> = flowOf(rows)
    override suspend fun count(): Int = rows.size
    override suspend fun getLatestMood(): String? = rows.firstOrNull()?.mood
    override suspend fun getUnsynced(): List<CheckInEntity> = rows.filterNot { it.synced }
    override suspend fun markSynced(date: String) {}
    override suspend fun upsert(entity: CheckInEntity) {}
    override suspend fun upsertAll(entities: List<CheckInEntity>) {}
}

class CheckInRepositoryTest {

    @Test
    fun `renamed mood enum falls back to Neutral instead of crashing`() = runBlocking {
        // Regression test: toDomain() used to call Mood.valueOf(mood), which
        // throws IllegalArgumentException for any string that isn't a current
        // enum constant name - e.g. after a future rename of the Mood enum.
        val row = CheckInEntity(
            date = "2026-09-16",
            mood = "SomeOldRenamedValue",
            stress = "Moderate",
            readiness = "Moderate",
            activityTagsCsv = "",
            interactionTagsCsv = "",
            reflection = null
        )
        val repository = CheckInRepository(FakeCheckInDao(listOf(row)))

        val history = repository.observeHistory()
        var result: List<com.watchpoint.app.checkin.CheckInEntry>? = null
        history.collect { result = it }

        assertEquals(com.watchpoint.app.checkin.Mood.Neutral, result?.single()?.mood)
    }

    @Test
    fun `unknown activity tag is dropped rather than crashing`() = runBlocking {
        val row = CheckInEntity(
            date = "2026-09-16",
            mood = "Neutral",
            stress = "Moderate",
            readiness = "Moderate",
            activityTagsCsv = "BoardExamReview,SomeRemovedTag",
            interactionTagsCsv = "",
            reflection = null
        )
        val repository = CheckInRepository(FakeCheckInDao(listOf(row)))

        var result: List<com.watchpoint.app.checkin.CheckInEntry>? = null
        repository.observeHistory().collect { result = it }

        assertEquals(
            setOf(com.watchpoint.app.checkin.ActivityTag.BoardExamReview),
            result?.single()?.activityTags
        )
    }
}
