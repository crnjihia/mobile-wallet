// path: shared/src/commonMain/kotlin/com/chris/mobilewallet/security/IntegrityChecker.kt
package com.chris.mobilewallet.security

enum class IntegrityPolicy {
    WARN_ONLY,
    BLOCK_TRANSACTIONS,
    BLOCK_APP_LAUNCH
}

data class IntegrityReport(
    val isRootedOrJailbroken: Boolean,
    val signalsDetected: List<String>,
    val policy: IntegrityPolicy
)

expect class IntegrityChecker {
    fun evaluateIntegrity(policy: IntegrityPolicy = IntegrityPolicy.WARN_ONLY): IntegrityReport
}
