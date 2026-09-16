package com.watchpoint.app.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.watchpoint.app.data.repository.OnboardingRepository

class OnboardingViewModelFactory(
    private val repository: OnboardingRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        OnboardingViewModel(repository) as T
}
