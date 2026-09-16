package com.watchpoint.app.navigation

/**
 * One route per screen in the layout deck, in the order they appear.
 * Kept as plain string constants so the graph stays readable at a glance.
 */
object Route {
    const val WELCOME = "welcome"          // 01 wordmark + Get started
    const val LOADING = "loading"          // 02 spinner
    const val GREETING = "greeting"        // 03 Mabuhay! I'm Wabby!
    const val AUTH = "auth"                // 04 Apple / Google / Email
    const val QUOTE = "quote"              // 05 Shpancer quote
    const val SOURCE = "source"            // 06-07 How did you first join?
    const val REASON_FOR_USING = "reason_for_using" // 06b What brings you here?
    const val MOOD = "mood"                // 08 How have you been lately?
    const val REASSURANCE = "reassurance"  // 09 So glad to hear that!
    const val FINAL_STEP = "final_step"    // 10 You're doing great
    const val WAKE_TIME = "wake_time"      // 11 step 1 of 6
    const val BED_TIME = "bed_time"        // 12 step 2 of 6
    const val INTERESTS = "interests"      // 13 step 3 of 6
    const val SUPPORT = "support"          // 14 step 4 of 6
    const val PROGRAM = "program"          // 15b step 5 of 6
    const val AGE = "age"                  // 15 step 6 of 6
    const val SUMMARY = "summary"          // end of onboarding

    // Daily check-in flow
    const val HOME = "home"                          // B  Home Dashboard
    const val CHECKIN_MOOD = "checkin_mood"          // C1 Mood
    const val CHECKIN_STRESS = "checkin_stress"      // C2 Stress level
    const val CHECKIN_READINESS = "checkin_readiness" // C3 Readiness
    const val CHECKIN_TAGS = "checkin_tags"          // C4 Activity + interaction tagging
    const val CHECKIN_DONE = "checkin_done"          // D  Automated feedback
    const val TRENDS = "trends"                      // E  Trend visualization
    const val WEEKLY_SUMMARY = "weekly_summary"       // F  Weekly summary
    const val SETTINGS = "settings"                  // Reminder preferences
    const val TERMS = "terms"                        // Terms of Service
    const val PRIVACY = "privacy"                    // Philippines Privacy Policy
    const val EXERCISES = "exercises"                 // Browsable exercise library
    const val EXERCISE_DETAIL = "exercise_detail"     // {id} appended at navigation time
    const val GETTING_STARTED = "getting_started"     // Short structured program
    const val STREAK_GOAL = "streak_goal"             // Commit to a target streak length
    const val MILESTONE_CELEBRATION = "milestone_celebration" // Full-screen streak milestone moment
    const val JOURNAL = "journal"                      // Free-form journal entries

    fun exerciseDetail(id: String) = "$EXERCISE_DETAIL/$id"
}
