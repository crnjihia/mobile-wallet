// path: shared/src/commonMain/kotlin/com/chris/mobilewallet/security/BiometricAuthenticator.kt
package com.chris.mobilewallet.security

expect class BiometricAuthenticator {
    fun isBiometricAvailable(): Boolean
    suspend fun authenticate(title: String, subtitle: String): BiometricResult
}

sealed interface BiometricResult {
    data object Success : BiometricResult
    data class Error(val code: Int, val message: String) : BiometricResult
    data object Canceled : BiometricResult
    data object UsePinFallback : BiometricResult
}
