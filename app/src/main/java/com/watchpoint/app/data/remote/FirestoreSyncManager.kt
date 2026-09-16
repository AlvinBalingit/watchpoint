package com.watchpoint.app.data.remote

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.watchpoint.app.data.db.CheckInDao
import com.watchpoint.app.data.db.CheckInEntity
import com.watchpoint.app.data.db.ExerciseCompletionDao
import com.watchpoint.app.data.db.ExerciseCompletionEntity
import com.watchpoint.app.data.db.JournalEntryDao
import com.watchpoint.app.data.db.JournalEntryEntity
import com.watchpoint.app.data.db.OnboardingAnswersDao
import com.watchpoint.app.data.db.OnboardingAnswersEntity
import com.watchpoint.app.data.db.ProgramProgressDao
import com.watchpoint.app.data.db.ProgramProgressEntity
import com.watchpoint.app.data.db.StreakGoalDao
import com.watchpoint.app.data.db.StreakGoalEntity
import kotlinx.coroutines.tasks.await

private const val TAG = "FirestoreSyncManager"

/**
 * Pushes anything Room has marked unsynced up to Firestore, one collection
 * at a time. Room stays the source of truth the UI reads from - this is a
 * one-way, best-effort backup, not a live sync: a row that fails to push
 * just stays unsynced and is retried on the next call. Only runs when the
 * caller (NetworkAwareSyncTrigger) has already established the device is
 * online, so a failure here means a real problem (auth, permissions), not
 * "no network" - that case is handled by simply not calling this at all.
 */
class FirestoreSyncManager(
    private val firestore: FirebaseFirestore,
    private val authManager: FirebaseAuthManager,
    private val checkInDao: CheckInDao,
    private val exerciseCompletionDao: ExerciseCompletionDao,
    private val programProgressDao: ProgramProgressDao,
    private val onboardingAnswersDao: OnboardingAnswersDao,
    private val streakGoalDao: StreakGoalDao,
    private val journalEntryDao: JournalEntryDao
) {

    suspend fun syncAll() {
        val uid = authManager.userId ?: return

        syncCheckIns(uid)
        syncExerciseCompletions(uid)
        syncProgramProgress(uid)
        syncOnboardingAnswers(uid)
        syncStreakGoal(uid)
        syncJournalEntries(uid)
    }

    /**
     * Pulls this account's Firestore backup down into Room. Call once right
     * after a successful sign-in - a fresh install or a new device otherwise
     * has an empty local database even though a backup exists online, and
     * onboarding gets repeated because onboarding_answers.completedAt is
     * never populated locally. Downloaded rows are marked synced = true,
     * since Firestore is where they already live.
     */
    suspend fun downloadAll(uid: String) {
        runCatching {
            val checkIns = firestore.collection("users").document(uid)
                .collection("checkIns").get().await()
            checkInDao.upsertAll(checkIns.documents.mapNotNull { it.toCheckInEntity() })
        }.onFailure { Log.w(TAG, "Failed to download check-ins", it) }

        runCatching {
            val completions = firestore.collection("users").document(uid)
                .collection("exerciseCompletions").get().await()
            for (doc in completions.documents) {
                doc.toExerciseCompletionEntity()?.let { exerciseCompletionDao.upsert(it) }
            }
        }.onFailure { Log.w(TAG, "Failed to download exercise completions", it) }

        runCatching {
            val doc = firestore.userDoc(uid, "programProgress", "singleton").get().await()
            doc.toProgramProgressEntity()?.let { programProgressDao.upsert(it) }
        }.onFailure { Log.w(TAG, "Failed to download program progress", it) }

        runCatching {
            val doc = firestore.userDoc(uid, "onboardingAnswers", "singleton").get().await()
            doc.toOnboardingAnswersEntity()?.let { onboardingAnswersDao.upsert(it) }
        }.onFailure { Log.w(TAG, "Failed to download onboarding answers", it) }

        runCatching {
            val doc = firestore.userDoc(uid, "streakGoal", "singleton").get().await()
            doc.toStreakGoalEntity()?.let { streakGoalDao.upsert(it) }
        }.onFailure { Log.w(TAG, "Failed to download streak goal", it) }

        runCatching {
            val entries = firestore.collection("users").document(uid)
                .collection("journalEntries").get().await()
            for (doc in entries.documents) {
                doc.toJournalEntryEntity()?.let { journalEntryDao.upsert(it) }
            }
        }.onFailure { Log.w(TAG, "Failed to download journal entries", it) }
    }

    private suspend fun syncCheckIns(uid: String) {
        val unsynced = checkInDao.getUnsynced()
        for (entity in unsynced) {
            runCatching {
                firestore.userDoc(uid, "checkIns", entity.date)
                    .set(entity.toFirestoreMap(), SetOptions.merge())
                    .await()
                checkInDao.markSynced(entity.date)
            }.onFailure { Log.w(TAG, "Failed to sync check-in ${entity.date}", it) }
        }
    }

    private suspend fun syncExerciseCompletions(uid: String) {
        val unsynced = exerciseCompletionDao.getUnsynced()
        for (entity in unsynced) {
            runCatching {
                firestore.userDoc(uid, "exerciseCompletions", entity.id.toString())
                    .set(entity.toFirestoreMap(), SetOptions.merge())
                    .await()
                exerciseCompletionDao.markSynced(entity.id)
            }.onFailure { Log.w(TAG, "Failed to sync exercise completion ${entity.id}", it) }
        }
    }

    private suspend fun syncProgramProgress(uid: String) {
        val unsynced = programProgressDao.getUnsynced()
        for (entity in unsynced) {
            runCatching {
                firestore.userDoc(uid, "programProgress", "singleton")
                    .set(entity.toFirestoreMap(), SetOptions.merge())
                    .await()
                programProgressDao.markSynced(entity.id)
            }.onFailure { Log.w(TAG, "Failed to sync program progress", it) }
        }
    }

    private suspend fun syncOnboardingAnswers(uid: String) {
        val unsynced = onboardingAnswersDao.getUnsynced()
        for (entity in unsynced) {
            runCatching {
                firestore.userDoc(uid, "onboardingAnswers", "singleton")
                    .set(entity.toFirestoreMap(), SetOptions.merge())
                    .await()
                onboardingAnswersDao.markSynced(entity.id)
            }.onFailure { Log.w(TAG, "Failed to sync onboarding answers", it) }
        }
    }

    private suspend fun syncStreakGoal(uid: String) {
        val unsynced = streakGoalDao.getUnsynced()
        for (entity in unsynced) {
            runCatching {
                firestore.userDoc(uid, "streakGoal", "singleton")
                    .set(entity.toFirestoreMap(), SetOptions.merge())
                    .await()
                streakGoalDao.markSynced(entity.id)
            }.onFailure { Log.w(TAG, "Failed to sync streak goal", it) }
        }
    }

    private suspend fun syncJournalEntries(uid: String) {
        val pendingDeletes = journalEntryDao.getPendingDeletes()
        for (entity in pendingDeletes) {
            runCatching {
                firestore.userDoc(uid, "journalEntries", entity.id.toString())
                    .delete()
                    .await()
                journalEntryDao.hardDelete(entity.id)
            }.onFailure { Log.w(TAG, "Failed to delete journal entry ${entity.id}", it) }
        }

        val unsynced = journalEntryDao.getUnsynced()
        for (entity in unsynced) {
            runCatching {
                firestore.userDoc(uid, "journalEntries", entity.id.toString())
                    .set(entity.toFirestoreMap(), SetOptions.merge())
                    .await()
                journalEntryDao.markSynced(entity.id)
            }.onFailure { Log.w(TAG, "Failed to sync journal entry ${entity.id}", it) }
        }
    }

    private fun FirebaseFirestore.userDoc(uid: String, collection: String, docId: String) =
        collection("users").document(uid).collection(collection).document(docId)

    private fun CheckInEntity.toFirestoreMap() = mapOf(
        "date" to date,
        "mood" to mood,
        "stress" to stress,
        "readiness" to readiness,
        "activityTags" to activityTagsCsv,
        "interactionTags" to interactionTagsCsv,
        "reflection" to reflection,
        "updatedAt" to updatedAt
    )

    private fun ExerciseCompletionEntity.toFirestoreMap() = mapOf(
        "exerciseId" to exerciseId,
        "completedAt" to completedAt,
        "noteTitle" to noteTitle,
        "noteText" to noteText,
        "updatedAt" to updatedAt
    )

    private fun ProgramProgressEntity.toFirestoreMap() = mapOf(
        "dayCompleted" to dayCompleted,
        "updatedAt" to updatedAt
    )

    private fun OnboardingAnswersEntity.toFirestoreMap() = mapOf(
        "source" to source,
        "mood" to mood,
        "wakeHour" to wakeHour,
        "wakeMinute" to wakeMinute,
        "bedHour" to bedHour,
        "bedMinute" to bedMinute,
        "interests" to interestsCsv,
        "support" to support,
        "ageGroup" to ageGroup,
        "program" to program,
        "reasonsForUsing" to reasonsForUsingCsv,
        "completedAt" to completedAt,
        "updatedAt" to updatedAt
    )

    private fun JournalEntryEntity.toFirestoreMap() = mapOf(
        "title" to title,
        "text" to text,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )

    private fun StreakGoalEntity.toFirestoreMap() = mapOf(
        "targetDays" to targetDays,
        "committedAt" to committedAt,
        "updatedAt" to updatedAt
    )

    private fun DocumentSnapshot.toCheckInEntity(): CheckInEntity? {
        if (!exists()) return null
        return CheckInEntity(
            date = getString("date") ?: id,
            mood = getString("mood") ?: return null,
            stress = getString("stress") ?: return null,
            readiness = getString("readiness") ?: return null,
            activityTagsCsv = getString("activityTags") ?: "",
            interactionTagsCsv = getString("interactionTags") ?: "",
            reflection = getString("reflection"),
            synced = true,
            updatedAt = getLong("updatedAt") ?: 0L
        )
    }

    private fun DocumentSnapshot.toExerciseCompletionEntity(): ExerciseCompletionEntity? {
        if (!exists()) return null
        return ExerciseCompletionEntity(
            id = id.toLongOrNull() ?: return null,
            exerciseId = getString("exerciseId") ?: return null,
            completedAt = getLong("completedAt") ?: 0L,
            noteTitle = getString("noteTitle") ?: "",
            noteText = getString("noteText") ?: "",
            synced = true,
            updatedAt = getLong("updatedAt") ?: 0L
        )
    }

    private fun DocumentSnapshot.toProgramProgressEntity(): ProgramProgressEntity? {
        if (!exists()) return null
        return ProgramProgressEntity(
            dayCompleted = (getLong("dayCompleted") ?: 0L).toInt(),
            synced = true,
            updatedAt = getLong("updatedAt") ?: 0L
        )
    }

    private fun DocumentSnapshot.toOnboardingAnswersEntity(): OnboardingAnswersEntity? {
        if (!exists()) return null
        return OnboardingAnswersEntity(
            source = getString("source"),
            mood = getString("mood"),
            wakeHour = (getLong("wakeHour") ?: 0L).toInt(),
            wakeMinute = (getLong("wakeMinute") ?: 0L).toInt(),
            bedHour = (getLong("bedHour") ?: 0L).toInt(),
            bedMinute = (getLong("bedMinute") ?: 0L).toInt(),
            interestsCsv = getString("interests") ?: "",
            support = getString("support"),
            ageGroup = getString("ageGroup"),
            program = getString("program"),
            reasonsForUsingCsv = getString("reasonsForUsing") ?: "",
            completedAt = getLong("completedAt"),
            synced = true,
            updatedAt = getLong("updatedAt") ?: 0L
        )
    }

    private fun DocumentSnapshot.toStreakGoalEntity(): StreakGoalEntity? {
        if (!exists()) return null
        return StreakGoalEntity(
            targetDays = (getLong("targetDays") ?: 0L).toInt(),
            committedAt = getLong("committedAt"),
            synced = true,
            updatedAt = getLong("updatedAt") ?: 0L
        )
    }

    private fun DocumentSnapshot.toJournalEntryEntity(): JournalEntryEntity? {
        if (!exists()) return null
        return JournalEntryEntity(
            id = id.toLongOrNull() ?: return null,
            title = getString("title") ?: "",
            text = getString("text") ?: return null,
            createdAt = getLong("createdAt") ?: 0L,
            synced = true,
            updatedAt = getLong("updatedAt") ?: 0L,
            pendingDelete = false
        )
    }
}
