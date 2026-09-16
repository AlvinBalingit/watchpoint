package com.watchpoint.app.onboarding.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.watchpoint.app.R
import com.watchpoint.app.onboarding.ReasonForUsing
import com.watchpoint.app.ui.components.PrimaryButton
import com.watchpoint.app.ui.components.QuestionSubtitle
import com.watchpoint.app.ui.components.QuestionTitle
import com.watchpoint.app.ui.components.RadioRow
import com.watchpoint.app.ui.components.WpScreen

/**
 * Multi-select "what brings you here" checklist, distinct from the acquisition
 * source question - this one is about the person's own motivation for using
 * the app, styled after the MindDoc-style checklist reference.
 */
@Composable
fun ReasonForUsingScreen(
    selected: Set<ReasonForUsing>,
    onToggle: (ReasonForUsing) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    WpScreen(
        onBack = onBack,
        bottomBar = {
            AnimatedVisibility(
                visible = selected.isNotEmpty(),
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { it / 2 }
            ) {
                PrimaryButton(
                    text = stringResource(R.string.confirm),
                    onClick = onNext
                )
            }
        }
    ) {
        Spacer(Modifier.height(30.dp))
        QuestionTitle(text = stringResource(R.string.q_reason_for_using))
        QuestionSubtitle(text = stringResource(R.string.q_reason_for_using_sub))
        Spacer(Modifier.height(26.dp))

        ReasonForUsing.entries.forEach { option ->
            RadioRow(
                text = stringResource(option.label),
                selected = option in selected,
                onClick = { onToggle(option) }
            )
        }

        Spacer(Modifier.height(12.dp))
    }
}
