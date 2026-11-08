// path: shared/src/iosMain/kotlin/com/chris/mobilewallet/security/IntegrityChecker.ios.kt
package com.chris.mobilewallet.security

import platform.Foundation.NSFileManager
import platform.Foundation.NSString
import platform.Foundation.writeToFile
import platform.Foundation.NSUTF8StringEncoding

actual class IntegrityChecker {
    actual fun evaluateIntegrity(policy: IntegrityPolicy): IntegrityReport {
        val detectedSignals = mutableListOf<String>()

        val jailbreakPaths = arrayOf(
            "/Applications/Cydia.app",
            "/Library/MobileSubstrate/MobileSubstrate.dylib",
            "/bin/bash",
            "/usr/sbin/sshd",
            "/etc/apt",
            "/private/var/lib/apt/"
        )

        val fileManager = NSFileManager.defaultManager
        for (path in jailbreakPaths) {
            if (fileManager.fileExistsAtPath(path)) {
                detectedSignals.add("Jailbreak artifact detected at $path")
            }
        }

        // Sandbox write check
        val testString = NSString.create(string = "IntegrityCheck")
        val sandboxPath = "/private/jailbreak_sandbox_test.txt"
        val writeSuccess = testString.writeToFile(
            path = sandboxPath,
            atomically = true,
            encoding = NSUTF8StringEncoding,
            error = null
        )
        if (writeSuccess) {
            detectedSignals.add("Sandbox restriction bypassed: write to system root succeeded")
            fileManager.removeItemAtPath(sandboxPath, error = null)
        }

        val isJailbroken = detectedSignals.isNotEmpty()
        return IntegrityReport(
            isRootedOrJailbroken = isJailbroken,
            signalsDetected = detectedSignals,
            policy = policy
        )
    }
}
