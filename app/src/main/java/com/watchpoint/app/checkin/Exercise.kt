package com.watchpoint.app.checkin

import androidx.annotation.StringRes
import com.watchpoint.app.R

/** Grouping used on the exercise library screen. */
enum class ExerciseCategory(@StringRes val label: Int) {
    Breathing(R.string.exercise_cat_breathing),
    Journaling(R.string.exercise_cat_journaling),
    Grounding(R.string.exercise_cat_grounding),
    Movement(R.string.exercise_cat_movement),
    Rest(R.string.exercise_cat_rest)
}

/** A short, self-guided practice the user can open any time from the exercise library. */
data class Exercise(
    val id: String,
    @StringRes val title: Int,
    @StringRes val description: Int,
    val category: ExerciseCategory,
    val durationMinutes: Int,
    val requiresTextEntry: Boolean = false,
    @StringRes val steps: List<Int>
)

val EXERCISES: List<Exercise> = listOf(
    Exercise(
        id = "breathing_4",
        title = R.string.exercise_breathing_title,
        description = R.string.exercise_breathing_desc,
        category = ExerciseCategory.Breathing,
        durationMinutes = 2,
        steps = listOf(
            R.string.exercise_breathing_step1,
            R.string.exercise_breathing_step2,
            R.string.exercise_breathing_step3,
            R.string.exercise_breathing_step4
        )
    ),
    Exercise(
        id = "grounding_54321",
        title = R.string.exercise_grounding_title,
        description = R.string.exercise_grounding_desc,
        category = ExerciseCategory.Grounding,
        durationMinutes = 3,
        steps = listOf(
            R.string.exercise_grounding_step1,
            R.string.exercise_grounding_step2,
            R.string.exercise_grounding_step3,
            R.string.exercise_grounding_step4,
            R.string.exercise_grounding_step5
        )
    ),
    Exercise(
        id = "gratitude_note",
        title = R.string.exercise_gratitude_title,
        description = R.string.exercise_gratitude_desc,
        category = ExerciseCategory.Journaling,
        durationMinutes = 3,
        requiresTextEntry = true,
        steps = listOf(
            R.string.exercise_gratitude_step1,
            R.string.exercise_gratitude_step2,
            R.string.exercise_gratitude_step3
        )
    ),
    Exercise(
        id = "brain_dump",
        title = R.string.exercise_braindump_title,
        description = R.string.exercise_braindump_desc,
        category = ExerciseCategory.Journaling,
        durationMinutes = 5,
        requiresTextEntry = true,
        steps = listOf(
            R.string.exercise_braindump_step1,
            R.string.exercise_braindump_step2,
            R.string.exercise_braindump_step3
        )
    ),
    Exercise(
        id = "short_walk",
        title = R.string.exercise_movement_title,
        description = R.string.exercise_movement_desc,
        category = ExerciseCategory.Movement,
        durationMinutes = 5,
        steps = listOf(
            R.string.exercise_movement_step1,
            R.string.exercise_movement_step2,
            R.string.exercise_movement_step3
        )
    ),
    Exercise(
        id = "wind_down",
        title = R.string.exercise_rest_title,
        description = R.string.exercise_rest_desc,
        category = ExerciseCategory.Rest,
        durationMinutes = 4,
        steps = listOf(
            R.string.exercise_rest_step1,
            R.string.exercise_rest_step2,
            R.string.exercise_rest_step3
        )
    ),
    Exercise(
        id = "body_scan",
        title = R.string.exercise_bodyscan_title,
        description = R.string.exercise_bodyscan_desc,
        category = ExerciseCategory.Grounding,
        durationMinutes = 4,
        steps = listOf(
            R.string.exercise_bodyscan_step1,
            R.string.exercise_bodyscan_step2,
            R.string.exercise_bodyscan_step3
        )
    ),
    Exercise(
        id = "pre_exam_reset",
        title = R.string.exercise_preexam_title,
        description = R.string.exercise_preexam_desc,
        category = ExerciseCategory.Grounding,
        durationMinutes = 2,
        steps = listOf(
            R.string.exercise_preexam_step1,
            R.string.exercise_preexam_step2,
            R.string.exercise_preexam_step3,
            R.string.exercise_preexam_step4
        )
    )
)
