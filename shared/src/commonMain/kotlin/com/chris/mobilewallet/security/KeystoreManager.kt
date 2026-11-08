// path: shared/src/commonMain/kotlin/com/chris/mobilewallet/security/KeystoreManager.kt
package com.chris.mobilewallet.security

expect class KeystoreManager {
    fun getOrCreateMasterPassphrase(): ByteArray
    fun clearMasterPassphrase()
}
