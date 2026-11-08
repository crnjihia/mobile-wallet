// path: androidApp/src/main/java/com/chris/mobilewallet/android/MainActivity.kt
package com.chris.mobilewallet.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.chris.mobilewallet.data.db.DatabaseDriverFactory
import com.chris.mobilewallet.data.db.createDatabase
import com.chris.mobilewallet.data.network.ApiService
import com.chris.mobilewallet.data.network.HttpClientFactory
import com.chris.mobilewallet.data.repository.SyncEngine
import com.chris.mobilewallet.data.repository.WalletRepositoryImpl
import com.chris.mobilewallet.security.AutoLockManager
import com.chris.mobilewallet.security.IntegrityChecker
import com.chris.mobilewallet.security.KeystoreManager
import com.chris.mobilewallet.ui.App
import com.chris.mobilewallet.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val keystoreManager = KeystoreManager(this)
        val passphrase = keystoreManager.getOrCreateMasterPassphrase()
        val driverFactory = DatabaseDriverFactory(this)
        val database = createDatabase(driverFactory, passphrase)

        // Pre-populate account with Chris Njihia and KSh 51,234.56
        database.appDatabaseQueries.insertOrUpdateAccount(
            userId = "usr_chris_njihia",
            phoneNumber = "+254712345678",
            fullName = "Chris Njihia",
            balanceMinor = 51_234_56L,
            pinHash = "1234",
            isBiometricEnabled = 1L
        )

        val httpClient = HttpClientFactory.create()
        val apiService = ApiService(httpClient)
        val syncEngine = SyncEngine(database, apiService)
        val walletRepository = WalletRepositoryImpl(database, apiService, syncEngine)
        val integrityChecker = IntegrityChecker()
        val autoLockManager = AutoLockManager()

        val viewModel = MainViewModel(
            walletRepository = walletRepository,
            integrityChecker = integrityChecker,
            autoLockManager = autoLockManager
        )

        setContent {
            App(viewModel = viewModel)
        }
    }
}
