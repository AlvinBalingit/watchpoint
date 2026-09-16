package com.watchpoint.app.onboarding.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.watchpoint.app.R
import com.watchpoint.app.onboarding.JoinSource
import com.watchpoint.app.onboarding.MoodState
import com.watchpoint.app.ui.components.AnswerPill
import com.watchpoint.app.ui.components.MascotWithRing
import com.watchpoint.app.ui.components.PrimaryButton
import com.watchpoint.app.ui.components.QuestionTitle
import com.watchpoint.app.ui.components.RadioRow
import com.watchpoint.app.ui.components.WpScreen
import com.watchpoint.app.ui.theme.AccentGreen
import com.watchpoint.app.ui.theme.OnForest
import com.watchpoint.app.ui.theme.WpSpace

/* --------------------------------------------------------------- 06-07 ---- */

/**
 * Deck pages 6 and 7 are the same screen in two states: the orange Next button
 * only appears once an option has been picked, so it is bound to [selected].
 * Wabby peers in from the bottom-left corner behind the list.
 */
@Composable
fun SourceScreen(
    selected: JoinSource?,
    onSelect: (JoinSource) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        WpScreen(
            onBack = onBack,
            bottomBar = {
                AnimatedVisibility(
                    visible = selected != null,
                    enter = fadeIn() + slideInVertically { it / 2 },
                    exit = fadeOut() + slideOutVertically { it / 2 }
                ) {
                    PrimaryButton(
                        text = stringResource(R.string.next),
                        onClick = onNext
                    )
                }
            }
        ) {
            Spacer(Modifier.height(30.dp))
            QuestionTitle(text = stringResource(R.string.q_source))
            Spacer(Modifier.height(26.dp))

            JoinSource.entries.forEach { option ->
                RadioRow(
                    text = stringResource(option.label),
                    selected = selected == option,
                    onClick = { onSelect(option) }
                )
            }

            Spacer(Modifier.weight(1f))
        }

        // Wabby is decoration here, so he sits behind the touch targets.
        Image(
            painter = painterResource(R.drawable.wabby_laptop),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-34).dp, y = 26.dp)
                .size(215.dp)
        )
    }
}

/* ------------------------------------------------------------------ 08 ---- */

/**
 * Deck page 8. Picking an answer moves the flow on immediately - there is no
 * Next button on this screen in the layout.
 */
@Composable
fun MoodScreen(
    selected: MoodState?,
    onSelect: (MoodState) -> Unit,
    onBack: () -> Unit
) {
    WpScreen(onBack = onBack) {
        Spacer(Modifier.height(30.dp))
        QuestionTitle(text = stringResource(R.string.q_mood))

        Spacer(Modifier.height(8.dp))
        QuestionMarks()
        MascotWithRing(
            mascot = R.drawable.wabby_think,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            mascotSize = 180.dp,
            showRing = false
        )

        Spacer(Modifier.height(24.dp))
        Column(verticalArrangement = Arrangement.spacedBy(WpSpace.Gap)) {
            MoodState.entries.forEach { option ->
                AnswerPill(
                    text = stringResource(option.label),
                    selected = selected == option,
                    onClick = { onSelect(option) }
                )
            }
        }
        Spacer(Modifier.weight(1f))
    }
}

/** The three green question marks floating over Wabby's head on page 8. */
@Composable
private fun QuestionMarks() {
    Text(
        text = "? ? ?",
        style = MaterialTheme.typography.headlineMedium,
        color = AccentGreen,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

/* ------------------------------------------------------------------ 09 ---- */

/** Deck page 9. Wabby celebrates whatever the person just told us. */
@Composable
fun ReassuranceScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    WpScreen(
        onBack = onBack,
        bottomBar = { PrimaryButton(text = stringResource(R.string.next), onClick = onNext) }
    ) {
        Spacer(Modifier.height(44.dp))
        Text(
            text = stringResource(R.string.glad_title),
            style = MaterialTheme.typography.headlineSmall,
            color = OnForest,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.glad_body),
            style = MaterialTheme.typography.headlineSmall,
            color = OnForest,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.weight(1f))
        MascotWithRing(
            mascot = R.drawable.wabby_jump,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            mascotSize = 240.dp,
            showRing = false
        )
        Spacer(Modifier.weight(1.1f))
    }
}

/* ------------------------------------------------------------------ 10 ---- */

/** Deck page 10. The bridge into the five personalisation questions. */
@Composable
fun FinalStepScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    WpScreen(
        onBack = onBack,
        bottomBar = { PrimaryButton(text = stringResource(R.string.next), onClick = onNext) }
    ) {
        Spacer(Modifier.height(44.dp))
        Text(
            text = stringResource(R.string.final_title),
            style = MaterialTheme.typography.headlineSmall,
            color = AccentGreen,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.final_body),
            style = MaterialTheme.typography.bodyLarge,
            color = OnForest,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.final_note),
            style = MaterialTheme.typography.headlineSmall,
            color = OnForest,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.weight(1f))
        MascotWithRing(
            mascot = R.drawable.wabby_book,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            mascotSize = 200.dp,
            showRing = false
        )
        Spacer(Modifier.weight(1.1f))
    }
}
