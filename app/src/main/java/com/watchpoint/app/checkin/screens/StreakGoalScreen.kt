package com.watchpoint.app.checkin.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.watchpoint.app.R
import com.watchpoint.app.ui.components.Mascot
import com.watchpoint.app.ui.components.QuestionSubtitle
import com.watchpoint.app.ui.components.QuestionTitle
import com.watchpoint.app.ui.components.WpScreen
import com.watchpoint.app.ui.theme.ForestPanel
import com.watchpoint.app.ui.theme.Mint
import com.watchpoint.app.ui.theme.MintOutline
import com.watchpoint.app.ui.theme.OnForest
import com.watchpoint.app.ui.theme.OnForestMuted
import com.watchpoint.app.ui.theme.SelectGreen
import com.watchpoint.app.ui.theme.WpShape

private data class GoalOption(val days: Int, val daysLabelRes: Int, val achievementLabelRes: Int)

private val GOAL_OPTIONS = listOf(
    GoalOption(2, R.string.streak_goal_2_days, R.string.streak_goal_2_label),
    GoalOption(5, R.string.streak_goal_5_days, R.string.streak_goal_5_label),
    GoalOption(7, R.string.streak_goal_7_days, R.string.streak_goal_7_label),
    GoalOption(14, R.string.streak_goal_14_days, R.string.streak_goal_14_label)
)

/** Lets the user commit to a target streak length, framed with short achievement-style labels. */
@Composable
fun StreakGoalScreen(
    currentTargetDays: Int?,
    onBack: () -> Unit,
    onCommit: (Int) -> Unit
) {
    WpScreen(onBack = onBack) {
        Spacer(Modifier.height(20.dp))
        QuestionTitle(text = stringResource(R.string.q_streak_goal))
        QuestionSubtitle(text = stringResource(R.string.q_streak_goal_sub))

        Spacer(Modifier.height(16.dp))
        Mascot(mascot = R.drawable.wabby_wink, size = 130.dp)

        Spacer(Modifier.height(20.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            GOAL_OPTIONS.forEach { option ->
                GoalRow(
                    daysLabel = stringResource(option.daysLabelRes),
                    achievementLabel = stringResource(option.achievementLabelRes),
                    selected = option.days == currentTargetDays,
                    onClick = { onCommit(option.days) }
                )
            }
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun GoalRow(
    daysLabel: String,
    achievementLabel: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(WpShape.Card)
            .background(if (selected) SelectGreen.copy(alpha = 0.25f) else ForestPanel.copy(alpha = 0.72f))
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) SelectGreen else MintOutline.copy(alpha = 0.5f),
                shape = WpShape.Card
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Text(
            text = daysLabel,
            style = MaterialTheme.typography.titleMedium,
            color = if (selected) Mint else OnForest,
            textAlign = TextAlign.Start
        )
        Text(
            text = achievementLabel,
            style = MaterialTheme.typography.bodyMedium,
            color = OnForestMuted
        )
    }
}
