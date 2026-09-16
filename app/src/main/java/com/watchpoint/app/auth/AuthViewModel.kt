package com.watchpoint.app.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.watchpoint.app.data.repository.AuthRepository
import kotlinx.coroutines.launch

enum class AuthMode { Register, SignIn }

/** Owns the registration/sign-in form state; AuthScreen itself stays a dumb composable like every other screen. */
class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    var mode by mutableStateOf(AuthMode.Register)
        private set

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var confirmPassword by mutableStateOf("")
        private set

    var firstName by mutableStateOf("")
        private set
    var middleInitial by mutableStateOf("")
        private set
    var lastName by mutableStateOf("")
        private set
    var birthday by mutableStateOf("")
        private set

    var privacyConsent by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    val currentEmail: String?
        get() = repository.email

    fun toggleMode() {
        mode = if (mode == AuthMode.Register) AuthMode.SignIn else AuthMode.Register
        privacyConsent = false
        errorMessage = null
    }

    fun updatePrivacyConsent(value: Boolean) {
        privacyConsent = value
    }

    fun updateEmail(value: String) {
        email = value
    }

    fun updatePassword(value: String) {
        password = value
    }

    fun updateConfirmPassword(value: String) {
        confirmPassword = value
    }

    fun updateFirstName(value: String) { firstName = value }
    fun updateMiddleInitial(value: String) { middleInitial = value.take(1) }
    fun updateLastName(value: String) { lastName = value }
    fun updateBirthday(value: String) { birthday = value }

    fun updateProfileEmail(email: String, onComplete: (String?) -> Unit) {
        val normalized = email.trim()
        if (normalized.isBlank()) {
            onComplete("Enter your email.")
            return
        }
        viewModelScope.launch {
            try {
                repository.updateEmail(normalized)
                onComplete(null)
            } catch (e: Exception) {
                onComplete(e.message ?: "Unable to update your email.")
            }
        }
    }

    fun updateProfile(
        email: String,
        firstName: String,
        middleInitial: String,
        lastName: String,
        birthday: String,
        onComplete: (String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.updateEmail(email.trim())
                repository.updateProfile(
                    firstName.trim(), middleInitial.trim(), lastName.trim(), birthday.trim()
                )
                onComplete(null)
            } catch (e: Exception) {
                onComplete(e.message ?: "Unable to update your profile.")
            }
        }
    }

    fun submit(onSuccess: () -> Unit) {
        val validationError = validate()
        if (validationError != null) {
            errorMessage = validationError
            return
        }

        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                if (mode == AuthMode.Register) {
                    repository.register(email.trim(), password)
                    repository.updateProfile(
                        firstName.trim(), middleInitial.trim(), lastName.trim(), birthday.trim()
                    )
                } else {
                    repository.signIn(email.trim(), password)
                }
                onSuccess()
            } catch (e: Exception) {
                errorMessage = e.toUserMessage()
            } finally {
                isLoading = false
            }
        }
    }

    private fun validate(): String? {
        if (email.isBlank()) return "Enter your email."
        if (password.length < 6) return "Password must be at least 6 characters."
        if (mode == AuthMode.Register && password != confirmPassword) return "Passwords don't match."
        if (mode == AuthMode.Register && !privacyConsent) return "Please accept the privacy notice to continue."
        return null
    }

    private fun Exception.toUserMessage(): String = when (this) {
        is FirebaseAuthUserCollisionException -> "That email is already registered."
        is FirebaseAuthInvalidUserException -> "No account found for that email."
        is FirebaseAuthInvalidCredentialsException -> "Incorrect email or password."
        is FirebaseAuthWeakPasswordException -> "Choose a stronger password."
        else -> message ?: "Something went wrong. Please try again."
    }
}
