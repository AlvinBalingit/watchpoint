package com.watchpoint.app.onboarding.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.watchpoint.app.R
import com.watchpoint.app.onboarding.AgeGroup
import com.watchpoint.app.onboarding.Interest
import com.watchpoint.app.onboarding.SupportLevel
import com.watchpoint.app.onboarding.TimeOfDay
import com.watchpoint.app.ui.components.AnswerPill
import com.watchpoint.app.ui.components.InterestIcon
import com.watchpoint.app.ui.components.Mascot
import com.watchpoint.app.ui.components.PrimaryButton
import com.watchpoint.app.ui.components.QuestionSubtitle
import com.watchpoint.app.ui.components.QuestionTitle
import com.watchpoint.app.ui.components.TimeChip
import com.watchpoint.app.ui.components.TimePickerDialog
import com.watchpoint.app.ui.components.WpScreen
import com.watchpoint.app.ui.theme.Cream
import com.watchpoint.app.ui.theme.Mint
import com.watchpoint.app.ui.theme.MintOutline
import com.watchpoint.app.ui.theme.OnMint
import com.watchpoint.app.ui.theme.SelectGreen
import com.watchpoint.app.ui.theme.WpShape
import com.watchpoint.app.ui.theme.WpSpace

/* ------------------------------------------------------------- 11 & 12 ---- */

/** Deck page 11, step 1 of 5. Sun overhead, Wabby waking up. */
@Composable
fun WakeTimeScreen(
    time: TimeOfDay,
    onTimeChange: (TimeOfDay) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    SleepScheduleScreen(
        step = 1,
        skyArt = R.drawable.sun_glow,
        mascot = R.drawable.wabby_crawl,
        question = stringResource(R.string.q_wake),
        time = time,
        onTimeChange = onTimeChange,
        onBack = onBack,
        onNext = onNext
    )
}

/** Deck page 12, step 2 of 5. Moon overhead, Wabby is not enjoying midnight. */
@Composable
fun BedTimeScreen(
    time: TimeOfDay,
    onTimeChange: (TimeOfDay) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    SleepScheduleScreen(
        step = 2,
        skyArt = R.drawable.moon_glow,
        mascot = R.drawable.wabby_cry,
        question = stringResource(R.string.q_bed),
        time = time,
        onTimeChange = onTimeChange,
        onBack = onBack,
        onNext = onNext
    )
}

@Composable
private fun SleepScheduleScreen(
    step: Int,
    skyArt: Int,
    mascot: Int,
    question: String,
    time: TimeOfDay,
    onTimeChange: (TimeOfDay) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    var picking by remember { mutableStateOf(false) }

    WpScreen(
        onBack = onBack,
        step = step,
        totalSteps = 6,
        bottomBar = { PrimaryButton(text = stringResource(R.string.next), onClick = onNext) }
    ) {
        Spacer(Modifier.height(14.dp))
        Image(
            painter = painterResource(skyArt),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(120.dp)
        )
        Spacer(Modifier.height(4.dp))
        Mascot(
            mascot = mascot,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            size = 220.dp
        )
        Spacer(Modifier.height(18.dp))
        QuestionTitle(
            text = question,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        Spacer(Modifier.height(18.dp))
        TimeChip(
            time = time.format(),
            onClick = { picking = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.weight(1f))
    }

    if (picking) {
        TimePickerDialog(
            initial = time,
            onDismiss = { picking = false },
            onConfirm = {
                onTimeChange(it)
                picking = false
            }
        )
    }
}

/* ------------------------------------------------------------------ 13 ---- */

/** Deck page 13, step 3 of 5. A 3x3 grid of practices; pick any number. */
@Composable
fun InterestsScreen(
    selected: Set<Interest>,
    onToggle: (Interest) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    WpScreen(
        onBack = onBack,
        step = 3,
        totalSteps = 6,
        bottomBar = { PrimaryButton(text = stringResource(R.string.next), onClick = onNext) }
    ) {
        Spacer(Modifier.height(24.dp))
        QuestionTitle(text = stringResource(R.string.q_interests))
        QuestionSubtitle(text = stringResource(R.string.q_interests_sub))
        Spacer(Modifier.height(26.dp))

        // Adaptive column count instead of a fixed chunk-of-3, so the grid
        // reflows sensibly on tablets, landscape and narrow phones alike.
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(Interest.entries) { interest ->
                InterestCard(
                    interest = interest,
                    selected = interest in selected,
                    onClick = { onToggle(interest) }
                )
            }
        }
    }
}

@Composable
private fun InterestCard(
    interest: Interest,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .aspectRatio(0.92f)
            .clip(WpShape.Card)
            .background(if (selected) SelectGreen else Mint)
            .border(
                width = if (selected) 2.5.dp else 1.5.dp,
                color = MintOutline.copy(alpha = if (selected) 0.95f else 0.35f),
                shape = WpShape.Card
            )
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        InterestIcon(
            interest = interest,
            tint = OnMint.copy(alpha = 0.75f),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(46.dp)
        )
        Text(
            text = stringResource(interest.label),
            style = MaterialTheme.typography.labelMedium,
            color = OnMint
        )
    }
}

/* ------------------------------------------------------------------ 14 ---- */

/** Deck page 14, step 4 of 5. Wabby and a smaller companion bot. */
@Composable
fun SupportScreen(
    selected: SupportLevel?,
    onSelect: (SupportLevel) -> Unit,
    onBack: () -> Unit
) {
    WpScreen(onBack = onBack, step = 4, totalSteps = 6) {
        Spacer(Modifier.height(26.dp))
        QuestionTitle(text = stringResource(R.string.q_support))
        QuestionSubtitle(text = stringResource(R.string.q_support_sub))

        Spacer(Modifier.height(18.dp))
        BuddyPair(modifier = Modifier.align(Alignment.CenterHorizontally))

        Spacer(Modifier.height(22.dp))
        Column(verticalArrangement = Arrangement.spacedBy(WpSpace.Gap)) {
            SupportLevel.entries.forEach { option ->
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

/** Big Wabby, a small bot, and the little speech bubble between them. */
@Composable
private fun BuddyPair(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.height(190.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Image(
                painter = painterResource(R.drawable.wabby_mini),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .padding(top = 42.dp)
                    .size(96.dp)
            )
            SpeechBubble(
                modifier = Modifier
                    .offset(x = 30.dp, y = 8.dp)
                    .width(72.dp)
            )
        }
        Image(
            painter = painterResource(R.drawable.wabby_buddy),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(170.dp)
        )
    }
}

@Composable
private fun SpeechBubble(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Cream)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_cable),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.height(24.dp)
        )
    }
}

/* ------------------------------------------------------------------ 15 ---- */

/** Deck page 15, step 6 of 6. The last question before the summary. */
@Composable
fun AgeScreen(
    selected: AgeGroup?,
    onSelect: (AgeGroup) -> Unit,
    onBack: () -> Unit
) {
    WpScreen(onBack = onBack, step = 6, totalSteps = 6) {
        Spacer(Modifier.height(30.dp))
        QuestionTitle(text = stringResource(R.string.q_age))

        Spacer(Modifier.height(12.dp))
        Mascot(
            mascot = R.drawable.wabby_wink,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            size = 175.dp
        )

        Spacer(Modifier.height(20.dp))
        Column(verticalArrangement = Arrangement.spacedBy(WpSpace.Gap)) {
            AgeGroup.entries.forEach { option ->
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
