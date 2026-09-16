package com.watchpoint.app.data.repository

import com.watchpoint.app.data.remote.FirebaseAuthManager
import kotlinx.coroutines.flow.Flow

/** Thin pass-through to Firebase Auth, matching the repository-then-ViewModel layering used everywhere else. */
class AuthRepository(private val authManager: FirebaseAuthManager) {

    val authStateFlow: Flow<Boolean> = authManager.authStateFlow

    val isSignedIn: Boolean get() = authManager.isSignedIn

    val email: String? get() = authManager.email

    suspend fun register(email: String, password: String): String =
        authManager.registerWithEmail(email, password)

    suspend fun signIn(email: String, password: String): String =
        authManager.signInWithEmail(email, password)

    fun signOut() = authManager.signOut()

    suspend fun updateEmail(email: String) = authManager.updateEmail(email)

    suspend fun updateProfile(firstName: String, middleInitial: String, lastName: String, birthday: String) =
        authManager.updateProfile(firstName, middleInitial, lastName, birthday)
}
