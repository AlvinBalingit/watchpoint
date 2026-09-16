package com.watchpoint.app.checkin.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.watchpoint.app.R
import com.watchpoint.app.checkin.CheckInEntry
import com.watchpoint.app.ui.components.LineChart
import com.watchpoint.app.ui.components.QuestionTitle
import com.watchpoint.app.ui.components.WindowToggle
import com.watchpoint.app.ui.components.WpScreen
import com.watchpoint.app.ui.components.trendSummary
import com.watchpoint.app.ui.theme.Cyan
import com.watchpoint.app.ui.theme.Mint
import com.watchpoint.app.ui.theme.OnForestMuted
import com.watchpoint.app.ui.theme.Orange

/** E. Trend Visualization - 7-day and 14-day charts for mood, stress and readiness. */
@Composable
fun TrendsScreen(
    history: List<CheckInEntry>,
    onBack: () -> Unit
) {
    var showShortWindow by remember { mutableStateOf(true) }

    val windowSize = if (showShortWindow) 7 else 14
    val entries = history.sortedBy { it.date }.takeLast(windowSize)

    val moodLabel = stringResource(R.string.trends_mood)
    val stressLabel = stringResource(R.string.trends_stress)
    val readinessLabel = stringResource(R.string.trends_readiness)

    val moodPoints = entries.map { it.date to it.mood.score.toFloat() }
    val stressPoints = entries.map { it.date to it.stress.score.toFloat() }
    val readinessPoints = entries.map { it.date to it.readiness.score.toFloat() }

    WpScreen(onBack = onBack) {
        Spacer(Modifier.height(20.dp))
        QuestionTitle(text = stringResource(R.string.trends_title))
        Text(
            text = stringResource(R.string.trends_tap_hint),
            style = MaterialTheme.typography.bodySmall,
            color = OnForestMuted
        )

        Spacer(Modifier.height(18.dp))
        WindowToggle(
            shortLabel = stringResource(R.string.trends_7_day),
            longLabel = stringResource(R.string.trends_14_day),
            isShortSelected = showShortWindow,
            onSelectShort = { showShortWindow = true },
            onSelectLong = { showShortWindow = false }
        )

        Spacer(Modifier.height(20.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                TrendSummaryLine(trendSummary(moodPoints, moodLabel))
                LineChart(title = moodLabel, points = moodPoints, color = Mint)
            }
            Column {
                TrendSummaryLine(trendSummary(stressPoints, stressLabel))
                LineChart(title = stressLabel, points = stressPoints, color = Orange)
            }
            Column {
                TrendSummaryLine(trendSummary(readinessPoints, readinessLabel))
                LineChart(title = readinessLabel, points = readinessPoints, color = Cyan)
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun TrendSummaryLine(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = OnForestMuted,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}
