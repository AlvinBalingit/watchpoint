package com.watchpoint.app.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.tasks.await

/**
 * Real email/password accounts, required before the app is used - there is
 * no anonymous or social sign-in path. The signed-in uid is what scopes this
 * account's data in Firestore (users/{uid}/...).
 */
class FirebaseAuthManager(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    val userId: String?
        get() = auth.currentUser?.uid

    val isSignedIn: Boolean
        get() = auth.currentUser != null

    val email: String?
        get() = auth.currentUser?.email

    /** Emits the current sign-in state and every change to it. */
    val authStateFlow: Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser != null)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.distinctUntilChanged()

    /** Creates a new account and signs into it, returning the uid. */
    suspend fun registerWithEmail(email: String, password: String): String {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user?.uid ?: error("Registration returned no user")
    }

    /** Signs into an existing account, returning the uid. */
    suspend fun signInWithEmail(email: String, password: String): String {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user?.uid ?: error("Sign-in returned no user")
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun updateEmail(email: String) {
        auth.currentUser?.updateEmail(email)?.await() ?: error("No signed-in user")
    }

    suspend fun updateProfile(
        firstName: String,
        middleInitial: String,
        lastName: String,
        birthday: String
    ) {
        val uid = auth.currentUser?.uid ?: error("No signed-in user")
        firestore.collection("users").document(uid).set(
            mapOf(
                "firstName" to firstName,
                "middleInitial" to middleInitial,
                "lastName" to lastName,
                "birthday" to birthday
            ),
            com.google.firebase.firestore.SetOptions.merge()
        ).await()
        val displayName = listOf(firstName, middleInitial, lastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
        auth.currentUser?.updateProfile(
            com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(displayName.ifBlank { null })
                .build()
        )?.await()
    }
}
