package com.watchpoint.app.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.watchpoint.app.data.remote.FirestoreSyncManager
import com.watchpoint.app.data.repository.AuthRepository
import com.watchpoint.app.data.repository.OnboardingRepository

class AuthViewModelFactory(
    private val repository: AuthRepository,
    private val syncManager: FirestoreSyncManager,
    private val onboardingRepository: OnboardingRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        AuthViewModel(repository, syncManager, onboardingRepository) as T
}
