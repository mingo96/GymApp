package com.mintocode.rutinapp.sync

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** Lightweight global sync state (for basic UX feedback) */
object SyncStateHolder {
    private val _isSyncing = MutableStateFlow(false)
    private val _lastError = MutableStateFlow<String?>(null)
    private val _lastSuccess = MutableStateFlow<Long?>(null)

    val isSyncing: StateFlow<Boolean> = _isSyncing
    val lastError: StateFlow<String?> = _lastError
    val lastSuccess: StateFlow<Long?> = _lastSuccess

    fun start() { _isSyncing.value = true; _lastError.value = null }
    fun success() { _isSyncing.value = false; _lastSuccess.value = System.currentTimeMillis() }
    fun fail(msg: String?) { _isSyncing.value = false; _lastError.value = msg }
}