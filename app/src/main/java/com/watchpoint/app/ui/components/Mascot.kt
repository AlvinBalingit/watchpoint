package com.watchpoint.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.watchpoint.app.R
import com.watchpoint.app.ui.theme.OnForest
import com.watchpoint.app.ui.theme.OnForestMuted
import com.watchpoint.app.ui.theme.WordmarkStyle
import kotlinx.coroutines.launch

/**
 * Wabby standing on the HUD ring used on the hero screens.
 *
 * The ring turns very slowly; Wabby himself bounces, wobbles and gives an
 * occasional little "breath" pulse so he reads as alive rather than a static
 * sticker. Pass [onTap] to make him react to a tap with an extra bounce.
 */
@Composable
fun MascotWithRing(
    @DrawableRes mascot: Int,
    modifier: Modifier = Modifier,
    ringSize: Dp = 300.dp,
    mascotSize: Dp = 210.dp,
    showRing: Boolean = true,
    onTap: (() -> Unit)? = null
) {
    val transition = rememberInfiniteTransition(label = "mascot")

    val spin by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(46_000), RepeatMode.Restart),
        label = "spin"
    )

    Box(modifier, contentAlignment = Alignment.Center) {
        if (showRing) {
            Image(
                painter = painterResource(R.drawable.hud_ring),
                contentDescription = null,
                modifier = Modifier
                    .size(ringSize)
                    .alpha(0.75f)
                    .rotate(spin)
            )
        }
        WabbyMotion(mascot = mascot, size = mascotSize, onTap = onTap)
    }
}

/** Wabby on his own, no ring behind him, with the same idle motion. */
@Composable
fun Mascot(
    @DrawableRes mascot: Int,
    modifier: Modifier = Modifier,
    size: Dp = 210.dp,
    onTap: (() -> Unit)? = null
) {
    WabbyMotion(mascot = mascot, size = size, modifier = modifier, onTap = onTap)
}

/**
 * Shared idle animation for Wabby: a gentle bounce, a slower wobble on an
 * offset period so it never looks mechanically synced, and every few seconds
 * a quick squash-stretch "breath" pulse. An optional [onTap] adds a one-shot
 * bounce-and-kick on top of the idle loop, so tapping Wabby feels responsive.
 */
@Composable
private fun WabbyMotion(
    @DrawableRes mascot: Int,
    size: Dp,
    modifier: Modifier = Modifier,
    onTap: (() -> Unit)? = null
) {
    val transition = rememberInfiniteTransition(label = "wabbyIdle")

    val bounce by transition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(tween(1400, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "bounce"
    )

    val wobble by transition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(tween(1900, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "wobble"
    )

    val breath by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = 3200
                1f at 0
                1f at 2600
                1.05f at 2900
                1f at 3200
            },
            RepeatMode.Restart
        ),
        label = "breath"
    )

    val tapScale = remember { Animatable(1f) }
    val tapRotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val interaction = remember { MutableInteractionSource() }

    Image(
        painter = painterResource(mascot),
        contentDescription = stringResource(R.string.cd_mascot),
        contentScale = ContentScale.Fit,
        modifier = modifier
            .size(size)
            .offset(y = bounce.dp)
            .rotate(wobble + tapRotation.value)
            .scale(breath * tapScale.value)
            .then(
                if (onTap != null) {
                    Modifier.clickable(
                        interactionSource = interaction,
                        indication = null,
                        onClick = {
                            scope.launch {
                                launch {
                                    tapScale.snapTo(1.18f)
                                    tapScale.animateTo(1f, spring(dampingRatio = 0.35f, stiffness = 260f))
                                }
                                launch {
                                    tapRotation.snapTo(-12f)
                                    tapRotation.animateTo(12f, spring(dampingRatio = 0.25f, stiffness = 200f))
                                    tapRotation.animateTo(0f, spring(dampingRatio = 0.6f, stiffness = 200f))
                                }
                            }
                            onTap()
                        }
                    )
                } else {
                    Modifier
                }
            )
    )
}

/** The "WatchPoint" wordmark with its tagline underneath. */
@Composable
fun Wordmark(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = WordmarkStyle.fontSize
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.brand),
            style = WordmarkStyle.copy(fontSize = fontSize),
            color = OnForest
        )
        Text(
            text = stringResource(R.string.tagline),
            style = MaterialTheme.typography.headlineSmall,
            color = OnForest,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

/** The centred white question that heads most screens. */
@Composable
fun QuestionTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = OnForest
) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        color = color,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth()
    )
}

/** Smaller supporting line under a [QuestionTitle]. */
@Composable
fun QuestionSubtitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = OnForestMuted,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
    )
}
