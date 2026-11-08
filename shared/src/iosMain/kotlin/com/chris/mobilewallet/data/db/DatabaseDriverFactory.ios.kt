// path: shared/src/iosMain/kotlin/com/chris/mobilewallet/data/db/DatabaseDriverFactory.ios.kt
package com.chris.mobilewallet.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.cash.sqldelight.driver.native.DatabaseConfiguration
import com.chris.mobilewallet.db.AppDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(passphrase: ByteArray): SqlDriver {
        val passphraseString = passphrase.decodeToString()
        return NativeSqliteDriver(
            schema = AppDatabase.Schema,
            name = "wallet_encrypted.db",
            onConfiguration = { config ->
                config.copy(
                    encryptionConfig = DatabaseConfiguration.Encryption(key = passphraseString)
                )
            }
        )
    }
}
