package com.watchpoint.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Single-row table (fixed [id]) holding everything collected during onboarding. */
@Entity(tableName = "onboarding_answers")
data class OnboardingAnswersEntity(
    @PrimaryKey val id: Int = SINGLETON_ID,
    val source: String?,
    val mood: String?,
    val wakeHour: Int,
    val wakeMinute: Int,
    val bedHour: Int,
    val bedMinute: Int,
    val interestsCsv: String,
    val support: String?,
    val ageGroup: String?,
    val program: String?,
    val reasonsForUsingCsv: String = "",
    /** Non-null once onboarding has been completed - the "already onboarded" signal. */
    val completedAt: Long?,
    val synced: Boolean = false,
    val updatedAt: Long = 0L
) {
    companion object {
        const val SINGLETON_ID = 1
    }
}
