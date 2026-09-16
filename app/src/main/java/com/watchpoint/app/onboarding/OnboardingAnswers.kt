package com.watchpoint.app.onboarding

/** Everything collected during onboarding, as a plain persisted snapshot. */
data class OnboardingAnswers(
    val source: JoinSource?,
    val mood: MoodState?,
    val wakeTime: TimeOfDay,
    val bedTime: TimeOfDay,
    val interests: Set<Interest>,
    val support: SupportLevel?,
    val ageGroup: AgeGroup?,
    val program: Program?,
    val reasonsForUsing: Set<ReasonForUsing>
)
