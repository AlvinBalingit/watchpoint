package com.watchpoint.app.onboarding.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.watchpoint.app.R
import com.watchpoint.app.onboarding.OnboardingViewModel
import com.watchpoint.app.ui.components.Mascot
import com.watchpoint.app.ui.components.PrimaryButton
import com.watchpoint.app.ui.components.QuestionTitle
import com.watchpoint.app.ui.components.WpScreen
import com.watchpoint.app.ui.theme.AccentGreen
import com.watchpoint.app.ui.theme.ForestPanel
import com.watchpoint.app.ui.theme.MintOutline
import com.watchpoint.app.ui.theme.OnForest
import com.watchpoint.app.ui.theme.OnForestMuted
import com.watchpoint.app.ui.theme.WpShape

/**
 * Not in the layout deck - this is where the front end stops.
 *
 * It reads back everything the flow gathered, which makes the whole thing
 * demonstrable end to end and gives an obvious seam for the real home screen.
 */
@Composable
fun SummaryScreen(
    state: OnboardingViewModel,
    onBack: () -> Unit,
    onEnter: () -> Unit
) {
    val notSet = stringResource(R.string.not_set)

    // map is inline, so stringResource is legal inside it; joinToString is not
    val interestLabels = state.interests.map { stringResource(it.label) }
    val interests = if (interestLabels.isEmpty()) notSet else interestLabels.joinToString(", ")

    val reasonLabels = state.reasonsForUsing.map { stringResource(it.label) }
    val reasons = if (reasonLabels.isEmpty()) notSet else reasonLabels.joinToString(", ")

    WpScreen(
        onBack = onBack,
        bottomBar = {
            PrimaryButton(
                text = stringResource(R.string.summary_enter),
                onClick = onEnter
            )
        }
    ) {
        Spacer(Modifier.height(20.dp))
        Mascot(
            mascot = R.drawable.wabby_jump,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            size = 170.dp
        )
        Spacer(Modifier.height(10.dp))
        QuestionTitle(text = stringResource(R.string.summary_title), color = AccentGreen)
        Text(
            text = stringResource(R.string.summary_body),
            style = MaterialTheme.typography.bodyMedium,
            color = OnForestMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        )

        Spacer(Modifier.height(22.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(1.dp),
            modifier = Modifier
                .clip(WpShape.Card)
                .background(ForestPanel.copy(alpha = 0.72f))
                .border(1.dp, MintOutline.copy(alpha = 0.5f), WpShape.Card)
                .verticalScroll(rememberScrollState())
        ) {
            SummaryRow(
                label = stringResource(R.string.summary_source),
                value = state.source?.let { stringResource(it.label) } ?: notSet
            )
            SummaryRow(
                label = stringResource(R.string.summary_reasons),
                value = reasons
            )
            SummaryRow(
                label = stringResource(R.string.summary_mood),
                value = state.mood?.let { stringResource(it.label) } ?: notSet
            )
            SummaryRow(
                label = stringResource(R.string.summary_wake),
                value = state.wakeTime.format()
            )
            SummaryRow(
                label = stringResource(R.string.summary_bed),
                value = state.bedTime.format()
            )
            SummaryRow(
                label = stringResource(R.string.summary_interests),
                value = interests
            )
            SummaryRow(
                label = stringResource(R.string.summary_support),
                value = state.support?.let { stringResource(it.label) } ?: notSet
            )
            SummaryRow(
                label = stringResource(R.string.summary_program),
                value = state.program?.let { stringResource(it.label) } ?: notSet
            )
            SummaryRow(
                label = stringResource(R.string.summary_age),
                value = state.ageGroup?.let { stringResource(it.label) } ?: notSet
            )
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 11.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = OnForestMuted,
            modifier = Modifier.weight(0.8f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = OnForest,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.2f)
        )
    }
}
