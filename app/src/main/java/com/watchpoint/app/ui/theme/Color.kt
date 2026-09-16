package com.watchpoint.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Palette sampled directly from the WatchPoint layout deck.
 *
 * The whole product sits on a dark, textured forest green. Orange is reserved
 * for the single primary action on a screen, mint for anything the user picks,
 * cream for the sheet that carries account actions.
 */

// Surfaces
val Forest = Color(0xFF072C07)        // base background under the texture
val ForestDeep = Color(0xFF041B04)    // vignette / scrim
val ForestPanel = Color(0xFF0D3A0C)   // raised panel, "Continue with Google"
val ForestInk = Color(0xFF08210A)     // near-black green, "Continue with Apple"
val Cream = Color(0xFFF1F0EC)         // bottom sheet

// Actions
val Orange = Color(0xFFE1781C)
val OrangePressed = Color(0xFFC1620F)

// Selection
val Mint = Color(0xFF96E882)          // answer pills, interest cards, time chip
val MintPressed = Color(0xFF7FD46B)
val MintOutline = Color(0xFF1D4A17)   // hairline around mint surfaces
val RingGreen = Color(0xFF4EA33F)     // unselected radio ring
val SelectGreen = Color(0xFF5CC94B)   // selected radio fill

// Type
val AccentGreen = Color(0xFF8FD07E)   // highlighted headline line
val OnMint = Color(0xFF0A2308)        // text on mint surfaces
val OnForest = Color(0xFFFFFFFF)
val OnForestMuted = Color(0xFFD9E4D6)
val OnCream = Color(0xFF0A2308)
val CreamMuted = Color(0xFF4A5A47)

// Mascot accent, used for small highlights so the UI echoes Wabby's visor
val Cyan = Color(0xFF54E9E4)
