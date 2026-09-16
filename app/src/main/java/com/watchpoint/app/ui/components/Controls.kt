package com.watchpoint.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.watchpoint.app.R
import com.watchpoint.app.ui.theme.Mint
import com.watchpoint.app.ui.theme.MintOutline
import com.watchpoint.app.ui.theme.MintPressed
import com.watchpoint.app.ui.theme.OnForest
import com.watchpoint.app.ui.theme.OnMint
import com.watchpoint.app.ui.theme.Orange
import com.watchpoint.app.ui.theme.OrangePressed
import com.watchpoint.app.ui.theme.RingGreen
import com.watchpoint.app.ui.theme.SelectGreen
import com.watchpoint.app.ui.theme.WpShape

/**
 * The orange pill. There is never more than one of these on a screen - it is
 * always the single way forward.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val container by animateColorAsState(
        targetValue = if (pressed) OrangePressed else Orange,
        label = "primaryContainer"
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interaction,
        shape = WpShape.Pill,
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = OnForest,
            disabledContainerColor = Orange.copy(alpha = 0.35f),
            disabledContentColor = OnForest.copy(alpha = 0.6f)
        ),
        contentPadding = PaddingValues(vertical = 16.dp),
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontSize = 17.sp
        )
    }
}

/**
 * A full-width mint answer button - the "How have you been lately?",
 * "How strong is your support system?" and age-group screens.
 */
@Composable
fun AnswerPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        label = "answerScale"
    )
    val container by animateColorAsState(
        targetValue = when {
            selected -> SelectGreen
            pressed -> MintPressed
            else -> Mint
        },
        label = "answerContainer"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(WpShape.Pill)
            .background(container)
            .border(BorderStroke(1.5.dp, MintOutline.copy(alpha = if (selected) 0.9f else 0.35f)), WpShape.Pill)
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            )
            .defaultMinSize(minHeight = 54.dp)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = OnMint,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * A left-aligned radio row over the background - the "How did you first join
 * WatchPoint?" screen. Unselected is a hollow green ring; selected fills and
 * turns the label green.
 */
@Composable
fun RadioRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ring by animateColorAsState(
        targetValue = if (selected) SelectGreen else Color.Transparent,
        label = "ringFill"
    )
    val label by animateColorAsState(
        targetValue = if (selected) SelectGreen else OnForest,
        label = "ringLabel"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(WpShape.Card)
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = onClick
            )
            .padding(vertical = 11.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(ring)
                .border(2.dp, if (selected) SelectGreen else RingGreen, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = stringResource(R.string.cd_selected),
                    tint = OnForest,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
        Spacer(Modifier.width(14.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = label
        )
    }
}

/** Mint chip that opens the time picker on the sleep-schedule screens. */
@Composable
fun TimeChip(
    time: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    Box(
        modifier = modifier
            .clip(WpShape.Pill)
            .background(if (pressed) MintPressed else Mint)
            .border(BorderStroke(1.5.dp, MintOutline.copy(alpha = 0.4f)), WpShape.Pill)
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            )
            .width(150.dp)
            .height(46.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = time,
            style = MaterialTheme.typography.labelLarge,
            color = OnMint
        )
    }
}
