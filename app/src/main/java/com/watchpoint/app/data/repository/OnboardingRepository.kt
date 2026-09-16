package com.watchpoint.app.data.repository

import com.watchpoint.app.data.db.OnboardingAnswersDao
import com.watchpoint.app.data.db.OnboardingAnswersEntity
import com.watchpoint.app.onboarding.AgeGroup
import com.watchpoint.app.onboarding.Interest
import com.watchpoint.app.onboarding.JoinSource
import com.watchpoint.app.onboarding.MoodState
import com.watchpoint.app.onboarding.OnboardingAnswers
import com.watchpoint.app.onboarding.Program
import com.watchpoint.app.onboarding.ReasonForUsing
import com.watchpoint.app.onboarding.SupportLevel
import com.watchpoint.app.onboarding.TimeOfDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/** Persists onboarding answers and whether onboarding has been completed. */
class OnboardingRepository(private val dao: OnboardingAnswersDao) {

    fun observeAnswers(): Flow<OnboardingAnswers?> = dao.observe().map { it?.toDomain() }

    fun observeIsOnboarded(): Flow<Boolean> = dao.observe().map { it?.completedAt != null }

    /** One-shot read for routing right after sign-in, once the downloaded backup (if any) has landed. */
    suspend fun isOnboarded(): Boolean = observeIsOnboarded().first()

    suspend fun save(answers: OnboardingAnswers) {
        dao.upsert(answers.toEntity(completedAt = System.currentTimeMillis()))
    }

    private fun OnboardingAnswers.toEntity(completedAt: Long) = OnboardingAnswersEntity(
        source = source?.name,
        mood = mood?.name,
        wakeHour = wakeTime.hour,
        wakeMinute = wakeTime.minute,
        bedHour = bedTime.hour,
        bedMinute = bedTime.minute,
        interestsCsv = interests.joinToString(",") { it.name },
        support = support?.name,
        ageGroup = ageGroup?.name,
        program = program?.name,
        reasonsForUsingCsv = reasonsForUsing.joinToString(",") { it.name },
        completedAt = completedAt,
        synced = false,
        updatedAt = System.currentTimeMillis()
    )

    private fun OnboardingAnswersEntity.toDomain() = OnboardingAnswers(
        source = source?.let { JoinSource.valueOf(it) },
        mood = mood?.let { MoodState.valueOf(it) },
        wakeTime = TimeOfDay(wakeHour, wakeMinute),
        bedTime = TimeOfDay(bedHour, bedMinute),
        interests = if (interestsCsv.isBlank()) {
            emptySet()
        } else {
            interestsCsv.split(",").filter { it.isNotBlank() }.map { Interest.valueOf(it) }.toSet()
        },
        support = support?.let { SupportLevel.valueOf(it) },
        ageGroup = ageGroup?.let { AgeGroup.valueOf(it) },
        program = program?.let { name -> Program.entries.firstOrNull { it.name == name } },
        reasonsForUsing = if (reasonsForUsingCsv.isBlank()) {
            emptySet()
        } else {
            reasonsForUsingCsv.split(",").filter { it.isNotBlank() }
                .mapNotNull { name -> ReasonForUsing.entries.firstOrNull { it.name == name } }
                .toSet()
        }
    )
}
