package com.watchpoint.app.checkin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.watchpoint.app.data.repository.CheckInRepository
import com.watchpoint.app.data.repository.ExerciseRepository
import com.watchpoint.app.data.repository.ProgramRepository
import com.watchpoint.app.data.repository.SettingsRepository
import com.watchpoint.app.data.repository.StreakGoalRepository
import com.watchpoint.app.data.repository.WeeklyReflectionRepository

class CheckInViewModelFactory(
    private val checkInRepository: CheckInRepository,
    private val exerciseRepository: ExerciseRepository,
    private val programRepository: ProgramRepository,
    private val streakGoalRepository: StreakGoalRepository,
    private val weeklyReflectionRepository: WeeklyReflectionRepository,
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        CheckInViewModel(
            checkInRepository,
            exerciseRepository,
            programRepository,
            streakGoalRepository,
            weeklyReflectionRepository,
            settingsRepository
        ) as T
}
