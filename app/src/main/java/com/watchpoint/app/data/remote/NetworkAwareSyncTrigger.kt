package com.watchpoint.app.data.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

private const val PERIODIC_RETRY_MS = 5 * 60 * 1000L // safety-net retry while online, in case a push failed transiently

/**
 * Watches for connectivity and calls [FirestoreSyncManager.syncAll] whenever
 * the device comes online, plus a periodic retry while it stays online.
 * Started once for the app's whole lifetime from WatchPointApplication, not
 * tied to any single screen's ViewModel.
 */
class NetworkAwareSyncTrigger(
    private val context: Context,
    private val syncManager: FirestoreSyncManager,
    private val authManager: FirebaseAuthManager
) {
    private fun observeOnline(): Flow<Boolean> = callbackFlow {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(false)
            }

            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                trySend(capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, callback)

        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()

    /** Runs for the lifetime of [scope] - call once from Application.onCreate. */
    fun start(scope: CoroutineScope) {
        var periodicSyncJob: Job? = null
        scope.launch {
            observeOnline().collect { online ->
                periodicSyncJob?.cancel()
                if (online) {
                    periodicSyncJob = launch {
                        while (true) {
                            if (authManager.isSignedIn) syncManager.syncAll()
                            delay(PERIODIC_RETRY_MS)
                        }
                    }
                }
            }
        }
    }
}
