// path: shared/src/androidMain/kotlin/com/chris/mobilewallet/security/IntegrityChecker.android.kt
package com.chris.mobilewallet.security

import android.os.Build
import java.io.File

actual class IntegrityChecker {
    actual fun evaluateIntegrity(policy: IntegrityPolicy): IntegrityReport {
        val detectedSignals = mutableListOf<String>()

        // Check Build Tags
        if (Build.TAGS != null && Build.TAGS.contains("test-keys")) {
            detectedSignals.add("Build tags contain test-keys")
        }

        // Check common su binary paths
        val suPaths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        for (path in suPaths) {
            if (File(path).exists()) {
                detectedSignals.add("Found su binary at $path")
            }
        }

        val isRooted = detectedSignals.isNotEmpty()
        return IntegrityReport(
            isRootedOrJailbroken = isRooted,
            signalsDetected = detectedSignals,
            policy = policy
        )
    }
}
