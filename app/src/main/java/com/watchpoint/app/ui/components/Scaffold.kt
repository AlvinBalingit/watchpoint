package com.watchpoint.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.watchpoint.app.R
import com.watchpoint.app.ui.theme.AccentGreen
import com.watchpoint.app.ui.theme.ForestDeep
import com.watchpoint.app.ui.theme.OnForestMuted
import com.watchpoint.app.ui.theme.WpSpace

/**
 * The textured forest background every screen sits on, with a soft vignette
 * so white text stays readable over the lighter patches of the photo.
 */
@Composable
fun ForestBackground(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_forest),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to ForestDeep.copy(alpha = 0.55f),
                        0.35f to ForestDeep.copy(alpha = 0.15f),
                        1f to ForestDeep.copy(alpha = 0.50f)
                    )
                )
        )
    }
}

/**
 * Five dashes marking progress through the personalisation questions
 * (screens 11-15 of the deck). [current] is 1-based; 0 hides the indicator.
 */
@Composable
fun ProgressDashes(
    current: Int,
    total: Int = 5,
    modifier: Modifier = Modifier
) {
    if (current <= 0) return
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(total) { index ->
            Box(
                Modifier
                    .width(18.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (index < current) AccentGreen else OnForestMuted.copy(alpha = 0.45f))
            )
        }
    }
}

/**
 * Standard page frame: background, an optional back arrow and progress
 * indicator on the top row, then the caller's content.
 *
 * [bottomBar] is pinned to the bottom of the screen, which is where the deck
 * always puts the orange primary action.
 */
@Composable
fun WpScreen(
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    step: Int = 0,
    totalSteps: Int = 5,
    contentPadding: androidx.compose.ui.unit.Dp = WpSpace.ScreenH,
    bottomBar: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier.fillMaxSize()) {
        ForestBackground()

        Column(
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 6.dp)
            ) {
                if (onBack != null) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = OnForestMuted,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
                ProgressDashes(
                    current = step,
                    total = totalSteps,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = contentPadding),
                content = content
            )

            if (bottomBar != null) {
                Spacer(Modifier.height(10.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = contentPadding)
                        .padding(bottom = WpSpace.BottomBar)
                ) { bottomBar() }
            }
        }
    }
}
