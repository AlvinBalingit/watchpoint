package com.watchpoint.app.checkin.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.watchpoint.app.R
import com.watchpoint.app.checkin.CheckInEntry
import com.watchpoint.app.ui.components.Mascot
import com.watchpoint.app.ui.components.MentalLoadBanner
import com.watchpoint.app.ui.components.PrimaryButton
import com.watchpoint.app.ui.components.ProgressGarden
import com.watchpoint.app.ui.components.QuestionTitle
import com.watchpoint.app.ui.components.WeekStrip
import com.watchpoint.app.ui.components.WpScreen
import com.watchpoint.app.ui.theme.AccentGreen
import com.watchpoint.app.ui.theme.ForestPanel
import com.watchpoint.app.ui.theme.Mint
import com.watchpoint.app.ui.theme.MintOutline
import com.watchpoint.app.ui.theme.OnForest
import com.watchpoint.app.ui.theme.OnForestMuted
import com.watchpoint.app.ui.theme.OnMint
import com.watchpoint.app.ui.theme.Orange
import com.watchpoint.app.ui.theme.WpShape
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

/** B. Home Dashboard - the quick mental readiness overview shown on app open. */
@Composable
fun DashboardScreen(
    latestEntry: CheckInEntry?,
    todayCheckedIn: Boolean,
    weeklyConsistency: Pair<Int, Int>,
    checkedInDatesThisWeek: Set<LocalDate>,
    currentStreak: Int,
    gardenProgress: Int,
    hasStreakGoal: Boolean,
    mentalLoadWarning: Boolean,
    highDemandMode: Boolean,
    onHighDemandModeChange: (Boolean) -> Unit,
    onStartCheckIn: () -> Unit,
    onViewTrends: () -> Unit,
    onViewWeeklySummary: () -> Unit,
    onOpenExercises: () -> Unit,
    onOpenGettingStarted: () -> Unit,
    onOpenStreakGoal: () -> Unit,
    onOpenJournal: () -> Unit,
    onOpenSettings: () -> Unit
) {
    WpScreen(
        bottomBar = {
            PrimaryButton(
                text = if (todayCheckedIn) {
                    stringResource(R.string.checked_in_today)
                } else {
                    stringResource(R.string.start_checkin)
                },
                onClick = onStartCheckIn,
                enabled = !todayCheckedIn
            )
        }
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(20.dp))
            TappableWabby()
            Spacer(Modifier.height(10.dp))
            QuestionTitle(text = stringResource(R.string.dashboard_title), color = AccentGreen)

            Spacer(Modifier.height(20.dp))
            TodayStatusCard(latestEntry)

            Spacer(Modifier.height(16.dp))
            Text(
                text = lastCheckInText(latestEntry),
                style = MaterialTheme.typography.bodyMedium,
                color = OnForestMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.weekly_consistency, weeklyConsistency.first, weeklyConsistency.second),
                style = MaterialTheme.typography.bodyMedium,
                color = OnForest
            )
            Spacer(Modifier.height(8.dp))
            WeekStrip(checkedInDates = checkedInDatesThisWeek)

            if (currentStreak > 0) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.current_streak, currentStreak),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Orange
                )
            }

            Spacer(Modifier.height(16.dp))
            ProgressGarden(
                progress = gardenProgress,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            if (mentalLoadWarning) {
                Spacer(Modifier.height(16.dp))
                MentalLoadBanner()
            }

            Spacer(Modifier.height(16.dp))
            HighDemandModeRow(highDemandMode, onHighDemandModeChange)

            Spacer(Modifier.height(20.dp))
            DashboardLinkRow(
                text = stringResource(
                    if (hasStreakGoal) R.string.change_streak_goal else R.string.set_streak_goal
                ),
                onClick = onOpenStreakGoal
            )
            Spacer(Modifier.height(10.dp))
            DashboardLinkRow(
                text = stringResource(R.string.view_exercises),
                onClick = onOpenExercises
            )
            Spacer(Modifier.height(10.dp))
            DashboardLinkRow(
                text = stringResource(R.string.view_getting_started),
                onClick = onOpenGettingStarted
            )
            Spacer(Modifier.height(10.dp))
            DashboardLinkRow(
                text = stringResource(R.string.view_trends),
                onClick = onViewTrends
            )
            Spacer(Modifier.height(10.dp))
            DashboardLinkRow(
                text = stringResource(R.string.view_weekly_summary),
                onClick = onViewWeeklySummary
            )
            Spacer(Modifier.height(10.dp))
            DashboardLinkRow(
                text = stringResource(R.string.dashboard_journal),
                onClick = onOpenJournal
            )
            Spacer(Modifier.height(10.dp))
            DashboardLinkRow(
                text = stringResource(R.string.view_settings),
                onClick = onOpenSettings
            )
            Spacer(Modifier.height(20.dp))
        }
    }
}

/** Wabby on the dashboard reacts to a tap with a bounce and a rotating encouragement line. */
@Composable
private fun TappableWabby() {
    val encouragements = stringArrayResource(R.array.dashboard_encouragements)
    var message by remember { mutableStateOf<String?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Mascot(
            mascot = R.drawable.wabby_wink,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            size = 130.dp,
            onTap = { message = encouragements[Random.nextInt(encouragements.size)] }
        )
        AnimatedVisibility(visible = message != null, enter = fadeIn(), exit = fadeOut()) {
            Text(
                text = message.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                color = OnMint,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clip(WpShape.Pill)
                    .background(Mint.copy(alpha = 0.85f))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun TodayStatusCard(latestEntry: CheckInEntry?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(WpShape.Card)
            .background(ForestPanel.copy(alpha = 0.72f))
            .border(1.dp, MintOutline.copy(alpha = 0.5f), WpShape.Card)
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.todays_status),
            style = MaterialTheme.typography.labelLarge,
            color = OnForestMuted
        )
        Spacer(Modifier.height(8.dp))
        if (latestEntry != null) {
            Text(
                text = "${latestEntry.mood.emoji}  ${stringResource(latestEntry.mood.label)}",
                style = MaterialTheme.typography.headlineSmall,
                color = OnForest,
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                text = stringResource(R.string.no_checkin_yet),
                style = MaterialTheme.typography.headlineSmall,
                color = OnForest,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun lastCheckInText(latestEntry: CheckInEntry?): String {
    if (latestEntry == null) return stringResource(R.string.no_checkin_yet)
    val formatter = DateTimeFormatter.ofPattern("MMM d")
    val label = if (latestEntry.date == LocalDate.now()) {
        stringResource(R.string.today)
    } else {
        latestEntry.date.format(formatter)
    }
    return stringResource(R.string.last_checkin, label)
}

@Composable
private fun HighDemandModeRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(WpShape.Card)
            .background(ForestPanel.copy(alpha = 0.5f))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.high_demand_mode),
                style = MaterialTheme.typography.labelLarge,
                color = OnForest
            )
            Text(
                text = stringResource(R.string.high_demand_mode_sub),
                style = MaterialTheme.typography.bodySmall,
                color = OnForestMuted
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = OnMint,
                checkedTrackColor = Mint,
                uncheckedThumbColor = OnForestMuted,
                uncheckedTrackColor = ForestPanel
            )
        )
    }
}

@Composable
private fun DashboardLinkRow(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(WpShape.Pill)
            .background(Mint.copy(alpha = 0.15f))
            .border(1.dp, MintOutline.copy(alpha = 0.4f), WpShape.Pill)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = OnForest
        )
    }
}
