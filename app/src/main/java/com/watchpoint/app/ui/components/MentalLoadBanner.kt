package com.watchpoint.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.watchpoint.app.R
import com.watchpoint.app.ui.theme.Orange
import com.watchpoint.app.ui.theme.OrangePressed
import com.watchpoint.app.ui.theme.OnForest
import com.watchpoint.app.ui.theme.WpShape

/**
 * 3.A Mental Load Indicator - a plain reflective banner, not a modal or an
 * alarm. Only shown when [CheckInViewModel.mentalLoadWarning] is true.
 */
@Composable
fun MentalLoadBanner(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.mental_load_message),
        style = MaterialTheme.typography.bodyMedium,
        color = OnForest,
        modifier = modifier
            .fillMaxWidth()
            .clip(WpShape.Card)
            .background(OrangePressed.copy(alpha = 0.35f))
            .border(1.dp, Orange.copy(alpha = 0.6f), WpShape.Card)
            .padding(14.dp)
    )
}
