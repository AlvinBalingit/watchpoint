package com.watchpoint.app.checkin

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

private val today = LocalDate.of(2026, 9, 16)

private fun entry(date: LocalDate) = CheckInEntry(
    date = date,
    mood = Mood.Neutral,
    stress = StressLevel.Moderate,
    readiness = Readiness.Moderate
)

class CurrentStreakTest {

    @Test
    fun `empty history is zero`() {
        assertEquals(0, currentStreak(emptyList(), today))
    }

    @Test
    fun `single check-in today is a streak of one`() {
        val history = listOf(entry(today))
        assertEquals(1, currentStreak(history, today))
    }

    @Test
    fun `consecutive days ending today counts all of them`() {
        val history = listOf(
            entry(today),
            entry(today.minusDays(1)),
            entry(today.minusDays(2))
        )
        assertEquals(3, currentStreak(history, today))
    }

    @Test
    fun `streak ending yesterday still counts when today has no check-in yet`() {
        // Regression test: this used to return 0 because the count always
        // started from `today`, so a real streak that just hadn't been
        // continued yet (it's morning, no check-in today) looked broken.
        val history = listOf(
            entry(today.minusDays(1)),
            entry(today.minusDays(2)),
            entry(today.minusDays(3))
        )
        assertEquals(3, currentStreak(history, today))
    }

    @Test
    fun `a streak with a real gap (more than one day) is zero, not preserved`() {
        // No check-in yesterday or today - unlike the "just not done today
        // yet" grace period, this is a genuine lapse and should reset to 0.
        val history = listOf(
            entry(today.minusDays(2)),
            entry(today.minusDays(3))
        )
        assertEquals(0, currentStreak(history, today))
    }

    @Test
    fun `gap in the middle stops counting`() {
        val history = listOf(
            entry(today),
            entry(today.minusDays(1)),
            // gap at today - 2
            entry(today.minusDays(3))
        )
        assertEquals(2, currentStreak(history, today))
    }
}
