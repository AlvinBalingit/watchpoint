package com.watchpoint.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.watchpoint.app.ui.theme.OnForest
import com.watchpoint.app.ui.theme.OnForestMuted
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

/** Where a 1-5 score falls, used to label the y-axis in plain language instead of bare numbers. */
private val SCALE_LABELS = listOf("1", "2", "3", "4", "5")

/**
 * A line chart drawn with Canvas - no charting dependency. Shows a y-axis
 * scale, one x-axis date label per point (thinned out if there isn't room),
 * and lets the viewer tap any point to see its exact date and value.
 */
@Composable
fun LineChart(
    title: String,
    points: List<Pair<LocalDate, Float>>,
    color: Color,
    modifier: Modifier = Modifier,
    minValue: Float = 1f,
    maxValue: Float = 5f,
    valueLabel: (Float) -> String = { "%.0f".format(it) },
    height: androidx.compose.ui.unit.Dp = 150.dp
) {
    var selected by remember { mutableIntStateOf(-1) }

    Column(modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(
                Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = OnForest
            )
        }
        Spacer(Modifier.height(6.dp))

        if (selected in points.indices) {
            val (date, value) = points[selected]
            Text(
                text = "${date.format(DateTimeFormatter.ofPattern("MMM d"))} · ${valueLabel(value)}",
                style = MaterialTheme.typography.bodySmall,
                color = color,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        Row(Modifier.fillMaxWidth()) {
            // Y-axis scale, low to high, aligned with the plot area below.
            Column(
                modifier = Modifier
                    .height(height)
                    .width(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                for (i in SCALE_LABELS.indices.reversed()) {
                    Text(
                        text = SCALE_LABELS[i],
                        style = MaterialTheme.typography.labelSmall,
                        color = OnForestMuted
                    )
                }
            }
            Spacer(Modifier.width(6.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .pointerInput(points) {
                        detectTapAndFindNearest(points.size) { index ->
                            selected = if (selected == index) -1 else index
                        }
                    }
            ) {
                if (points.isEmpty()) return@Canvas

                val range = (maxValue - minValue).coerceAtLeast(0.01f)
                val stepX = if (points.size > 1) size.width / (points.size - 1) else 0f

                fun yFor(value: Float): Float {
                    val t = (value - minValue) / range
                    return size.height - (t * size.height)
                }

                // One guide line per scale step so the plotted line can be read against a value.
                val steps = SCALE_LABELS.size
                for (i in 0 until steps) {
                    val y = size.height - (i.toFloat() / (steps - 1)) * size.height
                    drawLine(
                        color = OnForestMuted.copy(alpha = 0.15f),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                if (points.size == 1) {
                    drawCircle(color = color, radius = 5.dp.toPx(), center = Offset(0f, yFor(points[0].second)))
                    return@Canvas
                }

                val plotted = points.mapIndexed { index, (_, value) -> Offset(index * stepX, yFor(value)) }
                for (i in 0 until plotted.size - 1) {
                    drawLine(
                        color = color,
                        start = plotted[i],
                        end = plotted[i + 1],
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
                plotted.forEachIndexed { index, point ->
                    val isSelected = index == selected
                    drawCircle(
                        color = color,
                        radius = (if (isSelected) 7f else 4f).dp.toPx(),
                        center = point
                    )
                    if (isSelected) {
                        drawCircle(
                            color = OnForest,
                            radius = 2.5f.dp.toPx(),
                            center = point
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))
        // X-axis date labels, thinned so they don't collide on a 14-day window.
        val labelEvery = if (points.size > 8) 2 else 1
        Row(Modifier.fillMaxWidth().padding(start = 22.dp)) {
            points.forEachIndexed { index, (date, _) ->
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    if (index % labelEvery == 0) {
                        Text(
                            text = date.format(DateTimeFormatter.ofPattern("d")),
                            style = MaterialTheme.typography.labelSmall,
                            color = OnForestMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

private suspend fun PointerInputScope.detectTapAndFindNearest(
    pointCount: Int,
    onTap: (Int) -> Unit
) {
    if (pointCount == 0) return
    detectTapGestures { offset ->
        val stepX = if (pointCount > 1) size.width.toFloat() / (pointCount - 1) else 0f
        val index = if (stepX > 0f) (offset.x / stepX).roundToInt().coerceIn(0, pointCount - 1) else 0
        onTap(index)
    }
}

/** A short, plain-language read on where a series stands and which way it's moving. */
fun trendSummary(
    points: List<Pair<LocalDate, Float>>,
    label: String,
    unit: String = ""
): String {
    if (points.isEmpty()) return "$label: no data yet."
    val values = points.map { it.second }
    val average = values.average()
    if (values.size < 4) {
        return "$label: averaging %.1f$unit.".format(average)
    }
    val mid = values.size / 2
    val firstHalfAvg = values.take(mid).average()
    val secondHalfAvg = values.drop(mid).average()
    val delta = secondHalfAvg - firstHalfAvg
    val direction = when {
        delta > 0.3 -> "trending up"
        delta < -0.3 -> "trending down"
        else -> "holding steady"
    }
    return "$label: averaging %.1f$unit, $direction.".format(average)
}

/** Two evenly-spaced segments toggling between a short and a long window (7-day / 14-day). */
@Composable
fun WindowToggle(
    shortLabel: String,
    longLabel: String,
    isShortSelected: Boolean,
    onSelectShort: () -> Unit,
    onSelectLong: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SegmentPill(text = shortLabel, selected = isShortSelected, onClick = onSelectShort, modifier = Modifier.weight(1f))
        SegmentPill(text = longLabel, selected = !isShortSelected, onClick = onSelectLong, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun SegmentPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnswerPill(text = text, selected = selected, onClick = onClick, modifier = modifier)
}
