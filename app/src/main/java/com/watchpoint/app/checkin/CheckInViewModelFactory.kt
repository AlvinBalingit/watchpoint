package com.watchpoint.app.checkin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.watchpoint.app.data.repository.CheckInRepository
import com.watchpoint.app.data.repository.ExerciseRepository
import com.watchpoint.app.data.repository.ProgramRepository
import com.watchpoint.app.data.repository.StreakGoalRepository

class CheckInViewModelFactory(
    private val checkInRepository: CheckInRepository,
    private val exerciseRepository: ExerciseRepository,
    private val programRepository: ProgramRepository,
    private val streakGoalRepository: StreakGoalRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        CheckInViewModel(checkInRepository, exerciseRepository, programRepository, streakGoalRepository) as T
}
