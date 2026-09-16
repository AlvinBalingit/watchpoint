package com.watchpoint.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.watchpoint.app.WatchPointApplication
import com.watchpoint.app.auth.AuthViewModel
import com.watchpoint.app.auth.AuthViewModelFactory
import com.watchpoint.app.checkin.CheckInViewModel
import com.watchpoint.app.checkin.CheckInViewModelFactory
import com.watchpoint.app.checkin.EXERCISES
import com.watchpoint.app.checkin.JournalViewModel
import com.watchpoint.app.checkin.JournalViewModelFactory
import com.watchpoint.app.checkin.screens.ActivityTagStepScreen
import com.watchpoint.app.checkin.screens.CHECKIN_STEPS
import com.watchpoint.app.checkin.screens.CHECKIN_STEPS_HIGH_DEMAND
import com.watchpoint.app.checkin.screens.CheckInDoneScreen
import com.watchpoint.app.checkin.screens.DashboardScreen
import com.watchpoint.app.checkin.screens.ExerciseDetailScreen
import com.watchpoint.app.checkin.screens.ExercisesScreen
import com.watchpoint.app.checkin.screens.GettingStartedScreen
import com.watchpoint.app.checkin.screens.JournalScreen
import com.watchpoint.app.checkin.screens.MilestoneCelebrationScreen
import com.watchpoint.app.checkin.screens.MoodStepScreen
import com.watchpoint.app.checkin.screens.ReadinessStepScreen
import com.watchpoint.app.checkin.screens.StreakGoalScreen
import com.watchpoint.app.checkin.screens.StressStepScreen
import com.watchpoint.app.checkin.screens.TrendsScreen
import com.watchpoint.app.checkin.screens.WeeklySummaryScreen
import com.watchpoint.app.checkin.suggestionsFor
import com.watchpoint.app.onboarding.OnboardingViewModel
import com.watchpoint.app.onboarding.OnboardingViewModelFactory
import com.watchpoint.app.onboarding.screens.AgeScreen
import com.watchpoint.app.onboarding.screens.AuthScreen
import com.watchpoint.app.onboarding.screens.BedTimeScreen
import com.watchpoint.app.onboarding.screens.FinalStepScreen
import com.watchpoint.app.onboarding.screens.GreetingScreen
import com.watchpoint.app.onboarding.screens.InterestsScreen
import com.watchpoint.app.onboarding.screens.LoadingScreen
import com.watchpoint.app.onboarding.screens.MoodScreen
import com.watchpoint.app.onboarding.screens.ProgramScreen
import com.watchpoint.app.onboarding.screens.QuoteScreen
import com.watchpoint.app.onboarding.screens.ReassuranceScreen
import com.watchpoint.app.onboarding.screens.ReasonForUsingScreen
import com.watchpoint.app.onboarding.screens.SourceScreen
import com.watchpoint.app.onboarding.screens.SummaryScreen
import com.watchpoint.app.onboarding.screens.SupportScreen
import com.watchpoint.app.onboarding.screens.WakeTimeScreen
import com.watchpoint.app.onboarding.screens.WelcomeScreen
import com.watchpoint.app.settings.SettingsViewModel
import com.watchpoint.app.settings.SettingsViewModelFactory
import com.watchpoint.app.settings.screens.PrivacyPolicyScreen
import com.watchpoint.app.settings.screens.SettingsScreen
import com.watchpoint.app.settings.screens.TermsOfServiceScreen
import com.watchpoint.app.ui.theme.Forest
import kotlinx.coroutines.launch

private const val SLIDE_MS = 320

/** After a check-in is submitted, route through the milestone celebration first if one was hit. */
private fun navigateAfterCheckIn(navController: NavController, milestone: Int?) {
    val destination = if (milestone != null) Route.MILESTONE_CELEBRATION else Route.CHECKIN_DONE
    navController.navigate(destination) {
        popUpTo(Route.HOME)
    }
}

@Composable
fun WatchPointNavHost() {
    val context = LocalContext.current
    val container = (context.applicationContext as WatchPointApplication).container

    val vm: OnboardingViewModel = viewModel(factory = OnboardingViewModelFactory(container.onboardingRepository))
    val checkInVm: CheckInViewModel = viewModel(
        factory = CheckInViewModelFactory(
            container.checkInRepository,
            container.exerciseRepository,
            container.programRepository,
            container.streakGoalRepository,
            container.weeklyReflectionRepository,
            container.settingsRepository
        )
    )
    val settingsVm: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(container.settingsRepository))
    val authVm: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(container.authRepository, container.syncManager, container.onboardingRepository)
    )
    val journalVm: JournalViewModel = viewModel(factory = JournalViewModelFactory(container.journalRepository))

    // The graph's start destination depends on whether the user is signed in
    // and whether onboarding was already completed - both one-time async
    // reads, so the graph is only created once both are known, and a
    // returning signed-in user never sees a flash of the welcome screen.
    val isSignedIn by container.authRepository.authStateFlow.collectAsStateWithLifecycle(initialValue = null)
    val isOnboarded by vm.isOnboardedForStartup.collectAsStateWithLifecycle(initialValue = null)
    val startDestination = if (isSignedIn == false) {
        Route.WELCOME
    } else {
        isOnboarded?.let { if (it) Route.HOME else Route.WELCOME }
    }

    if (startDestination == null) {
        Surface(modifier = Modifier.fillMaxSize(), color = Forest) {}
        return
    }

    val navController = rememberNavController()

    // Every screen answers a question and hands off to the next, so the whole
    // graph slides in one direction; back slides the other way.
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                tween(SLIDE_MS)
            ) + fadeIn(tween(SLIDE_MS))
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                tween(SLIDE_MS)
            ) + fadeOut(tween(SLIDE_MS))
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                tween(SLIDE_MS)
            ) + fadeIn(tween(SLIDE_MS))
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                tween(SLIDE_MS)
            ) + fadeOut(tween(SLIDE_MS))
        }
    ) {
        val back: () -> Unit = { navController.popBackStack() }
        fun go(route: String) = navController.navigate(route)

        composable(Route.WELCOME) {
            WelcomeScreen(
                onGetStarted = { go(Route.LOADING) },
                onSignIn = { go(Route.AUTH) }
            )
        }

        composable(Route.LOADING) {
            LoadingScreen(
                onBack = back,
                onFinished = {
                    navController.navigate(Route.GREETING) {
                        // the spinner is a hand-off, not somewhere to come back to
                        popUpTo(Route.LOADING) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.GREETING) {
            GreetingScreen(onBack = back, onNext = { go(Route.AUTH) })
        }

        composable(Route.AUTH) {
            AuthScreen(
                mode = authVm.mode,
                email = authVm.email,
                password = authVm.password,
                confirmPassword = authVm.confirmPassword,
                firstName = authVm.firstName,
                middleInitial = authVm.middleInitial,
                lastName = authVm.lastName,
                birthday = authVm.birthday,
                isLoading = authVm.isLoading,
                errorMessage = authVm.errorMessage,
                privacyConsent = authVm.privacyConsent,
                onEmailChange = authVm::updateEmail,
                onPasswordChange = authVm::updatePassword,
                onConfirmPasswordChange = authVm::updateConfirmPassword,
                onFirstNameChange = authVm::updateFirstName,
                onMiddleInitialChange = authVm::updateMiddleInitial,
                onLastNameChange = authVm::updateLastName,
                onBirthdayChange = authVm::updateBirthday,
                onPrivacyConsentChange = authVm::updatePrivacyConsent,
                onToggleMode = authVm::toggleMode,
                onSubmit = {
                    authVm.submit(onSuccess = { alreadyOnboarded ->
                        if (alreadyOnboarded) {
                            navController.navigate(Route.HOME) {
                                popUpTo(0) { inclusive = true }
                            }
                        } else {
                            go(Route.QUOTE)
                        }
                    })
                },
                onOpenTerms = { go(Route.TERMS) },
                onOpenPrivacy = { go(Route.PRIVACY) },
                onBack = back
            )
        }

        composable(Route.QUOTE) {
            QuoteScreen(onBack = back, onNext = { go(Route.SOURCE) })
        }

        composable(Route.SOURCE) {
            SourceScreen(
                selected = vm.source,
                onSelect = vm::selectSource,
                onBack = back,
                onNext = { go(Route.REASON_FOR_USING) }
            )
        }

        composable(Route.REASON_FOR_USING) {
            ReasonForUsingScreen(
                selected = vm.reasonsForUsing,
                onToggle = vm::toggleReasonForUsing,
                onBack = back,
                onNext = { go(Route.MOOD) }
            )
        }

        composable(Route.MOOD) {
            MoodScreen(
                selected = vm.mood,
                onSelect = {
                    vm.selectMood(it)
                    go(Route.REASSURANCE)
                },
                onBack = back
            )
        }

        composable(Route.REASSURANCE) {
            ReassuranceScreen(onBack = back, onNext = { go(Route.FINAL_STEP) })
        }

        composable(Route.FINAL_STEP) {
            FinalStepScreen(onBack = back, onNext = { go(Route.WAKE_TIME) })
        }

        composable(Route.WAKE_TIME) {
            WakeTimeScreen(
                time = vm.wakeTime,
                onTimeChange = vm::selectWakeTime,
                onBack = back,
                onNext = { go(Route.BED_TIME) }
            )
        }

        composable(Route.BED_TIME) {
            BedTimeScreen(
                time = vm.bedTime,
                onTimeChange = vm::selectBedTime,
                onBack = back,
                onNext = { go(Route.INTERESTS) }
            )
        }

        composable(Route.INTERESTS) {
            InterestsScreen(
                selected = vm.interests,
                onToggle = vm::toggleInterest,
                onBack = back,
                onNext = { go(Route.SUPPORT) }
            )
        }

        composable(Route.SUPPORT) {
            SupportScreen(
                selected = vm.support,
                onSelect = {
                    vm.selectSupport(it)
                    go(Route.PROGRAM)
                },
                onBack = back
            )
        }

        composable(Route.PROGRAM) {
            ProgramScreen(
                selected = vm.program,
                onSelect = {
                    vm.selectProgram(it)
                    go(Route.AGE)
                },
                onBack = back
            )
        }

        composable(Route.AGE) {
            AgeScreen(
                selected = vm.ageGroup,
                onSelect = {
                    vm.selectAgeGroup(it)
                    go(Route.SUMMARY)
                },
                onBack = back
            )
        }

        composable(Route.SUMMARY) {
            SummaryScreen(
                state = vm,
                onBack = back,
                onEnter = {
                    vm.completeOnboarding()
                    navController.navigate(Route.HOME) {
                        popUpTo(Route.WELCOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.HOME) {
            val dashboardState by checkInVm.dashboardState.collectAsStateWithLifecycle()
            val streakGoal by checkInVm.streakGoal.collectAsStateWithLifecycle()
            val highDemandMode by checkInVm.highDemandMode.collectAsStateWithLifecycle()
            DashboardScreen(
                latestEntry = dashboardState.latestEntry,
                todayCheckedIn = dashboardState.todayCheckedIn,
                weeklyConsistency = dashboardState.weeklyConsistency,
                checkedInDatesThisWeek = dashboardState.checkedInDatesThisWeek,
                currentStreak = dashboardState.currentStreak,
                gardenProgress = dashboardState.gardenProgress,
                hasStreakGoal = streakGoal != null,
                mentalLoadWarning = dashboardState.mentalLoadWarning,
                highDemandMode = highDemandMode,
                onHighDemandModeChange = checkInVm::selectHighDemandMode,
                onStartCheckIn = {
                    if (highDemandMode) go(Route.CHECKIN_STRESS) else go(Route.CHECKIN_MOOD)
                },
                onViewTrends = { go(Route.TRENDS) },
                onViewWeeklySummary = { go(Route.WEEKLY_SUMMARY) },
                onOpenExercises = { go(Route.EXERCISES) },
                onOpenGettingStarted = { go(Route.GETTING_STARTED) },
                onOpenStreakGoal = { go(Route.STREAK_GOAL) },
                onOpenJournal = { go(Route.JOURNAL) },
                onOpenSettings = { go(Route.SETTINGS) }
            )
        }

        composable(Route.CHECKIN_MOOD) {
            MoodStepScreen(
                selected = checkInVm.draftMood,
                onSelect = {
                    checkInVm.selectMood(it)
                    go(Route.CHECKIN_STRESS)
                },
                onBack = back
            )
        }

        composable(Route.CHECKIN_STRESS) {
            val highDemand by checkInVm.highDemandMode.collectAsStateWithLifecycle()
            StressStepScreen(
                selected = checkInVm.draftStress,
                onSelect = {
                    checkInVm.selectStress(it)
                    go(Route.CHECKIN_READINESS)
                },
                onBack = back,
                step = if (highDemand) 1 else 2,
                totalSteps = if (highDemand) CHECKIN_STEPS_HIGH_DEMAND else CHECKIN_STEPS
            )
        }

        composable(Route.CHECKIN_READINESS) {
            val highDemand by checkInVm.highDemandMode.collectAsStateWithLifecycle()
            val scope = rememberCoroutineScope()
            ReadinessStepScreen(
                selected = checkInVm.draftReadiness,
                onSelect = {
                    checkInVm.selectReadiness(it)
                    if (highDemand) {
                        scope.launch {
                            checkInVm.submitCheckIn()
                            navigateAfterCheckIn(navController, checkInVm.milestoneReached)
                        }
                    } else {
                        go(Route.CHECKIN_TAGS)
                    }
                },
                onBack = back,
                step = if (highDemand) 2 else 3,
                totalSteps = if (highDemand) CHECKIN_STEPS_HIGH_DEMAND else CHECKIN_STEPS
            )
        }

        composable(Route.CHECKIN_TAGS) {
            val scope = rememberCoroutineScope()
            ActivityTagStepScreen(
                selectedActivities = checkInVm.draftActivityTags,
                selectedInteractions = checkInVm.draftInteractionTags,
                reflection = checkInVm.draftReflection,
                onToggleActivity = checkInVm::toggleActivityTag,
                onToggleInteraction = checkInVm::toggleInteractionTag,
                onReflectionChange = checkInVm::updateReflection,
                onBack = back,
                onDone = {
                    scope.launch {
                        checkInVm.submitCheckIn()
                        navigateAfterCheckIn(navController, checkInVm.milestoneReached)
                    }
                },
                onSkip = {
                    scope.launch {
                        checkInVm.submitCheckIn()
                        navigateAfterCheckIn(navController, checkInVm.milestoneReached)
                    }
                }
            )
        }

        composable(Route.CHECKIN_DONE) {
            val history by checkInVm.history.collectAsStateWithLifecycle()
            val latest = history.maxByOrNull { it.date }
            CheckInDoneScreen(
                messages = checkInVm.feedbackMessages(),
                suggestion = latest?.let { suggestionsFor(it, vm.interests).firstOrNull() },
                onBackToDashboard = {
                    navController.navigate(Route.HOME) {
                        popUpTo(Route.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.MILESTONE_CELEBRATION) {
            val dashboardState by checkInVm.dashboardState.collectAsStateWithLifecycle()
            MilestoneCelebrationScreen(
                milestoneDays = checkInVm.milestoneReached ?: 0,
                checkedInDatesThisWeek = dashboardState.checkedInDatesThisWeek,
                onContinue = {
                    navController.navigate(Route.CHECKIN_DONE) {
                        popUpTo(Route.HOME)
                    }
                }
            )
        }

        composable(Route.TRENDS) {
            val history by checkInVm.history.collectAsStateWithLifecycle()
            TrendsScreen(history = history, onBack = back)
        }

        composable(Route.WEEKLY_SUMMARY) {
            val history by checkInVm.history.collectAsStateWithLifecycle()
            val weeklyReflection by checkInVm.weeklyReflection.collectAsStateWithLifecycle()
            WeeklySummaryScreen(
                history = history,
                weeklyReflection = weeklyReflection,
                onWeeklyReflectionChange = checkInVm::saveWeeklyReflection,
                onBack = back
            )
        }

        composable(Route.SETTINGS) {
            val settings by settingsVm.settings.collectAsStateWithLifecycle()
            val scope = rememberCoroutineScope()
            SettingsScreen(
                reminderEnabled = settings.enabled,
                motivationalQuotesEnabled = settings.motivationalQuotesEnabled,
                currentEmail = authVm.currentEmail,
                profileFirstName = authVm.firstName,
                profileMiddleInitial = authVm.middleInitial,
                profileLastName = authVm.lastName,
                profileBirthday = authVm.birthday,
                reminderTime = settings.time,
                onReminderEnabledChange = settingsVm::selectReminderEnabled,
                onMotivationalQuotesEnabledChange = settingsVm::selectMotivationalQuotesEnabled,
                onReminderTimeChange = settingsVm::selectReminderTime,
                onOpenTerms = { go(Route.TERMS) },
                onOpenPrivacy = { go(Route.PRIVACY) },
                onUpdateProfile = authVm::updateProfile,
                onLogout = {
                    container.authRepository.signOut()
                    scope.launch {
                        container.clearLocalUserData()
                    }
                    navController.navigate(Route.WELCOME) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onDeleteAccount = { onComplete ->
                    scope.launch {
                        try {
                            container.authRepository.deleteAccount()
                            container.clearLocalUserData()
                            onComplete(null)
                            navController.navigate(Route.WELCOME) {
                                popUpTo(0) { inclusive = true }
                            }
                        } catch (e: Exception) {
                            onComplete(e.message ?: "Unable to delete your account. Please try again.")
                        }
                    }
                },
                onBack = back
            )
        }

        composable(Route.TERMS) {
            TermsOfServiceScreen(onBack = back)
        }

        composable(Route.PRIVACY) {
            PrivacyPolicyScreen(onBack = back)
        }

        composable(Route.EXERCISES) {
            val completedExerciseIds by checkInVm.completedExerciseIds.collectAsStateWithLifecycle()
            ExercisesScreen(
                completedExerciseIds = completedExerciseIds,
                onBack = back,
                onOpenExercise = { id -> go(Route.exerciseDetail(id)) }
            )
        }

        composable("${Route.EXERCISE_DETAIL}/{id}") { backStackEntry ->
            val completedExerciseIds by checkInVm.completedExerciseIds.collectAsStateWithLifecycle()
            val exerciseNotes by checkInVm.exerciseNotes.collectAsStateWithLifecycle()
            val id = backStackEntry.arguments?.getString("id")
            val exercise = EXERCISES.firstOrNull { it.id == id }
            if (exercise != null) {
                ExerciseDetailScreen(
                    exercise = exercise,
                    done = exercise.id in completedExerciseIds,
                    savedNotes = exerciseNotes[exercise.id].orEmpty(),
                    onBack = back,
                    onMarkDone = { title: String, note: String -> checkInVm.completeExercise(exercise.id, title, note) },
                    onUpdateNote = checkInVm::updateExerciseNote,
                    onDeleteNote = checkInVm::deleteExerciseNote
                )
            }
        }

        composable(Route.GETTING_STARTED) {
            val programDayCompleted by checkInVm.programDayCompleted.collectAsStateWithLifecycle()
            GettingStartedScreen(
                dayCompleted = programDayCompleted,
                onBack = back,
                onCompleteDay = checkInVm::completeProgramDay,
                onOpenExercise = { id -> go(Route.exerciseDetail(id)) }
            )
        }

        composable(Route.STREAK_GOAL) {
            val streakGoal by checkInVm.streakGoal.collectAsStateWithLifecycle()
            StreakGoalScreen(
                currentTargetDays = streakGoal?.targetDays,
                onBack = back,
                onCommit = { days ->
                    checkInVm.commitStreakGoal(days)
                    back()
                }
            )
        }

        composable(Route.JOURNAL) {
            val entries by journalVm.entries.collectAsStateWithLifecycle()
            JournalScreen(
                entries = entries,
                draftTitle = journalVm.draftTitle,
                draftText = journalVm.draftText,
                onDraftTitleChange = journalVm::updateDraftTitle,
                onDraftChange = journalVm::updateDraftText,
                onSave = journalVm::submitEntry,
                editingEntryId = journalVm.editingEntryId,
                onEdit = journalVm::beginEditing,
                onCancelEdit = journalVm::cancelEditing,
                onDelete = journalVm::deleteEntry,
                onBack = back
            )
        }
    }
}
