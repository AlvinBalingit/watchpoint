package com.watchpoint.app.checkin.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.watchpoint.app.R
import com.watchpoint.app.ui.components.ForestBackground
import com.watchpoint.app.ui.components.PrimaryButton
import com.watchpoint.app.ui.components.QuestionTitle
import com.watchpoint.app.ui.components.WeekStrip
import com.watchpoint.app.ui.theme.AccentGreen
import com.watchpoint.app.ui.theme.OnForest
import com.watchpoint.app.ui.theme.Orange
import com.watchpoint.app.ui.theme.WpSpace
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * A full-screen celebratory moment shown when a streak milestone is hit,
 * before handing off to the regular CheckInDoneScreen. Built entirely from
 * existing components/colors - Wabby, ForestBackground, the app's palette.
 */
@Composable
fun MilestoneCelebrationScreen(
    milestoneDays: Int,
    checkedInDatesThisWeek: Set<LocalDate>,
    onContinue: () -> Unit
) {
    val burstScale = remember { Animatable(0.4f) }
    val burstRotation = remember { Animatable(-8f) }

    LaunchedEffect(milestoneDays) {
        launch {
            burstScale.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = 220f))
        }
        launch {
            burstRotation.animateTo(0f, spring(dampingRatio = 0.4f, stiffness = 180f))
        }
    }

    Box(Modifier.fillMaxSize()) {
        ForestBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = WpSpace.ScreenH),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(0.6f))

            Image(
                painter = painterResource(R.drawable.wabby_jump),
                contentDescription = stringResource(R.string.cd_mascot),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(180.dp)
                    .scale(burstScale.value)
                    .rotate(burstRotation.value)
            )

            Spacer(Modifier.height(20.dp))
            QuestionTitle(text = stringResource(R.string.milestone_celebration_title), color = AccentGreen)
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.milestone_banner, milestoneDays),
                style = MaterialTheme.typography.headlineSmall,
                color = Orange,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.milestone_celebration_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = OnForest,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(28.dp))
            WeekStrip(checkedInDates = checkedInDatesThisWeek)

            Spacer(Modifier.weight(1f))
            PrimaryButton(text = stringResource(R.string.milestone_continue), onClick = onContinue)
            Spacer(Modifier.height(WpSpace.BottomBar))
        }
    }
}
