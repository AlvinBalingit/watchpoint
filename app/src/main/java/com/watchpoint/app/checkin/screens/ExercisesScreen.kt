package com.watchpoint.app.checkin.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.watchpoint.app.R
import com.watchpoint.app.checkin.EXERCISES
import com.watchpoint.app.checkin.Exercise
import com.watchpoint.app.ui.components.QuestionSubtitle
import com.watchpoint.app.ui.components.QuestionTitle
import com.watchpoint.app.ui.components.WpScreen
import com.watchpoint.app.ui.theme.AccentGreen
import com.watchpoint.app.ui.theme.ForestPanel
import com.watchpoint.app.ui.theme.Mint
import com.watchpoint.app.ui.theme.MintOutline
import com.watchpoint.app.ui.theme.OnForest
import com.watchpoint.app.ui.theme.OnForestMuted
import com.watchpoint.app.ui.theme.WpShape

/** A browsable library of short self-guided practices, open any time - not just after a check-in. */
@Composable
fun ExercisesScreen(
    completedExerciseIds: Set<String>,
    onBack: () -> Unit,
    onOpenExercise: (String) -> Unit
) {
    WpScreen(onBack = onBack) {
        Spacer(Modifier.height(20.dp))
        QuestionTitle(text = stringResource(R.string.exercises_title), color = AccentGreen)
        QuestionSubtitle(text = stringResource(R.string.exercises_subtitle))

        Spacer(Modifier.height(20.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            EXERCISES.forEach { exercise ->
                ExerciseRow(
                    exercise = exercise,
                    done = exercise.id in completedExerciseIds,
                    onClick = { onOpenExercise(exercise.id) }
                )
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ExerciseRow(
    exercise: Exercise,
    done: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(WpShape.Card)
            .background(ForestPanel.copy(alpha = 0.72f))
            .border(1.dp, MintOutline.copy(alpha = 0.5f), WpShape.Card)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = stringResource(exercise.title),
                style = MaterialTheme.typography.labelLarge,
                color = OnForest
            )
            Text(
                text = "${stringResource(exercise.category.label)} · ${
                    stringResource(R.string.exercise_minutes, exercise.durationMinutes)
                }",
                style = MaterialTheme.typography.bodySmall,
                color = OnForestMuted
            )
        }
        if (done) {
            Text(
                text = stringResource(R.string.exercise_done_badge),
                style = MaterialTheme.typography.labelSmall,
                color = Mint,
                modifier = Modifier
                    .clip(WpShape.Pill)
                    .background(Mint.copy(alpha = 0.18f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}
