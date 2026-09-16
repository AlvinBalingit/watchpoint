package com.watchpoint.app.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Adds sync-tracking columns to every table so the Firestore sync manager
 * knows what still needs pushing. Existing local rows default to
 * synced = 0 (unsynced) so nothing already on a device is silently skipped
 * once sync starts running.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE check_ins ADD COLUMN synced INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE check_ins ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")

        db.execSQL("ALTER TABLE exercise_completions ADD COLUMN synced INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE exercise_completions ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")

        db.execSQL("ALTER TABLE program_progress ADD COLUMN synced INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE program_progress ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")

        db.execSQL("ALTER TABLE onboarding_answers ADD COLUMN synced INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE onboarding_answers ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
    }
}

/** Adds the streak_goal table - purely additive, no existing table is touched. */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS streak_goal (
                id INTEGER NOT NULL PRIMARY KEY,
                targetDays INTEGER NOT NULL,
                committedAt INTEGER,
                synced INTEGER NOT NULL DEFAULT 0,
                updatedAt INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent()
        )
    }
}

/** Adds the journal_entries table - purely additive, no existing table is touched. */
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS journal_entries (
                id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                text TEXT NOT NULL,
                createdAt INTEGER NOT NULL,
                synced INTEGER NOT NULL DEFAULT 0,
                updatedAt INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent()
        )
    }
}

/** Adds the new onboarding questions (program, reasons for using) - additive columns only. */
val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE onboarding_answers ADD COLUMN program TEXT")
        db.execSQL("ALTER TABLE onboarding_answers ADD COLUMN reasonsForUsingCsv TEXT NOT NULL DEFAULT ''")
    }
}

/** Adds note text metadata for exercise journaling entries so saved notes can be displayed with timestamps. */
val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE exercise_completions ADD COLUMN noteText TEXT NOT NULL DEFAULT ''")
    }
}

/** Adds a user-provided title to standalone journal entries. */
val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE journal_entries ADD COLUMN title TEXT NOT NULL DEFAULT ''")
    }
}

/** Adds a user-provided title to exercise notes. */
val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE exercise_completions ADD COLUMN noteTitle TEXT NOT NULL DEFAULT ''")
    }
}

/**
 * Adds a soft-delete flag to journal entries so a deletion can be pushed to
 * Firestore (erasing the online backup, as the Privacy Policy promises)
 * before the row is removed locally.
 */
val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE journal_entries ADD COLUMN pendingDelete INTEGER NOT NULL DEFAULT 0")
    }
}
