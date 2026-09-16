package com.watchpoint.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.watchpoint.app.R
import com.watchpoint.app.onboarding.TimeOfDay
import com.watchpoint.app.ui.theme.Cream
import com.watchpoint.app.ui.theme.OnMint

/** Shared time-of-day picker dialog, used by onboarding's sleep schedule and Settings' reminder time. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initial: TimeOfDay,
    onDismiss: () -> Unit,
    onConfirm: (TimeOfDay) -> Unit
) {
    val state = rememberTimePickerState(
        initialHour = initial.hour,
        initialMinute = initial.minute,
        is24Hour = false
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Cream,
        titleContentColor = OnMint,
        textContentColor = OnMint,
        title = {
            Text(
                text = stringResource(R.string.time_pick_title),
                style = MaterialTheme.typography.headlineSmall,
                color = OnMint
            )
        },
        text = {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TimeInput(state = state)
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(TimeOfDay(state.hour, state.minute)) }) {
                Text(stringResource(R.string.ok), color = OnMint)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = OnMint)
            }
        }
    )
}
