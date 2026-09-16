package com.watchpoint.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

/**
 * Two roles only, as in the deck:
 *  - [DisplayFamily] carries the "WatchPoint" wordmark (Anton: heavy, condensed).
 *  - [TextFamily] carries everything else (Montserrat).
 *
 * Both fall back to the platform sans-serif so the project builds and runs with
 * nothing extra to download. See res/font_README.md to drop in the real faces:
 * add the .ttf files, then swap the two lines below for the REAL FONTS blocks.
 */

// --- Fallback (works out of the box) ---
val DisplayFamily: FontFamily = FontFamily.SansSerif
val TextFamily: FontFamily = FontFamily.SansSerif

/* --- REAL FONTS: uncomment after adding the .ttf files to res/font ---
import androidx.compose.ui.text.font.Font
import com.watchpoint.app.R

val DisplayFamily: FontFamily = FontFamily(
    Font(R.font.anton_regular, FontWeight.Normal)
)

val TextFamily: FontFamily = FontFamily(
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold),
    Font(R.font.montserrat_bold, FontWeight.Bold)
)
*/

/** The wordmark. Very tight tracking, very heavy weight. */
val WordmarkStyle = TextStyle(
    fontFamily = DisplayFamily,
    fontWeight = FontWeight.Black,
    fontSize = 56.sp,
    lineHeight = 58.sp,
    letterSpacing = (-1.5).sp,
    textAlign = TextAlign.Center
)

val WatchPointTypography = Typography(
    // Screen questions: "How have you been lately?"
    headlineMedium = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 25.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.2).sp,
        textAlign = TextAlign.Center
    ),
    // Secondary headline, e.g. the mascot's greeting
    headlineSmall = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 21.sp,
        lineHeight = 29.sp,
        textAlign = TextAlign.Center
    ),
    // Sub-copy under a question
    bodyLarge = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 23.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    // Legal line, attribution
    bodySmall = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 15.sp
    ),
    // Button and answer-pill text
    labelLarge = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        textAlign = TextAlign.Center
    ),
    // Interest card captions
    labelMedium = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 15.sp
    )
)
