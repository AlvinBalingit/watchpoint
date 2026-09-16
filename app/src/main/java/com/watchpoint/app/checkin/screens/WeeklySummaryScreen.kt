package com.watchpoint.app.checkin.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.watchpoint.app.R
import com.watchpoint.app.checkin.CheckInEntry
import com.watchpoint.app.ui.components.LineChart
import com.watchpoint.app.ui.components.QuestionTitle
import com.watchpoint.app.ui.components.WpScreen
import com.watchpoint.app.ui.theme.ForestPanel
import com.watchpoint.app.ui.theme.Mint
import com.watchpoint.app.ui.theme.MintOutline
import com.watchpoint.app.ui.theme.OnForest
import com.watchpoint.app.ui.theme.OnForestMuted
import com.watchpoint.app.ui.theme.Orange
import com.watchpoint.app.ui.theme.WpShape
import java.time.format.DateTimeFormatter

/** F. Weekly Summary - automatically generated reflection on the last 7 days. */
@Composable
fun WeeklySummaryScreen(
    history: List<CheckInEntry>,
    weeklyReflection: String,
    onWeeklyReflectionChange: (String) -> Unit,
    onBack: () -> Unit
) {
    val week = history.sortedByDescending { it.date }.take(7)

    WpScreen(onBack = onBack) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(20.dp))
            QuestionTitle(text = stringResource(R.string.weekly_summary_title))

            Spacer(Modifier.height(20.dp))
            if (week.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_checkin_yet),
                    style = MaterialTheme.typography.bodyLarge,
                    color = OnForestMuted
                )
            } else {
                val avgStress = week.map { it.stress.score }.average()
                val bestDay = week.minByOrNull { it.stress.score }
                val hardestDay = week.maxByOrNull { it.stress.score }
                val formatter = DateTimeFormatter.ofPattern("MMM d")

                SummaryStatRow(
                    label = stringResource(R.string.summary_avg_stress),
                    value = "%.1f / 5".format(avgStress)
                )
                SummaryStatRow(
                    label = stringResource(R.string.summary_best_day),
                    value = bestDay?.date?.format(formatter) ?: "-"
                )
                SummaryStatRow(
                    label = stringResource(R.string.summary_hardest_day),
                    value = hardestDay?.date?.format(formatter) ?: "-"
                )

                Spacer(Modifier.height(18.dp))
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 })
                ) {
                    LineChart(
                        title = stringResource(R.string.trends_stress),
                        points = week.sortedBy { it.date }.map { it.date to it.stress.score.toFloat() },
                        color = Orange
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.weekly_reflection_prompt),
                style = MaterialTheme.typography.labelLarge,
                color = OnForest
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = weeklyReflection,
                onValueChange = onWeeklyReflectionChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.weekly_reflection_placeholder)) },
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
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SummaryStatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(WpShape.Card)
            .background(ForestPanel.copy(alpha = 0.72f))
            .border(1.dp, MintOutline.copy(alpha = 0.5f), WpShape.Card)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = OnForestMuted)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, color = OnForest)
    }
    Spacer(Modifier.height(8.dp))
}
