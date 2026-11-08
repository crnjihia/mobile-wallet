// path: shared/src/androidMain/kotlin/com/chris/mobilewallet/data/db/DatabaseDriverFactory.android.kt
package com.chris.mobilewallet.data.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.chris.mobilewallet.db.AppDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(passphrase: ByteArray): SqlDriver {
        return AndroidSqliteDriver(
            schema = AppDatabase.Schema,
            context = context,
            name = "wallet_encrypted.db"
        )
    }
}
