package com.watchpoint.app.onboarding

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watchpoint.app.R
import com.watchpoint.app.data.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/** Where the person heard about WatchPoint (deck screen 6). */
enum class JoinSource(@StringRes val label: Int) {
    StoreSearch(R.string.source_store_search),
    StoreRecommendations(R.string.source_store_recs),
    FriendOrFamily(R.string.source_friend),
    Advertisement(R.string.source_ad),
    Social(R.string.source_social),
    Other(R.string.source_other)
}

/** Self-reported state of mind (deck screen 8). */
enum class MoodState(@StringRes val label: Int) {
    Difficult(R.string.mood_difficult),
    Ongoing(R.string.mood_ongoing),
    Okay(R.string.mood_okay)
}

/** Practices offered on the interests grid (deck screen 13). */
enum class Interest(@StringRes val label: Int) {
    Breathing(R.string.interest_breathing),
    Journaling(R.string.interest_journaling),
    Meditation(R.string.interest_meditation),
    MoodTracking(R.string.interest_mood),
    SelfLove(R.string.interest_selflove),
    Gratitude(R.string.interest_gratitude),
    Physical(R.string.interest_physical),
    Sleep(R.string.interest_sleep),
    Productivity(R.string.interest_productivity),
    TimeManagement(R.string.interest_time_management),
    SocialConnection(R.string.interest_social_connection),
    StudyFocus(R.string.interest_study_focus),
    EmotionalAwareness(R.string.interest_emotional_awareness),
    Relaxation(R.string.interest_relaxation),
    GoalSetting(R.string.interest_goal_setting),
    BoardExamPrep(R.string.interest_board_exam_prep)
}

/** Strength of the person's support network (deck screen 14). */
enum class SupportLevel(@StringRes val label: Int) {
    Excellent(R.string.support_excellent),
    Good(R.string.support_good),
    Limited(R.string.support_limited),
    Poor(R.string.support_poor)
}

/** Age bracket, matching criminology program year levels (deck screen 15). */
enum class AgeGroup(@StringRes val label: Int, val indicator: Int) {
    Range17To18(R.string.age_17_18, 1),
    Range19To20(R.string.age_19_20, 2),
    Range21To22(R.string.age_21_22, 3),
    Age23AndAbove(R.string.age_23_plus, 4)
}

/** Academic program the person is enrolled in. */
enum class Program(@StringRes val label: Int) {
    Criminology(R.string.program_criminology),
    ForensicScience(R.string.program_forensic_science),
    Other(R.string.program_other)
}

/** What brought the person to WatchPoint today - a distinct question from [JoinSource]. */
enum class ReasonForUsing(@StringRes val label: Int) {
    ExamStress(R.string.reason_exam_stress),
    FieldworkAdjustment(R.string.reason_fieldwork_adjustment),
    SleepIssues(R.string.reason_sleep_issues),
    Anxiety(R.string.reason_anxiety),
    RelationshipChallenges(R.string.reason_relationship_challenges),
    BigLifeChange(R.string.reason_big_life_change),
    SomethingElse(R.string.reason_something_else)
}

/** A wall-clock time of day, stored in 24h and rendered in 12h like the deck. */
data class TimeOfDay(val hour: Int, val minute: Int) {
    fun format(): String {
        val suffix = if (hour < 12) "AM" else "PM"
        val display = when {
            hour % 12 == 0 -> 12
            else -> hour % 12
        }
        return "%02d:%02d %s".format(display, minute, suffix)
    }
}

/**
 * Everything the onboarding flow collects.
 *
 * Answers are kept as transient Compose state while the flow is in progress
 * (exactly like before - being mid-onboarding is not something that should
 * survive process death), and persisted via [OnboardingRepository] only once
 * the flow completes, via [completeOnboarding].
 */
class OnboardingViewModel(private val repository: OnboardingRepository) : ViewModel() {

    /** Whether onboarding has already been completed - read once at app startup to pick the start destination. */
    val isOnboardedForStartup: Flow<Boolean> = repository.observeIsOnboarded()

    var source by mutableStateOf<JoinSource?>(null)
        private set

    var mood by mutableStateOf<MoodState?>(null)
        private set

    var wakeTime by mutableStateOf(TimeOfDay(7, 30))
        private set

    var bedTime by mutableStateOf(TimeOfDay(0, 0))
        private set

    var interests by mutableStateOf(emptySet<Interest>())
        private set

    var support by mutableStateOf<SupportLevel?>(null)
        private set

    var ageGroup by mutableStateOf<AgeGroup?>(null)
        private set

    var program by mutableStateOf<Program?>(null)
        private set

    var reasonsForUsing by mutableStateOf(emptySet<ReasonForUsing>())
        private set

    fun selectSource(value: JoinSource) {
        source = value
    }

    fun toggleReasonForUsing(value: ReasonForUsing) {
        reasonsForUsing = if (value in reasonsForUsing) reasonsForUsing - value else reasonsForUsing + value
    }

    fun selectProgram(value: Program) {
        program = value
    }

    fun selectMood(value: MoodState) {
        mood = value
    }

    fun selectWakeTime(value: TimeOfDay) {
        wakeTime = value
    }

    fun selectBedTime(value: TimeOfDay) {
        bedTime = value
    }

    fun toggleInterest(value: Interest) {
        interests = if (value in interests) interests - value else interests + value
    }

    fun selectSupport(value: SupportLevel) {
        support = value
    }

    fun selectAgeGroup(value: AgeGroup) {
        ageGroup = value
    }

    /** Persists the collected answers and marks onboarding as complete. */
    fun completeOnboarding() {
        val answers = OnboardingAnswers(
            source = source,
            mood = mood,
            wakeTime = wakeTime,
            bedTime = bedTime,
            interests = interests,
            support = support,
            ageGroup = ageGroup,
            program = program,
            reasonsForUsing = reasonsForUsing
        )
        viewModelScope.launch { repository.save(answers) }
    }
}
