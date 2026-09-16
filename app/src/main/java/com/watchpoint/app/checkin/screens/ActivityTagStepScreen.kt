package com.watchpoint.app.checkin.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.watchpoint.app.R
import com.watchpoint.app.checkin.ActivityTag
import com.watchpoint.app.checkin.InteractionTag
import com.watchpoint.app.ui.components.PrimaryButton
import com.watchpoint.app.ui.components.QuestionSubtitle
import com.watchpoint.app.ui.components.QuestionTitle
import com.watchpoint.app.ui.components.RadioRow
import com.watchpoint.app.ui.components.WpScreen
import com.watchpoint.app.ui.theme.AccentGreen
import com.watchpoint.app.ui.theme.Mint
import com.watchpoint.app.ui.theme.MintOutline
import com.watchpoint.app.ui.theme.OnForest
import com.watchpoint.app.ui.theme.OnForestMuted

/**
 * Daily Check-In, step 4: Activity and Interaction Stress Tagging. Both
 * groups are simple toggle rows so the whole screen stays within a couple of
 * taps; Skip is always available per spec.
 */
@Composable
fun ActivityTagStepScreen(
    selectedActivities: Set<ActivityTag>,
    selectedInteractions: Set<InteractionTag>,
    reflection: String,
    onToggleActivity: (ActivityTag) -> Unit,
    onToggleInteraction: (InteractionTag) -> Unit,
    onReflectionChange: (String) -> Unit,
    onBack: () -> Unit,
    onDone: () -> Unit,
    onSkip: () -> Unit
) {
    WpScreen(
        onBack = onBack,
        step = 4,
        totalSteps = CHECKIN_STEPS,
        bottomBar = {
            Column {
                PrimaryButton(text = stringResource(R.string.done), onClick = onDone)
                Spacer(Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.skip),
                    style = MaterialTheme.typography.bodyLarge,
                    color = OnForestMuted,
                    textAlign = TextAlign.Center,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onSkip)
                        .height(24.dp)
                )
            }
        }
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(20.dp))
            QuestionTitle(text = stringResource(R.string.q_checkin_tags))
            QuestionSubtitle(text = stringResource(R.string.q_checkin_tags_sub))

            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.tags_activity_header),
                style = MaterialTheme.typography.labelLarge,
                color = AccentGreen
            )
            ActivityTag.entries.forEach { tag ->
                RadioRow(
                    text = stringResource(tag.label),
                    selected = tag in selectedActivities,
                    onClick = { onToggleActivity(tag) }
                )
            }

            Spacer(Modifier.height(18.dp))
            Text(
                text = stringResource(R.string.tags_interaction_header),
                style = MaterialTheme.typography.labelLarge,
                color = AccentGreen
            )
            InteractionTag.entries.forEach { tag ->
                RadioRow(
                    text = stringResource(tag.label),
                    selected = tag in selectedInteractions,
                    onClick = { onToggleInteraction(tag) }
                )
            }

            Spacer(Modifier.height(18.dp))
            Text(
                text = stringResource(R.string.q_checkin_reflection),
                style = MaterialTheme.typography.labelLarge,
                color = AccentGreen
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = reflection,
                onValueChange = onReflectionChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.q_checkin_reflection_placeholder)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = OnForest,
                    unfocusedTextColor = OnForest,
                    focusedBorderColor = Mint,
                    unfocusedBorderColor = MintOutline,
                    cursorColor = Mint
                ),
                minLines = 3
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}
