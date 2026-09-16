package com.watchpoint.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.watchpoint.app.navigation.WatchPointNavHost
import com.watchpoint.app.ui.theme.Forest
import com.watchpoint.app.ui.theme.WatchPointTheme

/**
 * WatchPoint is a single-activity Compose app. Every screen in the layout deck
 * is a composable destination inside [WatchPointNavHost].
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            WatchPointTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Forest
                ) {
                    WatchPointNavHost()
                }
            }
        }
    }
}
