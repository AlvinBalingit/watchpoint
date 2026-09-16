package com.watchpoint.app.checkin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watchpoint.app.data.db.ExerciseCompletionEntity
import com.watchpoint.app.data.repository.CheckInRepository
import com.watchpoint.app.data.repository.ExerciseRepository
import com.watchpoint.app.data.repository.ProgramRepository
import com.watchpoint.app.data.repository.SettingsRepository
import com.watchpoint.app.data.repository.StreakGoal
import com.watchpoint.app.data.repository.StreakGoalRepository
import com.watchpoint.app.data.repository.WeeklyReflectionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.roundToInt

/** Everything the Dashboard needs, recomputed whenever history or exercise completions change. */
data class DashboardState(
    val latestEntry: CheckInEntry?,
    val todayCheckedIn: Boolean,
    val currentStreak: Int,
    val weeklyConsistency: Pair<Int, Int>,
    val checkedInDatesThisWeek: Set<LocalDate>,
    val mentalLoadWarning: Boolean,
    val gardenProgress: Int
)

/**
 * Everything the daily check-in flow reads and writes.
 *
 * Backed by Room via [CheckInRepository]/[ExerciseRepository]/[ProgramRepository],
 * so history, exercise completions and program progress now survive app
 * restarts. Draft (in-progress) answers stay as plain Compose state - they
 * are transient form state and should not survive process death mid-flow.
 */
class CheckInViewModel(
    private val checkInRepository: CheckInRepository,
    private val exerciseRepository: ExerciseRepository,
    private val programRepository: ProgramRepository,
    private val streakGoalRepository: StreakGoalRepository,
    private val weeklyReflectionRepository: WeeklyReflectionRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    companion object {
        private val MILESTONES = listOf(3, 7, 14, 30)
    }

    val history: StateFlow<List<CheckInEntry>> = checkInRepository.observeHistory()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val completedExerciseIds: StateFlow<Set<String>> = exerciseRepository.observeCompletions()
        .map { completions -> completions.map { it.exerciseId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    val exerciseNotes: StateFlow<Map<String, List<ExerciseCompletionEntity>>> = exerciseRepository.observeCompletions()
        .map { completions ->
            completions
                .groupBy { it.exerciseId }
                .mapValues { (_, rows) -> rows.sortedByDescending { it.completedAt } }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    val exerciseCompletionCount: StateFlow<Int> = exerciseRepository.observeCompletions()
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val programDayCompleted: StateFlow<Int> = programRepository.observeDayCompleted()
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val streakGoal: StateFlow<StreakGoal?> = streakGoalRepository.observeGoal()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val weeklyReflection: StateFlow<String> = weeklyReflectionRepository.observeForWeekOf(LocalDate.now())
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    fun saveWeeklyReflection(text: String) {
        viewModelScope.launch { weeklyReflectionRepository.save(LocalDate.now(), text) }
    }

    /** Single reactive source for everything the Dashboard shows - recomputes on any relevant change. */
    val dashboardState: StateFlow<DashboardState> = combine(history, exerciseCompletionCount) { history, exerciseCount ->
        val sortedDesc = history.sortedByDescending { it.date }
        val streak = currentStreak(sortedDesc)
        val today = LocalDate.now()
        val last7 = (0..6).map { today.minusDays(it.toLong()) }.toSet()
        val checkedInDays = history.map { it.date }.toSet()
        val recent3 = sortedDesc.take(3)
        val mentalLoadWarning = recent3.size >= 3 &&
            (recent3.all { it.stress.score >= 4 } || recent3.all { it.readiness.score <= 2 })
        val streakShare = (streak.coerceAtMost(14) / 14f) * 60f
        val exerciseShare = (exerciseCount.coerceAtMost(10) / 10f) * 40f

        DashboardState(
            latestEntry = history.maxByOrNull { it.date },
            todayCheckedIn = history.any { it.date == today },
            currentStreak = streak,
            weeklyConsistency = last7.count { it in checkedInDays } to 7,
            checkedInDatesThisWeek = last7.filter { it in checkedInDays }.toSet(),
            mentalLoadWarning = mentalLoadWarning,
            gardenProgress = (streakShare + exerciseShare).roundToInt()
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        DashboardState(null, false, 0, 0 to 7, emptySet(), false, 0)
    )

    /**
     * Persisted via SettingsRepository/DataStore (not plain Compose state) so
     * the choice survives process death instead of silently resetting to off
     * every time the app is reopened.
     */
    val highDemandMode: StateFlow<Boolean> = settingsRepository.highDemandMode
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // ---- draft answers for the check-in in progress - transient, not persisted ----

    var draftMood by mutableStateOf<Mood?>(null)
        private set

    var draftStress by mutableStateOf<StressLevel?>(null)
        private set

    var draftReadiness by mutableStateOf<Readiness?>(null)
        private set

    var draftActivityTags by mutableStateOf(emptySet<ActivityTag>())
        private set

    var draftInteractionTags by mutableStateOf(emptySet<InteractionTag>())
        private set

    var draftReflection by mutableStateOf("")
        private set

    /** Milestone streak length hit by the check-in just submitted, or null. Read once by CheckInDoneScreen. */
    var milestoneReached by mutableStateOf<Int?>(null)
        private set

    fun selectMood(value: Mood) {
        draftMood = value
    }

    fun selectStress(value: StressLevel) {
        draftStress = value
    }

    fun selectReadiness(value: Readiness) {
        draftReadiness = value
    }

    fun toggleActivityTag(value: ActivityTag) {
        draftActivityTags = if (value in draftActivityTags) draftActivityTags - value else draftActivityTags + value
    }

    fun toggleInteractionTag(value: InteractionTag) {
        draftInteractionTags =
            if (value in draftInteractionTags) draftInteractionTags - value else draftInteractionTags + value
    }

    fun updateReflection(value: String) {
        draftReflection = value
    }

    fun selectHighDemandMode(value: Boolean) {
        viewModelScope.launch { settingsRepository.setHighDemandMode(value) }
    }

    fun completeExercise(id: String, noteTitle: String = "", noteText: String = "") {
        viewModelScope.launch { exerciseRepository.recordCompletion(id, noteTitle, noteText) }
    }

    fun updateExerciseNote(id: Long, noteTitle: String, noteText: String) {
        viewModelScope.launch { exerciseRepository.updateNote(id, noteTitle, noteText) }
    }

    fun deleteExerciseNote(id: Long) {
        viewModelScope.launch { exerciseRepository.deleteCompletion(id) }
    }

    fun completeProgramDay() {
        val next = (programDayCompleted.value + 1).coerceAtMost(5)
        viewModelScope.launch { programRepository.setDayCompleted(next) }
    }

    fun commitStreakGoal(targetDays: Int) {
        viewModelScope.launch { streakGoalRepository.commit(targetDays) }
    }

    /**
     * Commits the draft as today's entry (or replaces it, if already checked
     * in today) and clears the draft. Suspends until the write - and
     * [milestoneReached] - are settled, so a caller can reliably branch on
     * the milestone right after this returns (e.g. to route to a
     * celebration screen) instead of racing the background write.
     */
    suspend fun submitCheckIn() {
        // Quick mode (highDemandMode) skips the mood step entirely, so there's
        // no draft mood to require here - falling back to Neutral keeps the
        // check-in from being silently dropped (it used to bail out at this
        // point because draftMood was never set).
        val mood = draftMood ?: if (highDemandMode.value) Mood.Neutral else return
        val stress = draftStress ?: return
        val readiness = draftReadiness ?: return

        val entry = CheckInEntry(
            date = LocalDate.now(),
            mood = mood,
            stress = stress,
            readiness = readiness,
            activityTags = draftActivityTags,
            interactionTags = draftInteractionTags,
            reflection = draftReflection.trim().ifBlank { null }
        )

        checkInRepository.submitCheckIn(entry)
        milestoneReached = MILESTONES.firstOrNull { it == currentStreakFor(entry) }
        clearDraft()
    }

    /** Streak as it will be right after [entry] is committed, computed against the pre-commit history. */
    private fun currentStreakFor(entry: CheckInEntry): Int {
        val withEntry = (history.value.filterNot { it.date == entry.date } + entry).sortedByDescending { it.date }
        return currentStreak(withEntry)
    }

    private fun clearDraft() {
        draftMood = null
        draftStress = null
        draftReadiness = null
        draftActivityTags = emptySet()
        draftInteractionTags = emptySet()
        draftReflection = ""
    }

    /** Non-clinical, own-data-only feedback lines shown right after a check-in. */
    fun feedbackMessages(): List<String> {
        val sorted = history.value.sortedByDescending { it.date }
        val messages = mutableListOf<String>()

        val streak = currentStreak(sorted)
        if (streak >= 3) {
            messages += "You've checked in consistently for $streak days in a row."
        }

        if (sorted.size >= 4) {
            val latest = sorted.first()
            val priorAvgStress = sorted.drop(1).take(6).map { it.stress.score }.average()
            if (latest.stress.score > priorAvgStress + 0.5) {
                messages += "Stress has been higher than usual lately."
            } else if (latest.stress.score < priorAvgStress - 0.5) {
                messages += "Stress looks lower than your recent average today."
            }
        }

        val highStressDays = sorted.take(7).filter { it.stress.score >= 4 }
        if (highStressDays.isNotEmpty() &&
            highStressDays.count { ActivityTag.BoardExamReview in it.activityTags } >= highStressDays.size / 2 + 1
        ) {
            messages += "Board exam review often appears on your higher-stress days."
        }

        val homeStressDays = sorted.take(7).filter { it.stress.score >= 4 }
        if (homeStressDays.isNotEmpty() &&
            homeStressDays.count { InteractionTag.HomeFamily in it.interactionTags } >= homeStressDays.size / 2 + 1
        ) {
            messages += "Stress levels appear higher during weeks with increased home responsibilities."
        }

        if (messages.isEmpty()) {
            messages += "Thanks for checking in today."
        }

        return messages
    }

    /**
     * Counts back from the most recent check-in rather than always from
     * today, so a streak that ended yesterday (today just not checked in
     * yet) still shows its real length instead of dropping to 0 the moment
     * the clock rolls over to a new day.
     */
    private fun currentStreak(sortedDesc: List<CheckInEntry>): Int {
        val mostRecent = sortedDesc.firstOrNull()?.date ?: return 0
        var streak = 0
        var expected = if (mostRecent.isBefore(LocalDate.now())) mostRecent else LocalDate.now()
        for (entry in sortedDesc) {
            if (entry.date == expected) {
                streak++
                expected = expected.minusDays(1)
            } else if (entry.date.isBefore(expected)) {
                break
            }
        }
        return streak
    }
}
