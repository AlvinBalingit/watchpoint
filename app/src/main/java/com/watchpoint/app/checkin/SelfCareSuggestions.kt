package com.watchpoint.app.checkin

import androidx.annotation.StringRes
import com.watchpoint.app.R
import com.watchpoint.app.onboarding.Interest

/** A single, non-clinical self-care nudge - a suggestion, never an instruction. */
data class SelfCareSuggestion(
    @StringRes val title: Int,
    @StringRes val body: Int,
    val matchingInterest: Interest? = null
)

private val CANDIDATES = listOf(
    SelfCareSuggestion(R.string.selfcare_breathing_title, R.string.selfcare_breathing_body, Interest.Breathing),
    SelfCareSuggestion(R.string.selfcare_break_title, R.string.selfcare_break_body, null),
    SelfCareSuggestion(R.string.selfcare_gratitude_title, R.string.selfcare_gratitude_body, Interest.Gratitude),
    SelfCareSuggestion(R.string.selfcare_journal_title, R.string.selfcare_journal_body, Interest.Journaling),
    SelfCareSuggestion(R.string.selfcare_movement_title, R.string.selfcare_movement_body, Interest.Physical),
    SelfCareSuggestion(R.string.selfcare_rest_title, R.string.selfcare_rest_body, Interest.Sleep)
)

/**
 * Picks a small handful of suggestions that fit today's check-in, preferring
 * ones that match what the person said they were interested in practicing
 * during onboarding so the nudge feels personal rather than generic.
 */
fun suggestionsFor(entry: CheckInEntry, interests: Set<Interest>): List<SelfCareSuggestion> {
    val relevant = mutableListOf<SelfCareSuggestion>()

    if (entry.stress.score >= 4) {
        relevant += CANDIDATES.first { it.title == R.string.selfcare_breathing_title }
    }
    if (entry.readiness.score <= 2) {
        relevant += CANDIDATES.first { it.title == R.string.selfcare_break_title }
    }
    if (entry.mood.score <= 2) {
        relevant += CANDIDATES.first { it.title == R.string.selfcare_gratitude_title }
        relevant += CANDIDATES.first { it.title == R.string.selfcare_journal_title }
    }
    if (relevant.isEmpty()) {
        relevant += CANDIDATES.first { it.title == R.string.selfcare_movement_title }
    }

    return relevant
        .distinct()
        .sortedByDescending { it.matchingInterest != null && it.matchingInterest in interests }
        .take(1)
}
