package com.watchpoint.app.checkin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watchpoint.app.data.db.JournalEntryEntity
import com.watchpoint.app.data.repository.JournalRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Free-form journal entries, independent of the daily check-in flow. */
class JournalViewModel(private val repository: JournalRepository) : ViewModel() {

    val entries: StateFlow<List<JournalEntryEntity>> = repository.observeEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var draftText by mutableStateOf("")
        private set

    var draftTitle by mutableStateOf("")
        private set

    var editingEntryId by mutableStateOf<Long?>(null)
        private set

    fun updateDraftText(value: String) {
        draftText = value
    }

    fun updateDraftTitle(value: String) {
        draftTitle = value
    }

    fun submitEntry() {
        val text = draftText.trim()
        val title = draftTitle.trim()
        if (title.isEmpty() || text.isEmpty()) return
        viewModelScope.launch {
            val id = editingEntryId
            if (id == null) repository.addEntry(title, text) else repository.updateEntry(id, title, text)
        }
        draftTitle = ""
        draftText = ""
        editingEntryId = null
    }

    fun beginEditing(entry: JournalEntryEntity) {
        editingEntryId = entry.id
        draftTitle = entry.title
        draftText = entry.text
    }

    fun cancelEditing() {
        editingEntryId = null
        draftTitle = ""
        draftText = ""
    }

    fun deleteEntry(id: Long) {
        viewModelScope.launch { repository.deleteEntry(id) }
    }
}
