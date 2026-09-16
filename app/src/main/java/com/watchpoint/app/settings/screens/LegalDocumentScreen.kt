package com.watchpoint.app.settings.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.watchpoint.app.R
import com.watchpoint.app.ui.components.QuestionTitle
import com.watchpoint.app.ui.components.WpScreen
import com.watchpoint.app.ui.theme.Mint
import com.watchpoint.app.ui.theme.OnForest
import com.watchpoint.app.ui.theme.OnForestMuted

@Composable
fun TermsOfServiceScreen(onBack: () -> Unit) {
    LegalDocumentScreen(
        title = stringResource(R.string.terms_title),
        updated = stringResource(R.string.terms_updated),
        sections = listOf(
            stringResource(R.string.terms_intro) to null,
            stringResource(R.string.terms_use_title) to stringResource(R.string.terms_use_body),
            stringResource(R.string.terms_content_title) to stringResource(R.string.terms_content_body),
            stringResource(R.string.terms_health_title) to stringResource(R.string.terms_health_body),
            stringResource(R.string.terms_availability_title) to stringResource(R.string.terms_availability_body),
            stringResource(R.string.terms_contact_title) to stringResource(R.string.terms_contact_body)
        ),
        onBack = onBack
    )
}

@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    LegalDocumentScreen(
        title = stringResource(R.string.privacy_title),
        updated = stringResource(R.string.privacy_updated),
        sections = listOf(
            stringResource(R.string.privacy_intro) to null,
            stringResource(R.string.privacy_collect_title) to stringResource(R.string.privacy_collect_body),
            stringResource(R.string.privacy_use_title) to stringResource(R.string.privacy_use_body),
            stringResource(R.string.privacy_share_title) to stringResource(R.string.privacy_share_body),
            stringResource(R.string.privacy_retention_title) to stringResource(R.string.privacy_retention_body),
            stringResource(R.string.privacy_security_title) to stringResource(R.string.privacy_security_body),
            stringResource(R.string.privacy_rights_title) to stringResource(R.string.privacy_rights_body),
            stringResource(R.string.privacy_contact_title) to stringResource(R.string.privacy_contact_body)
        ),
        onBack = onBack
    )
}

@Composable
private fun LegalDocumentScreen(
    title: String,
    updated: String,
    sections: List<Pair<String, String?>>,
    onBack: () -> Unit
) {
    WpScreen(onBack = onBack) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(Modifier.height(20.dp))
            QuestionTitle(text = title, color = Mint)
            Text(
                text = updated,
                style = MaterialTheme.typography.labelMedium,
                color = OnForestMuted
            )
            sections.forEach { (headingOrIntro, body) ->
                if (body == null) {
                    Text(
                        text = headingOrIntro,
                        style = MaterialTheme.typography.bodyLarge,
                        color = OnForest
                    )
                } else {
                    Text(
                        text = headingOrIntro,
                        style = MaterialTheme.typography.titleMedium,
                        color = Mint
                    )
                    Text(
                        text = body,
                        style = MaterialTheme.typography.bodyLarge,
                        color = OnForest
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}