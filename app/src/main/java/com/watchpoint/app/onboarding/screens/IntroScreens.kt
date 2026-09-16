package com.watchpoint.app.onboarding.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.watchpoint.app.R
import com.watchpoint.app.auth.AuthMode
import com.watchpoint.app.ui.components.ForestBackground
import com.watchpoint.app.ui.components.MascotWithRing
import com.watchpoint.app.ui.components.PrimaryButton
import com.watchpoint.app.ui.components.WpScreen
import com.watchpoint.app.ui.components.Wordmark
import com.watchpoint.app.ui.theme.CreamMuted
import com.watchpoint.app.ui.theme.Cream
import com.watchpoint.app.ui.theme.ForestPanel
import com.watchpoint.app.ui.theme.OnCream
import com.watchpoint.app.ui.theme.OnForest
import com.watchpoint.app.ui.theme.OnForestMuted
import com.watchpoint.app.ui.theme.Orange
import com.watchpoint.app.ui.theme.WpShape
import com.watchpoint.app.ui.theme.WpSpace
import kotlinx.coroutines.delay

/* ------------------------------------------------------------------ 01 ---- */

/**
 * Deck page 1. The wordmark and Wabby sit on the forest field; account actions
 * live on a cream sheet anchored to the bottom edge.
 */
@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onSignIn: () -> Unit
) {
    SheetScreen(
        sheet = {
            PrimaryButton(text = stringResource(R.string.get_started), onClick = onGetStarted)
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.already_have_account),
                style = MaterialTheme.typography.bodyLarge,
                color = OnCream,
                textDecoration = TextDecoration.Underline,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onSignIn)
                    .padding(vertical = 4.dp)
            )
        }
    ) {
        Spacer(Modifier.height(64.dp))
        Wordmark()
        Spacer(Modifier.weight(1f))
        MascotWithRing(
            mascot = R.drawable.wabby_hero,
            ringSize = 300.dp,
            mascotSize = 200.dp
        )
        Spacer(Modifier.weight(1.1f))
    }
}

/* ------------------------------------------------------------------ 02 ---- */

/**
 * Deck page 2. A brief hand-off while the app "prepares" the session; it pops
 * itself off the back stack when it finishes.
 */
@Composable
fun LoadingScreen(
    onBack: () -> Unit,
    onFinished: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(1_600)
        onFinished()
    }

    Box(Modifier.fillMaxSize()) {
        ForestBackground()
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(6.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.cd_back),
                tint = OnForestMuted
            )
        }
        CircularProgressIndicator(
            color = OnForest,
            strokeWidth = 3.dp,
            modifier = Modifier
                .align(Alignment.Center)
                .size(44.dp)
        )
    }
}

/* ------------------------------------------------------------------ 03 ---- */

/** Deck page 3. Wabby introduces himself. */
@Composable
fun GreetingScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    WpScreen(
        onBack = onBack,
        bottomBar = { PrimaryButton(text = stringResource(R.string.next), onClick = onNext) }
    ) {
        Spacer(Modifier.height(44.dp))
        Text(
            text = stringResource(R.string.wabby_greeting),
            style = MaterialTheme.typography.headlineSmall,
            color = OnForest,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text = stringResource(R.string.wabby_greeting_sub),
            style = MaterialTheme.typography.headlineSmall,
            color = OnForest,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.weight(1f))
        MascotWithRing(
            mascot = R.drawable.wabby_wave,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            ringSize = 290.dp,
            mascotSize = 215.dp
        )
        Spacer(Modifier.weight(1f))
    }
}

/* ------------------------------------------------------------------ 04 ---- */

/**
 * Deck page 4. Real email/password registration and sign-in, on the cream
 * sheet - no social providers, per product decision.
 */
@Composable
fun AuthScreen(
    mode: AuthMode,
    email: String,
    password: String,
    confirmPassword: String,
    firstName: String,
    middleInitial: String,
    lastName: String,
    birthday: String,
    isLoading: Boolean,
    errorMessage: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onFirstNameChange: (String) -> Unit,
    onMiddleInitialChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onBirthdayChange: (String) -> Unit,
        privacyConsent: Boolean,
        onPrivacyConsentChange: (Boolean) -> Unit,
    onToggleMode: () -> Unit,
    onSubmit: () -> Unit,
    onOpenTerms: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onBack: () -> Unit
) {
    var showPrivacyNotice by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    SheetScreen(
        onBack = onBack,
        sheet = {
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.email_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                colors = authFieldColors()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.password_label)) },
                singleLine = true,
                visualTransformation = if (showPassword) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = if (mode == AuthMode.Register) ImeAction.Next else ImeAction.Done
                ),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = stringResource(
                                if (showPassword) R.string.cd_hide_password else R.string.cd_show_password
                            )
                        )
                    }
                },
                colors = authFieldColors()
            )
            if (mode == AuthMode.Register) {
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.confirm_password_label)) },
                    singleLine = true,
                    visualTransformation = if (showConfirmPassword) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    trailingIcon = {
                        IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                            Icon(
                                imageVector = if (showConfirmPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = stringResource(
                                    if (showConfirmPassword) R.string.cd_hide_password else R.string.cd_show_password
                                )
                            )
                        }
                    },
                    colors = authFieldColors()
                )
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = privacyConsent,
                        onCheckedChange = { checked ->
                            if (checked) showPrivacyNotice = true
                            else onPrivacyConsentChange(false)
                        }
                    )
                    Text(
                        text = stringResource(R.string.privacy_consent),
                        style = MaterialTheme.typography.bodySmall,
                        color = OnCream,
                        modifier = Modifier.clickable { showPrivacyNotice = true }
                    )
                }
            }

            if (errorMessage != null) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = Orange,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(14.dp))
            PrimaryButton(
                text = stringResource(
                    if (mode == AuthMode.Register) R.string.create_account else R.string.sign_in
                ),
                onClick = onSubmit,
                enabled = !isLoading && (mode == AuthMode.SignIn || privacyConsent)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(
                    if (mode == AuthMode.Register) R.string.have_account_link else R.string.need_account_link
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = OnCream,
                textDecoration = TextDecoration.Underline,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleMode)
                    .padding(vertical = 4.dp)
            )
            Spacer(Modifier.height(10.dp))
            LegalLine(onOpenTerms = onOpenTerms, onOpenPrivacy = onOpenPrivacy)
        }
    ) {
        Spacer(Modifier.height(28.dp))
        Wordmark(fontSize = 42.sp)
        Spacer(Modifier.weight(1f))
        MascotWithRing(
            mascot = R.drawable.wabby_book,
            ringSize = 270.dp,
            mascotSize = 190.dp
        )
        Spacer(Modifier.weight(0.8f))
    }

    if (showPrivacyNotice) {
        AlertDialog(
            onDismissRequest = { showPrivacyNotice = false },
            title = { Text(stringResource(R.string.privacy_consent_title)) },
            text = { Text(stringResource(R.string.privacy_consent_body)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onPrivacyConsentChange(true)
                        showPrivacyNotice = false
                    }
                ) {
                    Text(stringResource(R.string.privacy_consent_accept))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPrivacyNotice = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = OnCream,
    unfocusedTextColor = OnCream,
    focusedBorderColor = ForestPanel,
    unfocusedBorderColor = CreamMuted,
    focusedLabelColor = ForestPanel,
    unfocusedLabelColor = CreamMuted,
    cursorColor = ForestPanel
)

@Composable
private fun LegalLine(onOpenTerms: () -> Unit, onOpenPrivacy: () -> Unit) {
    val prefix = stringResource(R.string.legal_prefix)
    val terms = stringResource(R.string.legal_terms)
    val and = stringResource(R.string.legal_and)
    val privacy = stringResource(R.string.legal_privacy)

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = prefix,
            style = MaterialTheme.typography.bodySmall,
            color = CreamMuted,
            textAlign = TextAlign.Center
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = terms,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline
                ),
                color = CreamMuted,
                modifier = Modifier.clickable(onClick = onOpenTerms)
            )
            Text(
                text = " $and ",
                style = MaterialTheme.typography.bodySmall,
                color = CreamMuted
            )
            Text(
                text = privacy,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline
                ),
                color = CreamMuted,
                modifier = Modifier.clickable(onClick = onOpenPrivacy)
            )
        }
    }
}

/* ------------------------------------------------------------------ 05 ---- */

/** Deck page 5. A quote to set the tone before the questions start. */
@Composable
fun QuoteScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    WpScreen(
        onBack = onBack,
        bottomBar = { PrimaryButton(text = stringResource(R.string.next), onClick = onNext) }
    ) {
        Spacer(Modifier.height(40.dp))
        Text(
            text = stringResource(R.string.quote_body),
            style = MaterialTheme.typography.headlineSmall,
            color = OnForest,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.quote_author),
            style = MaterialTheme.typography.bodyMedium,
            color = OnForestMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.weight(1f))
        MascotWithRing(
            mascot = R.drawable.wabby_pray,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            ringSize = 300.dp,
            mascotSize = 210.dp
        )
        Spacer(Modifier.weight(1f))
    }
}

/* --------------------------------------------------------------- shared --- */

/**
 * Layout shared by the two screens that carry account actions: forest field on
 * top, cream sheet clipped to a large top radius underneath.
 */
@Composable
private fun SheetScreen(
    onBack: (() -> Unit)? = null,
    sheet: @Composable ColumnScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        ForestBackground()

        Column(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = WpSpace.ScreenH),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = content
            )

            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(WpShape.Sheet)
                    .background(Cream)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(top = 26.dp, bottom = 26.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = sheet
            )
        }

        if (onBack != null) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(6.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back),
                    tint = OnForestMuted
                )
            }
        }
    }
}
