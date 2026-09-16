package com.watchpoint.app.onboarding.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.watchpoint.app.R
import com.watchpoint.app.onboarding.Program
import com.watchpoint.app.ui.components.AnswerPill
import com.watchpoint.app.ui.components.Mascot
import com.watchpoint.app.ui.components.QuestionTitle
import com.watchpoint.app.ui.components.WpScreen
import com.watchpoint.app.ui.theme.WpSpace

/** Personalize block, step 5 of 6. Asks which academic program the person is in. */
@Composable
fun ProgramScreen(
    selected: Program?,
    onSelect: (Program) -> Unit,
    onBack: () -> Unit
) {
    WpScreen(onBack = onBack, step = 5, totalSteps = 6) {
        Spacer(Modifier.height(30.dp))
        QuestionTitle(text = stringResource(R.string.q_program))

        Spacer(Modifier.height(12.dp))
        Mascot(
            mascot = R.drawable.wabby_book,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            size = 175.dp
        )

        Spacer(Modifier.height(20.dp))
        Column(verticalArrangement = Arrangement.spacedBy(WpSpace.Gap)) {
            Program.entries.forEach { option ->
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
