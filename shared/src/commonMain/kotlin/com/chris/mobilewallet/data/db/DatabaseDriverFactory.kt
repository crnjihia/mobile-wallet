// path: shared/src/commonMain/kotlin/com/chris/mobilewallet/data/db/DatabaseDriverFactory.kt
package com.chris.mobilewallet.data.db

import app.cash.sqldelight.db.SqlDriver
import com.chris.mobilewallet.db.AppDatabase

expect class DatabaseDriverFactory {
    fun createDriver(passphrase: ByteArray): SqlDriver
}

fun createDatabase(driverFactory: DatabaseDriverFactory, passphrase: ByteArray): AppDatabase {
    val driver = driverFactory.createDriver(passphrase)
    return AppDatabase(driver)
}
