// path: shared/src/iosMain/kotlin/com/chris/mobilewallet/security/BiometricAuthenticator.ios.kt
package com.chris.mobilewallet.security

import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics
import platform.Foundation.NSError
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

actual class BiometricAuthenticator {

    actual fun isBiometricAvailable(): Boolean {
        val context = LAContext()
        return context.canEvaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, error = null)
    }

    actual suspend fun authenticate(title: String, subtitle: String): BiometricResult {
        val context = LAContext()
        context.localizedFallbackTitle = "Use PIN"
        return suspendCancellableCoroutine { continuation ->
            context.evaluatePolicy(
                policy = LAPolicyDeviceOwnerAuthenticationWithBiometrics,
                localizedReason = "$title - $subtitle"
            ) { success, error ->
                if (continuation.isActive) {
                    if (success) {
                        continuation.resume(BiometricResult.Success)
                    } else {
                        val nsError = error as? NSError
                        if (nsError?.code == -2L) { // User canceled
                            continuation.resume(BiometricResult.Canceled)
                        } else if (nsError?.code == -3L) { // Fallback to PIN selected
                            continuation.resume(BiometricResult.UsePinFallback)
                        } else {
                            continuation.resume(BiometricResult.Error((nsError?.code ?: -1).toInt(), nsError?.localizedDescription ?: "Authentication failed"))
                        }
                    }
                }
            }
        }
    }
}
