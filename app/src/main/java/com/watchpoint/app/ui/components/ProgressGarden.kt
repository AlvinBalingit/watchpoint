package com.watchpoint.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.watchpoint.app.R
import com.watchpoint.app.ui.theme.AccentGreen
import com.watchpoint.app.ui.theme.Mint
import com.watchpoint.app.ui.theme.OnForestMuted
import com.watchpoint.app.ui.theme.Orange

/**
 * WatchPoint's own progress visual - a simple stem that grows taller and
 * sprouts more leaves as [progress] (0-100) climbs, built from stroke paths
 * on a 100x100 grid like the interest icons. Not a copy of any reference
 * app's illustration; the shape and growth logic are original to this app.
 */
@Composable
fun ProgressGarden(
    progress: Int,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 120.dp
) {
    val clamped = progress.coerceIn(0, 100)

    // Animate toward the new progress value whenever it changes, so the stem
    // visibly grows and leaves sprout in rather than snapping instantly.
    val animatedProgress by animateFloatAsState(
        targetValue = clamped.toFloat(),
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 60f),
        label = "gardenProgress"
    )
    val animatedLabel by animateIntAsState(
        targetValue = clamped,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 60f),
        label = "gardenProgressLabel"
    )
    val leafCount = (animatedProgress / 20).toInt().coerceIn(0, 5) // one leaf per 20% of progress
    val stemHeightFraction = (0.25f + (animatedProgress / 100f) * 0.6f) // stem grows from short to tall

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val groundY = h * 0.92f
            val topY = groundY - (h * stemHeightFraction)
            val stemX = w * 0.5f

            val stroke = Stroke(width = w * 0.035f, cap = StrokeCap.Round)

            // ground line
            drawLine(
                color = OnForestMuted.copy(alpha = 0.3f),
                start = Offset(w * 0.1f, groundY),
                end = Offset(w * 0.9f, groundY),
                strokeWidth = w * 0.02f
            )

            // stem
            drawLine(
                color = Mint,
                start = Offset(stemX, groundY),
                end = Offset(stemX, topY),
                strokeWidth = stroke.width,
                cap = StrokeCap.Round
            )

            // leaves, alternating sides, spaced evenly along the stem
            repeat(leafCount) { index ->
                val t = (index + 1f) / 6f // keep leaves below the very top
                val leafY = groundY - (groundY - topY) * t
                val leftSide = index % 2 == 0
                val direction = if (leftSide) -1f else 1f
                val leaf = Path().apply {
                    moveTo(stemX, leafY)
                    cubicTo(
                        stemX + direction * w * 0.22f, leafY - h * 0.05f,
                        stemX + direction * w * 0.22f, leafY + h * 0.08f,
                        stemX, leafY + h * 0.02f
                    )
                }
                drawPath(leaf, color = AccentGreen, style = stroke)
            }

            // a small bloom at the top once progress is high enough
            if (animatedProgress >= 80) {
                drawCircle(color = Orange, radius = w * 0.06f, center = Offset(stemX, topY))
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.garden_progress_label, animatedLabel),
            style = MaterialTheme.typography.bodySmall,
            color = OnForestMuted,
            textAlign = TextAlign.Center
        )
    }
}
