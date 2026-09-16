package com.watchpoint.app.checkin

import androidx.annotation.StringRes
import com.watchpoint.app.R
import java.time.LocalDate

/** Self-reported mood, 5-point scale (Daily Check-In, input 1). */
enum class Mood(val emoji: String, @StringRes val label: Int, val score: Int) {
    VeryLow("😣", R.string.mood_very_low, 1),
    Low("😟", R.string.mood_low, 2),
    Neutral("😐", R.string.mood_neutral, 3),
    Good("🙂", R.string.mood_good, 4),
    Great("😄", R.string.mood_great, 5)
}

/** Self-reported stress, 1-5 (Daily Check-In, input 2). */
enum class StressLevel(@StringRes val label: Int, val score: Int) {
    VeryLow(R.string.stress_very_low, 1),
    Low(R.string.stress_low, 2),
    Moderate(R.string.stress_moderate, 3),
    High(R.string.stress_high, 4),
    VeryHigh(R.string.stress_very_high, 5)
}

/** Self-perceived readiness, 1-5 (Daily Check-In, input 3). */
enum class Readiness(@StringRes val label: Int, val score: Int) {
    VeryLow(R.string.readiness_very_low, 1),
    Low(R.string.readiness_low, 2),
    Moderate(R.string.readiness_moderate, 3),
    High(R.string.readiness_high, 4),
    VeryHigh(R.string.readiness_very_high, 5)
}

/** Activity Type tag (Daily Check-In, input 4). */
enum class ActivityTag(@StringRes val label: Int) {
    FieldTraining(R.string.activity_field_training),
    BoardExamReview(R.string.activity_board_review),
    CriminalJusticeCoursework(R.string.activity_cj_coursework),
    Practicum(R.string.activity_practicum),
    Extracurricular(R.string.activity_extracurricular)
}

/** Interaction Context tag (Daily Check-In, input 4). */
enum class InteractionTag(@StringRes val label: Int) {
    School(R.string.interaction_school),
    HomeFamily(R.string.interaction_home_family),
    PeerSocial(R.string.interaction_peer_social),
    PersonalRelationships(R.string.interaction_personal_relationships),
    PersonalResponsibilities(R.string.interaction_personal_responsibilities),
    None(R.string.interaction_none)
}

/** One completed daily check-in. */
data class CheckInEntry(
    val date: LocalDate,
    val mood: Mood,
    val stress: StressLevel,
    val readiness: Readiness,
    val activityTags: Set<ActivityTag> = emptySet(),
    val interactionTags: Set<InteractionTag> = emptySet(),
    val reflection: String? = null
)
