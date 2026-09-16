package com.watchpoint.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CheckInEntity::class,
        OnboardingAnswersEntity::class,
        ExerciseCompletionEntity::class,
        ProgramProgressEntity::class,
        StreakGoalEntity::class,
        JournalEntryEntity::class
    ],
    version = 9,
    exportSchema = false
)
abstract class WatchPointDatabase : RoomDatabase() {
    abstract fun checkInDao(): CheckInDao
    abstract fun onboardingAnswersDao(): OnboardingAnswersDao
    abstract fun exerciseCompletionDao(): ExerciseCompletionDao
    abstract fun programProgressDao(): ProgramProgressDao
    abstract fun streakGoalDao(): StreakGoalDao
    abstract fun journalEntryDao(): JournalEntryDao

    companion object {
        @Volatile
        private var instance: WatchPointDatabase? = null

        fun getInstance(context: Context): WatchPointDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    WatchPointDatabase::class.java,
                    "watchpoint.db"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9).build().also { instance = it }
            }
    }
}
