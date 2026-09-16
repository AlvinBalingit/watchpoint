package com.watchpoint.app.settings.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.watchpoint.app.R
import com.watchpoint.app.checkin.ReminderScheduler
import com.watchpoint.app.onboarding.TimeOfDay
import com.watchpoint.app.ui.components.QuestionTitle
import com.watchpoint.app.ui.components.TimeChip
import com.watchpoint.app.ui.components.TimePickerDialog
import com.watchpoint.app.ui.components.WpScreen
import com.watchpoint.app.ui.theme.ForestPanel
import com.watchpoint.app.ui.theme.Mint
import com.watchpoint.app.ui.theme.OnForest
import com.watchpoint.app.ui.theme.OnForestMuted
import com.watchpoint.app.ui.theme.OnMint
import com.watchpoint.app.ui.theme.WpShape

/** Reminder preferences: a toggle plus a time, wired to a real (session-scoped) AlarmManager alarm. */
@Composable
fun SettingsScreen(
    reminderEnabled: Boolean,
    motivationalQuotesEnabled: Boolean,
    currentEmail: String?,
    profileFirstName: String,
    profileMiddleInitial: String,
    profileLastName: String,
    profileBirthday: String,
    reminderTime: TimeOfDay,
    onReminderEnabledChange: (Boolean) -> Unit,
    onMotivationalQuotesEnabledChange: (Boolean) -> Unit,
    onReminderTimeChange: (TimeOfDay) -> Unit,
    onOpenTerms: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onUpdateProfile: (String, String, String, String, String, (String?) -> Unit) -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var picking by remember { mutableStateOf(false) }
    var pendingNotificationAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var profileEmail by remember(currentEmail) { mutableStateOf(currentEmail.orEmpty()) }
    var profileFirstNameValue by remember(profileFirstName) { mutableStateOf(profileFirstName) }
    var profileMiddleInitialValue by remember(profileMiddleInitial) { mutableStateOf(profileMiddleInitial) }
    var profileLastNameValue by remember(profileLastName) { mutableStateOf(profileLastName) }
    var profileBirthdayValue by remember(profileBirthday) { mutableStateOf(profileBirthday) }
    var profileError by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            pendingNotificationAction?.invoke()
            pendingNotificationAction = null
        }
    }

    fun requestNotifications(action: () -> Unit) {
        val needsPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        if (needsPermission) {
            pendingNotificationAction = action
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            action()
        }
    }

    WpScreen(onBack = onBack) {
        Spacer(Modifier.height(20.dp))
        QuestionTitle(text = stringResource(R.string.settings_title))

        Spacer(Modifier.height(16.dp))
        LegalSettingsRow(
            label = stringResource(R.string.settings_edit_profile),
            onClick = { profileError = null; showProfileDialog = true }
        )

        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(WpShape.Card)
                .background(ForestPanel.copy(alpha = 0.5f))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.reminder_toggle),
                    style = MaterialTheme.typography.labelLarge,
                    color = OnForest
                )
                Text(
                    text = stringResource(R.string.reminder_toggle_sub),
                    style = MaterialTheme.typography.bodySmall,
                    color = OnForestMuted
                )
            }
            Switch(
                checked = reminderEnabled,
                onCheckedChange = { enabled ->
                    if (enabled) {
                        requestNotifications {
                            ReminderScheduler.schedule(context, reminderTime)
                            onReminderEnabledChange(true)
                        }
                    } else {
                        ReminderScheduler.cancel(context)
                        onReminderEnabledChange(false)
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = OnMint,
                    checkedTrackColor = Mint,
                    uncheckedThumbColor = OnForestMuted,
                    uncheckedTrackColor = ForestPanel
                )
            )
        }

        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(WpShape.Card)
                .background(ForestPanel.copy(alpha = 0.5f))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.motivational_quotes_toggle),
                    style = MaterialTheme.typography.labelLarge,
                    color = OnForest
                )
                Text(
                    text = stringResource(R.string.motivational_quotes_toggle_sub),
                    style = MaterialTheme.typography.bodySmall,
                    color = OnForestMuted
                )
            }
            Switch(
                checked = motivationalQuotesEnabled,
                onCheckedChange = { enabled ->
                    if (enabled) {
                        requestNotifications {
                            ReminderScheduler.scheduleMotivationalQuotes(context)
                            onMotivationalQuotesEnabledChange(true)
                        }
                    } else {
                        ReminderScheduler.cancelMotivationalQuotes(context)
                        onMotivationalQuotesEnabledChange(false)
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = OnMint,
                    checkedTrackColor = Mint,
                    uncheckedThumbColor = OnForestMuted,
                    uncheckedTrackColor = ForestPanel
                )
            )
        }

        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.reminder_time_label),
                style = MaterialTheme.typography.bodyLarge,
                color = OnForest,
                modifier = Modifier.weight(1f)
            )
            TimeChip(
                time = reminderTime.format(),
                onClick = { if (reminderEnabled) picking = true }
            )
        }

        Spacer(Modifier.height(28.dp))
        Text(
            text = stringResource(R.string.settings_legal_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = OnForestMuted
        )
        Spacer(Modifier.height(10.dp))
        LegalSettingsRow(
            label = stringResource(R.string.settings_terms),
            onClick = onOpenTerms
        )
        Spacer(Modifier.height(8.dp))
        LegalSettingsRow(
            label = stringResource(R.string.settings_privacy),
            onClick = onOpenPrivacy
        )
        Spacer(Modifier.height(8.dp))
        LegalSettingsRow(
            label = stringResource(R.string.settings_logout),
            onClick = { showLogoutDialog = true }
        )
        Spacer(Modifier.weight(1f))
    }

    if (picking) {
        TimePickerDialog(
            initial = reminderTime,
            onDismiss = { picking = false },
            onConfirm = {
                onReminderTimeChange(it)
                if (reminderEnabled) ReminderScheduler.schedule(context, it)
                picking = false
            }
        )
    }

    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = { Text(stringResource(R.string.settings_edit_profile)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = profileEmail,
                        onValueChange = { profileEmail = it; profileError = null },
                        label = { Text(stringResource(R.string.profile_email_label)) },
                        singleLine = true,
                        isError = profileError != null
                    )
                    OutlinedTextField(
                        value = profileFirstNameValue,
                        onValueChange = { profileFirstNameValue = it },
                        label = { Text(stringResource(R.string.first_name_label)) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = profileMiddleInitialValue,
                        onValueChange = { profileMiddleInitialValue = it.take(1) },
                        label = { Text(stringResource(R.string.middle_initial_label)) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = profileLastNameValue,
                        onValueChange = { profileLastNameValue = it },
                        label = { Text(stringResource(R.string.last_name_label)) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = profileBirthdayValue,
                        onValueChange = { profileBirthdayValue = it },
                        label = { Text(stringResource(R.string.birthday_label)) },
                        placeholder = { Text(stringResource(R.string.birthday_placeholder)) },
                        singleLine = true
                    )
                    if (profileError != null) {
                        Text(profileError.orEmpty(), color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onUpdateProfile(
                        profileEmail,
                        profileFirstNameValue,
                        profileMiddleInitialValue,
                        profileLastNameValue,
                        profileBirthdayValue
                    ) { error ->
                        profileError = error
                        if (error == null) showProfileDialog = false
                    }
                }) { Text(stringResource(R.string.profile_save)) }
            },
            dismissButton = {
                TextButton(onClick = { showProfileDialog = false }) {
                    Text(stringResource(R.string.profile_cancel))
                }
            }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(R.string.logout_title)) },
            text = { Text(stringResource(R.string.logout_body)) },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false; onLogout() }) {
                    Text(stringResource(R.string.logout_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(R.string.profile_cancel))
                }
            }
        )
    }
}

@Composable
private fun LegalSettingsRow(label: String, onClick: () -> Unit) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodyLarge,
        color = Mint,
        modifier = Modifier
            .fillMaxWidth()
            .clip(WpShape.Card)
            .background(ForestPanel.copy(alpha = 0.5f))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    )
}
