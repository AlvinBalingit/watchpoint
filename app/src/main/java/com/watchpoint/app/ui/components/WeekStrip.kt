package com.watchpoint.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.watchpoint.app.ui.theme.Mint
import com.watchpoint.app.ui.theme.MintOutline
import com.watchpoint.app.ui.theme.OnForestMuted
import java.time.DayOfWeek
import java.time.LocalDate

private val DAY_INITIALS = listOf("S", "M", "T", "W", "T", "F", "S")

/**
 * A calendar-style strip for the last 7 days, oldest to newest, with a
 * filled dot for days checked in and an outline ring on today's slot.
 * Shared by the Dashboard and the milestone celebration screen so the two
 * don't duplicate this drawing logic. Uses only the app's existing palette.
 */
@Composable
fun WeekStrip(
    checkedInDates: Set<LocalDate>,
    modifier: Modifier = Modifier,
    today: LocalDate = LocalDate.now()
) {
    val days = (6 downTo 0).map { today.minusDays(it.toLong()) }

    Column(modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            days.forEach { date ->
                Text(
                    text = DAY_INITIALS[date.dayOfWeek.sundayIndex()],
                    style = MaterialTheme.typography.labelSmall,
                    color = OnForestMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            days.forEach { date ->
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    val checkedIn = date in checkedInDates
                    val isToday = date == today
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(if (checkedIn) Mint else OnForestMuted.copy(alpha = 0.18f))
                            .then(
                                if (isToday) {
                                    Modifier.border(1.5.dp, MintOutline, CircleShape)
                                } else {
                                    Modifier
                                }
                            )
                    )
                }
            }
        }
    }
}

/** Sunday-first index (0..6) matching [DAY_INITIALS]. */
private fun DayOfWeek.sundayIndex(): Int = (value % 7)
