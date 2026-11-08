// path: shared/src/iosMain/kotlin/com/chris/mobilewallet/data/network/CertificatePinning.ios.kt
package com.chris.mobilewallet.data.network

import io.ktor.client.engine.darwin.DarwinClientEngineConfig

actual class CertificatePinningConfigurator {
    actual fun configurePinning(builder: Any, config: PinConfig) {
        if (config.allowDebugOverride) return
        val darwinConfig = builder as? DarwinClientEngineConfig ?: return
        darwinConfig.configureRequest {
            // Darwin session challenge configuration
        }
    }
}
