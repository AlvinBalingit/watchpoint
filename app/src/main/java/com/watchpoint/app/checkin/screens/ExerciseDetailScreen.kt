package com.watchpoint.app.checkin.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.watchpoint.app.R
import com.watchpoint.app.checkin.Exercise
import com.watchpoint.app.data.db.ExerciseCompletionEntity
import com.watchpoint.app.ui.components.PrimaryButton
import com.watchpoint.app.ui.components.QuestionTitle
import com.watchpoint.app.ui.components.WpScreen
import com.watchpoint.app.ui.theme.AccentGreen
import com.watchpoint.app.ui.theme.ForestPanel
import com.watchpoint.app.ui.theme.Mint
import com.watchpoint.app.ui.theme.MintOutline
import com.watchpoint.app.ui.theme.OnForest
import com.watchpoint.app.ui.theme.OnForestMuted
import com.watchpoint.app.ui.theme.OnMint
import com.watchpoint.app.ui.theme.Orange
import com.watchpoint.app.ui.theme.WpShape
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Steps for a single exercise, with a countdown timer and a way to mark it done. */
@Composable
fun ExerciseDetailScreen(
    exercise: Exercise,
    done: Boolean,
    savedNotes: List<ExerciseCompletionEntity> = emptyList(),
    onBack: () -> Unit,
    onMarkDone: (String, String) -> Unit,
    onUpdateNote: (Long, String, String) -> Unit,
    onDeleteNote: (Long) -> Unit
) {
    val latestSavedEntry = savedNotes.sortedByDescending { it.completedAt }.firstOrNull()
    val latestSavedNote = latestSavedEntry?.noteText.orEmpty()
    val latestSavedTitle = latestSavedEntry?.noteTitle.orEmpty()
    var noteTitle by rememberSaveable { mutableStateOf(latestSavedTitle) }
    var noteText by rememberSaveable { mutableStateOf(latestSavedNote) }
    var editingNoteId by rememberSaveable { mutableStateOf<Long?>(null) }
    var showNoteRequiredDialog by rememberSaveable { mutableStateOf(false) }

    WpScreen(
        onBack = onBack,
        bottomBar = {
            PrimaryButton(
                text = if (editingNoteId != null) {
                    stringResource(R.string.note_update)
                } else if (done) {
                    stringResource(R.string.exercise_done_badge)
                } else {
                    stringResource(R.string.exercise_mark_done)
                },
                onClick = {
                    val id = editingNoteId
                    val title = noteTitle.trim()
                    val text = noteText.trim()
                    if (exercise.requiresTextEntry && (title.isBlank() || text.isBlank())) {
                        showNoteRequiredDialog = true
                    } else if (id == null) {
                        onMarkDone(title, text)
                        noteTitle = ""
                        noteText = ""
                    } else {
                        onUpdateNote(id, title, text)
                        editingNoteId = null
                    }
                },
                enabled = editingNoteId != null || !done
            )
        }
    ) {
        Spacer(Modifier.height(20.dp))
        QuestionTitle(text = stringResource(exercise.title), color = AccentGreen)
        AnimatedVisibility(
            visible = done,
            enter = scaleIn(initialScale = 0.6f) + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Text(
                text = stringResource(R.string.exercise_completed_toast),
                style = MaterialTheme.typography.bodyMedium,
                color = Mint,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            )
        }
        Text(
            text = "${stringResource(exercise.category.label)} · ${
                stringResource(R.string.exercise_minutes, exercise.durationMinutes)
            }",
            style = MaterialTheme.typography.bodyMedium,
            color = OnForestMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
        )

        Spacer(Modifier.height(14.dp))
        Text(
            text = stringResource(exercise.description),
            style = MaterialTheme.typography.bodyLarge,
            color = OnForest,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            if (exercise.requiresTextEntry) {
                OutlinedTextField(
                    value = noteTitle,
                    onValueChange = { noteTitle = it },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.exercise_note_title_placeholder),
                            color = OnForestMuted
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = OnForest),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Mint,
                        unfocusedBorderColor = MintOutline.copy(alpha = 0.6f),
                        focusedTextColor = OnForest,
                        unfocusedTextColor = OnForest,
                        cursorColor = Mint,
                        focusedContainerColor = ForestPanel.copy(alpha = 0.5f),
                        unfocusedContainerColor = ForestPanel.copy(alpha = 0.5f)
                    )
                )
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.exercise_note_placeholder),
                            color = OnForestMuted
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = OnForest),
                    minLines = 5,
                    maxLines = 8,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 150.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Mint,
                        unfocusedBorderColor = MintOutline.copy(alpha = 0.6f),
                        focusedTextColor = OnForest,
                        unfocusedTextColor = OnForest,
                        cursorColor = Mint,
                        focusedContainerColor = ForestPanel.copy(alpha = 0.5f),
                        unfocusedContainerColor = ForestPanel.copy(alpha = 0.5f)
                    )
                )
            }

            ExerciseTimer(totalSeconds = exercise.durationMinutes * 60)

            Text(
                text = stringResource(R.string.exercise_steps_header),
                style = MaterialTheme.typography.labelLarge,
                color = OnForestMuted
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(WpShape.Card)
                    .background(ForestPanel.copy(alpha = 0.72f))
                    .border(1.dp, MintOutline.copy(alpha = 0.5f), WpShape.Card)
                    .padding(16.dp)
            ) {
                exercise.steps.forEachIndexed { index, step ->
                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(Mint),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                style = MaterialTheme.typography.labelMedium,
                                color = OnMint,
                                textAlign = TextAlign.Center
                            )
                        }
                        Text(
                            text = stringResource(step),
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnForest,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                }
            }

            val savedEntries = savedNotes.sortedByDescending { it.completedAt }
            if (savedEntries.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Saved notes",
                        style = MaterialTheme.typography.labelLarge,
                        color = OnForestMuted
                    )
                    savedEntries.forEach { entry ->
                        if (entry.noteText.isNotBlank()) {
                            var isExpanded by rememberSaveable(entry.id) { mutableStateOf(false) }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(WpShape.Card)
                                    .background(ForestPanel.copy(alpha = 0.6f))
                                    .border(1.dp, MintOutline.copy(alpha = 0.35f), WpShape.Card)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = entry.noteTitle.ifBlank { stringResource(R.string.saved_note_title) },
                                    style = MaterialTheme.typography.labelLarge,
                                    color = OnForestMuted
                                )
                                Text(
                                    text = formatTimestamp(entry.completedAt),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Mint
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = entry.noteText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnForest,
                                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                                    modifier = Modifier.clickable { isExpanded = !isExpanded }
                                )
                                if (entry.noteText.contains('\n') || entry.noteText.length > 80) {
                                    Text(
                                        text = stringResource(if (isExpanded) R.string.view_less else R.string.view_more),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Mint,
                                        modifier = Modifier.clickable { isExpanded = !isExpanded }
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Text(
                                        text = stringResource(R.string.note_edit),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Mint,
                                        modifier = Modifier.padding(end = 12.dp).clickable {
                                            noteTitle = entry.noteTitle
                                            noteText = entry.noteText
                                            editingNoteId = entry.id
                                        }
                                    )
                                    Text(
                                        text = stringResource(R.string.note_delete),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Orange,
                                        modifier = Modifier.clickable { onDeleteNote(entry.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }

    if (showNoteRequiredDialog) {
        AlertDialog(
            onDismissRequest = { showNoteRequiredDialog = false },
            title = { Text(stringResource(R.string.exercise_note_required_title)) },
            text = { Text(stringResource(R.string.exercise_note_required_body)) },
            confirmButton = {
                TextButton(onClick = { showNoteRequiredDialog = false }) {
                    Text(stringResource(R.string.exercise_note_required_action))
                }
            }
        )
    }
}

private fun formatTimestamp(epochMillis: Long): String {
    val formatter = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
    return formatter.format(Date(epochMillis))
}

/** A countdown timer with an animated progress ring, reset each time the screen is opened fresh. */
@Composable
private fun ExerciseTimer(totalSeconds: Int) {
    var remainingSeconds by remember(totalSeconds) { mutableIntStateOf(totalSeconds) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning, totalSeconds) {
        while (isRunning && remainingSeconds > 0) {
            delay(1000)
            remainingSeconds--
        }
        if (remainingSeconds <= 0) isRunning = false
    }

    val finished = remainingSeconds <= 0
    val progress by animateFloatAsState(
        targetValue = if (totalSeconds > 0) remainingSeconds / totalSeconds.toFloat() else 0f,
        animationSpec = tween(400),
        label = "timerProgress"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(Modifier.size(120.dp).aspectRatio(1f)) {
                val stroke = Stroke(width = size.minDimension * 0.08f, cap = StrokeCap.Round)
                drawArc(
                    color = MintOutline.copy(alpha = 0.35f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = stroke,
                    size = Size(size.width - stroke.width, size.height - stroke.width),
                    topLeft = androidx.compose.ui.geometry.Offset(stroke.width / 2f, stroke.width / 2f)
                )
                drawArc(
                    color = if (finished) Orange else Mint,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = stroke,
                    size = Size(size.width - stroke.width, size.height - stroke.width),
                    topLeft = androidx.compose.ui.geometry.Offset(stroke.width / 2f, stroke.width / 2f)
                )
            }
            Text(
                text = if (finished) {
                    stringResource(R.string.exercise_timer_done)
                } else {
                    "%d:%02d".format(remainingSeconds / 60, remainingSeconds % 60)
                },
                style = MaterialTheme.typography.titleMedium,
                color = if (finished) Orange else OnForest,
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.height(12.dp))
        if (!finished) {
            PrimaryButton(
                text = stringResource(
                    if (!isRunning && remainingSeconds == totalSeconds) {
                        R.string.exercise_timer_start
                    } else if (isRunning) {
                        R.string.exercise_timer_pause
                    } else {
                        R.string.exercise_timer_resume
                    }
                ),
                onClick = { isRunning = !isRunning }
            )
        }
    }
}
