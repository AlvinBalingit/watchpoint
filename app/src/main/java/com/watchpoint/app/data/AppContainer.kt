package com.watchpoint.app.data

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.watchpoint.app.data.db.WatchPointDatabase
import com.watchpoint.app.data.prefs.SettingsPreferences
import com.watchpoint.app.data.remote.FirebaseAuthManager
import com.watchpoint.app.data.remote.FirestoreSyncManager
import com.watchpoint.app.data.remote.NetworkAwareSyncTrigger
import com.watchpoint.app.data.repository.AuthRepository
import com.watchpoint.app.data.repository.CheckInRepository
import com.watchpoint.app.data.repository.ExerciseRepository
import com.watchpoint.app.data.repository.JournalRepository
import com.watchpoint.app.data.repository.OnboardingRepository
import com.watchpoint.app.data.repository.ProgramRepository
import com.watchpoint.app.data.repository.SettingsRepository
import com.watchpoint.app.data.repository.StreakGoalRepository
import com.watchpoint.app.data.repository.WeeklyReflectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Manual service locator for the app's repositories - no DI framework is used
 * anywhere in this project, so this stays a small, explicit wiring point
 * rather than introducing Hilt/Koin for three ViewModels.
 */
class AppContainer(context: Context) {
    private val database = WatchPointDatabase.getInstance(context)
    private val settingsPreferences = SettingsPreferences(context)

    val checkInRepository = CheckInRepository(database.checkInDao())
    val onboardingRepository = OnboardingRepository(database.onboardingAnswersDao())
    val exerciseRepository = ExerciseRepository(database.exerciseCompletionDao())
    val programRepository = ProgramRepository(database.programProgressDao())
    val settingsRepository = SettingsRepository(settingsPreferences)
    val streakGoalRepository = StreakGoalRepository(database.streakGoalDao())
    val journalRepository = JournalRepository(database.journalEntryDao())
    val weeklyReflectionRepository = WeeklyReflectionRepository(database.weeklyReflectionDao())

    private val authManager = FirebaseAuthManager(
        FirebaseAuth.getInstance(),
        FirebaseFirestore.getInstance()
    )
    val authRepository = AuthRepository(authManager)

    val syncManager = FirestoreSyncManager(
        firestore = FirebaseFirestore.getInstance(),
        authManager = authManager,
        checkInDao = database.checkInDao(),
        exerciseCompletionDao = database.exerciseCompletionDao(),
        programProgressDao = database.programProgressDao(),
        onboardingAnswersDao = database.onboardingAnswersDao(),
        streakGoalDao = database.streakGoalDao(),
        journalEntryDao = database.journalEntryDao()
    )
    val syncTrigger = NetworkAwareSyncTrigger(context, syncManager, authManager)

    /**
     * Wipes this device's local copy of the signed-out user's account data
     * (check-ins, journal, onboarding answers, etc.) so the next person to
     * sign in on the same phone can't see it. Reminder preferences are
     * device-level, not account data, so they're left alone. Firestore's
     * copy is untouched - it belongs to the account, not the device.
     */
    suspend fun clearLocalUserData() {
        withContext(Dispatchers.IO) {
            database.clearAllTables()
        }
    }
}
