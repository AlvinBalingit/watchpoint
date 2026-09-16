package com.watchpoint.app.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.watchpoint.app.data.repository.AuthRepository

class AuthViewModelFactory(
    private val repository: AuthRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        AuthViewModel(repository) as T
}
