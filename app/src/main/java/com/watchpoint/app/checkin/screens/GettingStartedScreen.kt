package com.watchpoint.app.checkin.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.watchpoint.app.checkin.GETTING_STARTED_PROGRAM
import com.watchpoint.app.ui.components.Mascot
import com.watchpoint.app.ui.components.PrimaryButton
import com.watchpoint.app.ui.components.ProgressDashes
import com.watchpoint.app.ui.components.QuestionSubtitle
import com.watchpoint.app.ui.components.QuestionTitle
import com.watchpoint.app.ui.components.WpScreen
import com.watchpoint.app.ui.theme.AccentGreen
import com.watchpoint.app.ui.theme.ForestPanel
import com.watchpoint.app.ui.theme.MintOutline
import com.watchpoint.app.ui.theme.OnForest
import com.watchpoint.app.ui.theme.WpShape

/** A short 5-day structured path introducing the check-in habit and a couple of exercises. */
@Composable
fun GettingStartedScreen(
    dayCompleted: Int,
    onBack: () -> Unit,
    onCompleteDay: () -> Unit,
    onOpenExercise: (String) -> Unit
) {
    val finished = dayCompleted >= GETTING_STARTED_PROGRAM.size
    val currentDayIndex = dayCompleted.coerceAtMost(GETTING_STARTED_PROGRAM.size - 1)
    val currentDay = GETTING_STARTED_PROGRAM[currentDayIndex]

    WpScreen(
        onBack = onBack,
        bottomBar = if (finished) {
            null
        } else {
            {
                Column {
                    if (currentDay.exerciseId != null) {
                        PrimaryButton(
                            text = stringResource(R.string.getting_started_open_exercise),
                            onClick = { onOpenExercise(currentDay.exerciseId) }
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                    PrimaryButton(
                        text = stringResource(R.string.getting_started_complete_day, currentDay.day),
                        onClick = onCompleteDay
                    )
                }
            }
        }
    ) {
        Spacer(Modifier.height(20.dp))
        QuestionTitle(text = stringResource(R.string.getting_started_title), color = AccentGreen)
        QuestionSubtitle(text = stringResource(R.string.getting_started_subtitle))

        Spacer(Modifier.height(18.dp))
        ProgressDashes(
            current = dayCompleted.coerceAtMost(GETTING_STARTED_PROGRAM.size),
            total = GETTING_STARTED_PROGRAM.size,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
        )

        Spacer(Modifier.height(24.dp))
        Mascot(
            mascot = R.drawable.wabby_book,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            size = 150.dp
        )

        Spacer(Modifier.height(20.dp))
        if (finished) {
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
                    text = stringResource(R.string.getting_started_finished_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = OnForest,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.getting_started_finished_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnForest,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Text(
                text = stringResource(R.string.getting_started_day_label, currentDay.day),
                style = MaterialTheme.typography.labelLarge,
                color = AccentGreen,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(currentDay.title),
                style = MaterialTheme.typography.titleMedium,
                color = OnForest,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = stringResource(currentDay.prompt),
                style = MaterialTheme.typography.bodyMedium,
                color = OnForest,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(Modifier.weight(1f))
    }
}
