// path: shared/src/androidMain/kotlin/com/chris/mobilewallet/data/network/CertificatePinning.android.kt
package com.chris.mobilewallet.data.network

import okhttp3.CertificatePinner
import okhttp3.OkHttpClient

actual class CertificatePinningConfigurator {
    actual fun configurePinning(builder: Any, config: PinConfig) {
        if (config.allowDebugOverride) return
        val okBuilder = builder as? OkHttpClient.Builder ?: return
        val pinner = CertificatePinner.Builder()
            .add(config.hostname, config.primaryPin)
            .add(config.hostname, config.backupPin)
            .build()
        okBuilder.certificatePinner(pinner)
    }
}
