package com.watchpoint.app.checkin.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.watchpoint.app.R
import com.watchpoint.app.data.db.JournalEntryEntity
import com.watchpoint.app.ui.components.PrimaryButton
import com.watchpoint.app.ui.components.QuestionSubtitle
import com.watchpoint.app.ui.components.QuestionTitle
import com.watchpoint.app.ui.components.WpScreen
import com.watchpoint.app.ui.theme.ForestPanel
import com.watchpoint.app.ui.theme.Mint
import com.watchpoint.app.ui.theme.MintOutline
import com.watchpoint.app.ui.theme.OnForest
import com.watchpoint.app.ui.theme.OnForestMuted
import com.watchpoint.app.ui.theme.Orange
import com.watchpoint.app.ui.theme.WpShape
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/** A standalone journal: free-form dated entries, separate from the daily check-in reflection. */
@Composable
fun JournalScreen(
    entries: List<JournalEntryEntity>,
    draftTitle: String,
    draftText: String,
    onDraftTitleChange: (String) -> Unit,
    onDraftChange: (String) -> Unit,
    onSave: () -> Unit,
    editingEntryId: Long?,
    onEdit: (JournalEntryEntity) -> Unit,
    onCancelEdit: () -> Unit,
    onDelete: (Long) -> Unit,
    onBack: () -> Unit
) {
    WpScreen(onBack = onBack) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(20.dp))
            QuestionTitle(text = stringResource(R.string.journal_title))
            QuestionSubtitle(text = stringResource(R.string.journal_subtitle))

            Spacer(Modifier.height(20.dp))
            OutlinedTextField(
                value = draftTitle,
                onValueChange = onDraftTitleChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.journal_entry_title_placeholder)) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = OnForest,
                    unfocusedTextColor = OnForest,
                    focusedBorderColor = Mint,
                    unfocusedBorderColor = MintOutline,
                    cursorColor = Mint
                )
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = draftText,
                onValueChange = onDraftChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.journal_placeholder)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = OnForest,
                    unfocusedTextColor = OnForest,
                    focusedBorderColor = Mint,
                    unfocusedBorderColor = MintOutline,
                    cursorColor = Mint
                ),
                minLines = 4
            )
            Spacer(Modifier.height(12.dp))
            PrimaryButton(
                text = stringResource(if (editingEntryId == null) R.string.journal_save else R.string.journal_update),
                onClick = onSave
            )
            if (editingEntryId != null) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.journal_cancel_edit),
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnForest,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable(onClick = onCancelEdit)
                )
            }

            Spacer(Modifier.height(24.dp))
            if (entries.isEmpty()) {
                Text(
                    text = stringResource(R.string.journal_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = OnForestMuted
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    entries.forEach { entry ->
                        JournalEntryCard(
                            entry = entry,
                            onEdit = { onEdit(entry) },
                            onDelete = { onDelete(entry.id) }
                        )
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

private val entryDateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy · h:mm a")

@Composable
private fun JournalEntryCard(
    entry: JournalEntryEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var isExpanded by rememberSaveable(entry.id) { mutableStateOf(false) }
    val dateLabel = Instant.ofEpochMilli(entry.createdAt)
        .atZone(ZoneId.systemDefault())
        .format(entryDateFormatter)

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(WpShape.Card)
            .background(ForestPanel.copy(alpha = 0.72f))
            .border(1.dp, MintOutline.copy(alpha = 0.5f), WpShape.Card)
            .padding(16.dp)
    ) {
        Text(
            text = entry.title.ifBlank { stringResource(R.string.journal_entry_untitled) },
            style = MaterialTheme.typography.labelLarge,
            color = OnForestMuted
        )
        Text(text = dateLabel, style = MaterialTheme.typography.labelMedium, color = OnForestMuted)
        Text(
            text = entry.text,
            style = MaterialTheme.typography.bodyLarge,
            color = OnForest,
            maxLines = if (isExpanded) Int.MAX_VALUE else 1,
            modifier = Modifier.clickable { isExpanded = !isExpanded }
        )
        if (entry.text.contains('\n') || entry.text.length > 80) {
            Text(
                text = stringResource(if (isExpanded) R.string.view_less else R.string.view_more),
                style = MaterialTheme.typography.labelLarge,
                color = Mint,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { isExpanded = !isExpanded }
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Text(
                text = stringResource(R.string.journal_edit),
                style = MaterialTheme.typography.bodyMedium,
                color = Mint,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable(onClick = onEdit)
            )
            Spacer(Modifier.padding(horizontal = 6.dp))
            Text(
                text = stringResource(R.string.journal_delete),
                style = MaterialTheme.typography.bodyMedium,
                color = Orange,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable(onClick = onDelete)
            )
        }
    }
}
