package com.watchpoint.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import com.watchpoint.app.onboarding.Interest
import com.watchpoint.app.ui.theme.OnMint

/**
 * The nine practice icons from the interests grid, drawn as strokes rather than
 * shipped as bitmaps: they stay crisp at any density and pick up the tint of
 * whatever card they sit on.
 *
 * Everything is authored on a 100x100 grid and scaled to the drawing area, so
 * the shapes below read as plain coordinates.
 */
@Composable
fun InterestIcon(
    interest: Interest,
    modifier: Modifier = Modifier,
    tint: Color = OnMint
) {
    Canvas(modifier) {
        val side = size.minDimension
        val u = side / 100f
        val stroke = Stroke(
            width = side * 0.055f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
        translate(
            left = (size.width - side) / 2f,
            top = (size.height - side) / 2f
        ) {
            when (interest) {
                Interest.Breathing -> drawCloud(u, tint, stroke)
                Interest.Journaling -> drawSmiley(u, tint, stroke)
                Interest.Meditation -> drawPencil(u, tint, stroke)
                Interest.MoodTracking -> drawHeart(u, tint, stroke)
                Interest.SelfLove -> drawSparkle(u, tint, stroke)
                Interest.Gratitude -> drawDroplet(u, tint, stroke)
                Interest.Physical -> drawFigure(u, tint, stroke)
                Interest.Sleep -> drawMoon(u, tint, stroke)
                Interest.Productivity -> drawSun(u, tint, stroke)
                Interest.TimeManagement -> drawClock(u, tint, stroke)
                Interest.SocialConnection -> drawSpeechBubbles(u, tint, stroke)
                Interest.StudyFocus -> drawBook(u, tint, stroke)
                Interest.EmotionalAwareness -> drawFace(u, tint, stroke)
                Interest.Relaxation -> drawWave(u, tint, stroke)
                Interest.GoalSetting -> drawTarget(u, tint, stroke)
                Interest.BoardExamPrep -> drawChecklist(u, tint, stroke)
            }
        }
    }
}

/** Builds a path from coordinates on the shared 100x100 grid. */
private fun path(u: Float, block: PathBuilder.() -> Unit): Path {
    val builder = PathBuilder(Path(), u)
    builder.block()
    return builder.path
}

private class PathBuilder(val path: Path, val u: Float) {
    fun moveTo(x: Float, y: Float) = path.moveTo(x * u, y * u)
    fun lineTo(x: Float, y: Float) = path.lineTo(x * u, y * u)
    fun curveTo(
        x1: Float, y1: Float,
        x2: Float, y2: Float,
        x3: Float, y3: Float
    ) = path.cubicTo(x1 * u, y1 * u, x2 * u, y2 * u, x3 * u, y3 * u)

    fun close() = path.close()
}

private fun DrawScope.drawCloud(u: Float, tint: Color, stroke: Stroke) {
    val p = path(u) {
        moveTo(26f, 70f)
        curveTo(13f, 70f, 11f, 52f, 24f, 49f)
        curveTo(25f, 32f, 48f, 27f, 55f, 41f)
        curveTo(66f, 32f, 84f, 42f, 81f, 56f)
        curveTo(93f, 58f, 92f, 70f, 79f, 70f)
        close()
    }
    drawPath(p, tint, style = stroke)
}

private fun DrawScope.drawSmiley(u: Float, tint: Color, stroke: Stroke) {
    drawCircle(tint, radius = 32f * u, center = Offset(50f * u, 50f * u), style = stroke)
    drawCircle(tint, radius = 4f * u, center = Offset(39f * u, 43f * u))
    drawCircle(tint, radius = 4f * u, center = Offset(61f * u, 43f * u))
    val mouth = path(u) {
        moveTo(36f, 59f)
        curveTo(42f, 70f, 58f, 70f, 64f, 59f)
    }
    drawPath(mouth, tint, style = stroke)
}

private fun DrawScope.drawPencil(u: Float, tint: Color, stroke: Stroke) {
    val body = path(u) {
        moveTo(21f, 79f)
        lineTo(27f, 61f)
        lineTo(64f, 24f)
        lineTo(77f, 37f)
        lineTo(40f, 74f)
        close()
    }
    drawPath(body, tint, style = stroke)
    val ferrule = path(u) {
        moveTo(27f, 61f)
        lineTo(40f, 74f)
    }
    drawPath(ferrule, tint, style = stroke)
}

private fun DrawScope.drawHeart(u: Float, tint: Color, stroke: Stroke) {
    val p = path(u) {
        moveTo(50f, 79f)
        curveTo(19f, 58f, 17f, 35f, 34f, 29f)
        curveTo(44f, 25f, 50f, 33f, 50f, 40f)
        curveTo(50f, 33f, 56f, 25f, 66f, 29f)
        curveTo(83f, 35f, 81f, 58f, 50f, 79f)
        close()
    }
    drawPath(p, tint, style = stroke)
}

private fun DrawScope.drawSparkle(u: Float, tint: Color, stroke: Stroke) {
    val big = path(u) {
        moveTo(46f, 20f)
        curveTo(50f, 44f, 54f, 48f, 78f, 52f)
        curveTo(54f, 56f, 50f, 60f, 46f, 84f)
        curveTo(42f, 60f, 38f, 56f, 14f, 52f)
        curveTo(38f, 48f, 42f, 44f, 46f, 20f)
        close()
    }
    drawPath(big, tint, style = stroke)
    val small = path(u) {
        moveTo(78f, 14f)
        curveTo(80f, 24f, 81f, 25f, 91f, 27f)
        curveTo(81f, 29f, 80f, 30f, 78f, 40f)
        curveTo(76f, 30f, 75f, 29f, 65f, 27f)
        curveTo(75f, 25f, 76f, 24f, 78f, 14f)
        close()
    }
    drawPath(small, tint, style = stroke)
}

private fun DrawScope.drawDroplet(u: Float, tint: Color, stroke: Stroke) {
    val p = path(u) {
        moveTo(50f, 15f)
        curveTo(64f, 33f, 78f, 48f, 78f, 61f)
        curveTo(78f, 76f, 65f, 86f, 50f, 86f)
        curveTo(35f, 86f, 22f, 76f, 22f, 61f)
        curveTo(22f, 48f, 36f, 33f, 50f, 15f)
        close()
    }
    drawPath(p, tint, style = stroke)
}

private fun DrawScope.drawFigure(u: Float, tint: Color, stroke: Stroke) {
    drawCircle(tint, radius = 8f * u, center = Offset(50f * u, 27f * u), style = stroke)
    val limbs = path(u) {
        moveTo(50f, 36f)
        lineTo(50f, 58f)
        moveTo(29f, 33f)
        lineTo(50f, 45f)
        lineTo(71f, 33f)
        moveTo(50f, 58f)
        lineTo(35f, 82f)
        moveTo(50f, 58f)
        lineTo(65f, 82f)
    }
    drawPath(limbs, tint, style = stroke)
    // motion marks either side of the head
    val marks = path(u) {
        moveTo(24f, 16f)
        lineTo(29f, 21f)
        moveTo(76f, 16f)
        lineTo(71f, 21f)
        moveTo(50f, 10f)
        lineTo(50f, 15f)
    }
    drawPath(marks, tint, style = stroke)
}

private fun DrawScope.drawMoon(u: Float, tint: Color, stroke: Stroke) {
    val p = path(u) {
        moveTo(71f, 19f)
        curveTo(47f, 24f, 31f, 43f, 34f, 63f)
        curveTo(37f, 81f, 55f, 91f, 73f, 85f)
        curveTo(51f, 71f, 45f, 43f, 71f, 19f)
        close()
    }
    drawPath(p, tint, style = stroke)
}

private fun DrawScope.drawSun(u: Float, tint: Color, stroke: Stroke) {
    drawCircle(tint, radius = 20f * u, center = Offset(50f * u, 50f * u), style = stroke)
    val rays = Path()
    val cx = 50f * u
    val cy = 50f * u
    val inner = 29f * u
    val outer = 40f * u
    repeat(8) { i ->
        val angle = Math.toRadians(i * 45.0)
        val dx = kotlin.math.cos(angle).toFloat()
        val dy = kotlin.math.sin(angle).toFloat()
        rays.moveTo(cx + dx * inner, cy + dy * inner)
        rays.lineTo(cx + dx * outer, cy + dy * outer)
    }
    drawPath(rays, tint, style = stroke)
}

private fun DrawScope.drawClock(u: Float, tint: Color, stroke: Stroke) {
    drawCircle(tint, radius = 34f * u, center = Offset(50f * u, 50f * u), style = stroke)
    val hands = path(u) {
        moveTo(50f, 50f)
        lineTo(50f, 28f)
        moveTo(50f, 50f)
        lineTo(68f, 58f)
    }
    drawPath(hands, tint, style = stroke)
}

private fun DrawScope.drawSpeechBubbles(u: Float, tint: Color, stroke: Stroke) {
    val big = path(u) {
        moveTo(18f, 26f)
        lineTo(66f, 26f)
        curveTo(70f, 26f, 72f, 28f, 72f, 32f)
        lineTo(72f, 50f)
        curveTo(72f, 54f, 70f, 56f, 66f, 56f)
        lineTo(34f, 56f)
        lineTo(24f, 68f)
        lineTo(26f, 56f)
        lineTo(18f, 56f)
        curveTo(14f, 56f, 12f, 54f, 12f, 50f)
        lineTo(12f, 32f)
        curveTo(12f, 28f, 14f, 26f, 18f, 26f)
        close()
    }
    drawPath(big, tint, style = stroke)
    val small = path(u) {
        moveTo(58f, 62f)
        lineTo(84f, 62f)
        curveTo(87f, 62f, 89f, 64f, 89f, 67f)
        lineTo(89f, 76f)
        curveTo(89f, 79f, 87f, 81f, 84f, 81f)
        lineTo(76f, 81f)
        lineTo(78f, 89f)
        lineTo(68f, 81f)
        lineTo(58f, 81f)
        curveTo(55f, 81f, 53f, 79f, 53f, 76f)
        lineTo(53f, 67f)
        curveTo(53f, 64f, 55f, 62f, 58f, 62f)
        close()
    }
    drawPath(small, tint, style = stroke)
}

private fun DrawScope.drawBook(u: Float, tint: Color, stroke: Stroke) {
    val cover = path(u) {
        moveTo(50f, 26f)
        curveTo(42f, 20f, 24f, 18f, 14f, 22f)
        lineTo(14f, 76f)
        curveTo(24f, 72f, 42f, 74f, 50f, 80f)
        curveTo(58f, 74f, 76f, 72f, 86f, 76f)
        lineTo(86f, 22f)
        curveTo(76f, 18f, 58f, 20f, 50f, 26f)
        close()
    }
    drawPath(cover, tint, style = stroke)
    val spine = path(u) {
        moveTo(50f, 26f)
        lineTo(50f, 80f)
    }
    drawPath(spine, tint, style = stroke)
}

private fun DrawScope.drawFace(u: Float, tint: Color, stroke: Stroke) {
    drawCircle(tint, radius = 32f * u, center = Offset(50f * u, 50f * u), style = stroke)
    drawCircle(tint, radius = 3.5f * u, center = Offset(39f * u, 45f * u))
    drawCircle(tint, radius = 3.5f * u, center = Offset(61f * u, 45f * u))
    val brows = path(u) {
        moveTo(32f, 34f)
        curveTo(35f, 30f, 42f, 30f, 45f, 34f)
        moveTo(55f, 34f)
        curveTo(58f, 30f, 65f, 30f, 68f, 34f)
    }
    drawPath(brows, tint, style = stroke)
    val mouth = path(u) {
        moveTo(38f, 63f)
        curveTo(44f, 58f, 56f, 58f, 62f, 63f)
    }
    drawPath(mouth, tint, style = stroke)
}

private fun DrawScope.drawWave(u: Float, tint: Color, stroke: Stroke) {
    val top = path(u) {
        moveTo(12f, 38f)
        curveTo(22f, 26f, 32f, 26f, 42f, 38f)
        curveTo(52f, 50f, 62f, 50f, 72f, 38f)
        curveTo(78f, 31f, 84f, 29f, 88f, 32f)
    }
    drawPath(top, tint, style = stroke)
    val bottom = path(u) {
        moveTo(12f, 62f)
        curveTo(22f, 50f, 32f, 50f, 42f, 62f)
        curveTo(52f, 74f, 62f, 74f, 72f, 62f)
        curveTo(78f, 55f, 84f, 53f, 88f, 56f)
    }
    drawPath(bottom, tint, style = stroke)
}

private fun DrawScope.drawTarget(u: Float, tint: Color, stroke: Stroke) {
    drawCircle(tint, radius = 34f * u, center = Offset(50f * u, 50f * u), style = stroke)
    drawCircle(tint, radius = 20f * u, center = Offset(50f * u, 50f * u), style = stroke)
    drawCircle(tint, radius = 5f * u, center = Offset(50f * u, 50f * u))
}

/** An exam paper with checkmarks - the Board Exam Prep interest icon. */
private fun DrawScope.drawChecklist(u: Float, tint: Color, stroke: Stroke) {
    val page = path(u) {
        moveTo(24f, 14f)
        lineTo(66f, 14f)
        lineTo(80f, 28f)
        lineTo(80f, 86f)
        lineTo(24f, 86f)
        close()
    }
    drawPath(page, tint, style = stroke)
    val fold = path(u) {
        moveTo(66f, 14f)
        lineTo(66f, 28f)
        lineTo(80f, 28f)
    }
    drawPath(fold, tint, style = stroke)

    val checks = path(u) {
        moveTo(33f, 42f)
        lineTo(38f, 47f)
        lineTo(48f, 37f)
        moveTo(33f, 62f)
        lineTo(38f, 67f)
        lineTo(48f, 57f)
    }
    drawPath(checks, tint, style = stroke)

    val lines = path(u) {
        moveTo(55f, 44f)
        lineTo(71f, 44f)
        moveTo(55f, 64f)
        lineTo(71f, 64f)
    }
    drawPath(lines, tint, style = stroke)
}
