package com.watchpoint.app.checkin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.watchpoint.app.data.repository.JournalRepository

class JournalViewModelFactory(
    private val repository: JournalRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        JournalViewModel(repository) as T
}
