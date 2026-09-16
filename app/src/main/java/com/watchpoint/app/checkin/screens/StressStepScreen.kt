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
import com.watchpoint.app.checkin.StressLevel
import com.watchpoint.app.ui.components.AnswerPill
import com.watchpoint.app.ui.components.QuestionSubtitle
import com.watchpoint.app.ui.components.QuestionTitle
import com.watchpoint.app.ui.components.WpScreen
import com.watchpoint.app.ui.theme.WpSpace

/** Daily Check-In, step 2: "How pressured do you feel today?" */
@Composable
fun StressStepScreen(
    selected: StressLevel?,
    onSelect: (StressLevel) -> Unit,
    onBack: () -> Unit,
    step: Int = 2,
    totalSteps: Int = CHECKIN_STEPS
) {
    WpScreen(onBack = onBack, step = step, totalSteps = totalSteps) {
        Spacer(Modifier.height(30.dp))
        QuestionTitle(text = stringResource(R.string.q_checkin_stress))
        QuestionSubtitle(text = stringResource(R.string.q_checkin_stress_sub))

        Spacer(Modifier.height(26.dp))
        Column(verticalArrangement = Arrangement.spacedBy(WpSpace.Gap)) {
            StressLevel.entries.forEach { option ->
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
