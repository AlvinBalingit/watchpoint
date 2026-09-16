package com.watchpoint.app.checkin

import androidx.annotation.StringRes
import com.watchpoint.app.R

/** One day of the short structured onboarding-into-the-habit program. */
data class ProgramDay(
    val day: Int,
    @StringRes val title: Int,
    @StringRes val prompt: Int,
    val exerciseId: String?
)

/**
 * A fixed 5-day sequence introducing check-ins and a couple of exercises.
 * Progress through it is marked manually (not calendar-gated) since there is
 * no persistence layer to reliably enforce one day per real day across app
 * restarts - this is a session-scoped demo of the flow, same as the rest of
 * the app.
 */
val GETTING_STARTED_PROGRAM: List<ProgramDay> = listOf(
    ProgramDay(1, R.string.program_day1_title, R.string.program_day1_prompt, "breathing_4"),
    ProgramDay(2, R.string.program_day2_title, R.string.program_day2_prompt, "gratitude_note"),
    ProgramDay(3, R.string.program_day3_title, R.string.program_day3_prompt, "grounding_54321"),
    ProgramDay(4, R.string.program_day4_title, R.string.program_day4_prompt, "short_walk"),
    ProgramDay(5, R.string.program_day5_title, R.string.program_day5_prompt, null)
)
