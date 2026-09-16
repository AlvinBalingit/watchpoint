package com.watchpoint.app.checkin.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.watchpoint.app.checkin.SelfCareSuggestion
import com.watchpoint.app.ui.components.Mascot
import com.watchpoint.app.ui.components.PrimaryButton
import com.watchpoint.app.ui.components.QuestionTitle
import com.watchpoint.app.ui.components.WpScreen
import com.watchpoint.app.ui.theme.AccentGreen
import com.watchpoint.app.ui.theme.ForestPanel
import com.watchpoint.app.ui.theme.Mint
import com.watchpoint.app.ui.theme.MintOutline
import com.watchpoint.app.ui.theme.OnForest
import com.watchpoint.app.ui.theme.OnMint
import com.watchpoint.app.ui.theme.WpShape

/** D. Automated feedback shown right after a check-in is submitted. Non-clinical, own-data-only. */
@Composable
fun CheckInDoneScreen(
    messages: List<String>,
    suggestion: SelfCareSuggestion?,
    onBackToDashboard: () -> Unit
) {
    WpScreen(
        bottomBar = {
            PrimaryButton(text = stringResource(R.string.back_to_dashboard), onClick = onBackToDashboard)
        }
    ) {
        Spacer(Modifier.height(30.dp))
        Mascot(
            mascot = R.drawable.wabby_jump,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            size = 150.dp
        )
        Spacer(Modifier.height(10.dp))
        QuestionTitle(text = stringResource(R.string.checkin_done_title), color = AccentGreen)

        Spacer(Modifier.height(22.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clip(WpShape.Card)
                .background(ForestPanel.copy(alpha = 0.72f))
                .border(1.dp, MintOutline.copy(alpha = 0.5f), WpShape.Card)
                .padding(16.dp)
        ) {
            messages.forEach { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = OnForest,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (suggestion != null) {
            Spacer(Modifier.height(14.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(WpShape.Card)
                    .background(Mint.copy(alpha = 0.15f))
                    .border(1.dp, MintOutline.copy(alpha = 0.4f), WpShape.Card)
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.selfcare_section_title),
                    style = MaterialTheme.typography.labelLarge,
                    color = OnMint
                )
                Text(
                    text = stringResource(suggestion.title),
                    style = MaterialTheme.typography.titleMedium,
                    color = OnForest
                )
                Text(
                    text = stringResource(suggestion.body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnForest
                )
            }
        }
        Spacer(Modifier.weight(1f))
    }
}
