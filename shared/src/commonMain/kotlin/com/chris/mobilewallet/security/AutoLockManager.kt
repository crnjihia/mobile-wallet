// path: shared/src/commonMain/kotlin/com/chris/mobilewallet/security/AutoLockManager.kt
package com.chris.mobilewallet.security

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AutoLockManager(
    private var lockTimeoutSeconds: Long = 60L // 60 seconds auto-lock default
) {
    private val _isLocked = MutableStateFlow(true)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    private var lastActiveTimestamp: Long = System.currentTimeMillis()

    fun updateActivity() {
        lastActiveTimestamp = System.currentTimeMillis()
    }

    fun onAppBackgrounded() {
        _isLocked.value = true
    }

    fun checkTimeout() {
        val elapsed = (System.currentTimeMillis() - lastActiveTimestamp) / 1000
        if (elapsed >= lockTimeoutSeconds) {
            _isLocked.value = true
        }
    }

    fun unlock() {
        lastActiveTimestamp = System.currentTimeMillis()
        _isLocked.value = false
    }

    fun setLockTimeout(seconds: Long) {
        require(seconds > 0) { "Timeout must be greater than 0" }
        lockTimeoutSeconds = seconds
    }
}
