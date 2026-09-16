package com.watchpoint.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val WatchPointColorScheme = darkColorScheme(
    primary = Orange,
    onPrimary = OnForest,
    secondary = Mint,
    onSecondary = OnMint,
    tertiary = Cyan,
    background = Forest,
    onBackground = OnForest,
    surface = Forest,
    onSurface = OnForest,
    surfaceVariant = ForestPanel,
    onSurfaceVariant = OnForestMuted,
    outline = MintOutline
)

private val WatchPointShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

/** Corner radius of the full-width pill buttons and answer rows. */
object WpShape {
    val Pill = RoundedCornerShape(percent = 50)
    val Card = RoundedCornerShape(14.dp)
    val Sheet = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp)
}

/** Layout rhythm shared by every screen so the flow feels continuous. */
object WpSpace {
    val ScreenH = 26.dp   // horizontal screen gutter
    val TopBar = 8.dp
    val Gap = 14.dp       // between stacked answer rows
    val Section = 28.dp
    val BottomBar = 26.dp // breathing room under the primary action
}

@Composable
fun WatchPointTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Forest.toArgb()
            window.navigationBarColor = Forest.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = WatchPointColorScheme,
        typography = WatchPointTypography,
        shapes = WatchPointShapes,
        content = content
    )
}
