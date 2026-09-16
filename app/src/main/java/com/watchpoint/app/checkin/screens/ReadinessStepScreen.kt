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
import com.watchpoint.app.checkin.Readiness
import com.watchpoint.app.ui.components.AnswerPill
import com.watchpoint.app.ui.components.QuestionSubtitle
import com.watchpoint.app.ui.components.QuestionTitle
import com.watchpoint.app.ui.components.WpScreen
import com.watchpoint.app.ui.theme.WpSpace

/**
 * Daily Check-In, step 3: readiness. One 5-point rating informed by three
 * angles (academic tasks, focus/decision-making, emotional control), shown as
 * supporting text rather than three separate questions.
 */
@Composable
fun ReadinessStepScreen(
    selected: Readiness?,
    onSelect: (Readiness) -> Unit,
    onBack: () -> Unit,
    step: Int = 3,
    totalSteps: Int = CHECKIN_STEPS
) {
    WpScreen(onBack = onBack, step = step, totalSteps = totalSteps) {
        Spacer(Modifier.height(30.dp))
        QuestionTitle(text = stringResource(R.string.q_checkin_readiness))
        QuestionSubtitle(text = stringResource(R.string.q_checkin_readiness_sub))

        Spacer(Modifier.height(26.dp))
        Column(verticalArrangement = Arrangement.spacedBy(WpSpace.Gap)) {
            Readiness.entries.forEach { option ->
                AnswerPill(
                    text = stringResource(option.label),
                    selected = selected == option,
                    onClick = { onSelect(option) }
                )
            }
        }
        Spacer(Modifier.weight(1f))
    }
}
