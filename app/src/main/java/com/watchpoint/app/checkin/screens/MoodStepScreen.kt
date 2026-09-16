package com.watchpoint.app.checkin.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.watchpoint.app.R
import com.watchpoint.app.checkin.Mood
import com.watchpoint.app.ui.components.AnswerPill
import com.watchpoint.app.ui.components.QuestionTitle
import com.watchpoint.app.ui.components.WpScreen
import com.watchpoint.app.ui.theme.WpSpace

/** Daily Check-In, step 1 of 4: mood. Picking an answer advances the flow immediately. */
@Composable
fun MoodStepScreen(
    selected: Mood?,
    onSelect: (Mood) -> Unit,
    onBack: () -> Unit
) {
    WpScreen(onBack = onBack, step = 1, totalSteps = CHECKIN_STEPS) {
        Spacer(Modifier.height(30.dp))
        QuestionTitle(text = stringResource(R.string.q_checkin_mood))

        Spacer(Modifier.height(26.dp))
        Column(verticalArrangement = Arrangement.spacedBy(WpSpace.Gap)) {
            Mood.entries.forEach { option ->
                AnswerPill(
                    text = "${option.emoji}  ${stringResource(option.label)}",
                    selected = selected == option,
                    onClick = { onSelect(option) }
                )
            }
        }
        Spacer(Modifier.weight(1f))
    }
}
