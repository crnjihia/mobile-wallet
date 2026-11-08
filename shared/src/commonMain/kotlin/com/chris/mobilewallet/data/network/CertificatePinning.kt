// path: shared/src/commonMain/kotlin/com/chris/mobilewallet/data/network/CertificatePinning.kt
package com.chris.mobilewallet.data.network

data class PinConfig(
    val hostname: String,
    val primaryPin: String,
    val backupPin: String,
    val allowDebugOverride: Boolean = false
)

expect class CertificatePinningConfigurator {
    fun configurePinning(builder: Any, config: PinConfig)
}
