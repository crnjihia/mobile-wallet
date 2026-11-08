// path: composeApp/src/commonMain/kotlin/com/chris/mobilewallet/ui/App.kt
package com.chris.mobilewallet.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.chris.mobilewallet.ui.screens.HomeScreen
import com.chris.mobilewallet.ui.screens.LoginScreen
import com.chris.mobilewallet.ui.screens.SendMoneyScreen
import com.chris.mobilewallet.ui.screens.HistoryScreen
import com.chris.mobilewallet.ui.screens.SecurityScreen
import com.chris.mobilewallet.ui.theme.MobileWalletTheme
import com.chris.mobilewallet.ui.viewmodel.MainViewModel

enum class Screen {
    LOGIN,
    HOME,
    SEND,
    HISTORY,
    SECURITY
}

@Composable
fun App(viewModel: MainViewModel) {
    var currentScreen by remember { mutableStateOf(Screen.LOGIN) }
    var sendError by remember { mutableStateOf<String?>(null) }
    var isDarkTheme by remember { mutableStateOf(false) }

    val balance by viewModel.balance.collectAsState(initial = viewModel.balance.value)
    val transfers by viewModel.transfers.collectAsState(initial = viewModel.transfers.value)
    val integrityReport by viewModel.integrityReport.collectAsState(initial = viewModel.integrityReport.value)

    MobileWalletTheme(darkTheme = isDarkTheme) {
        when (currentScreen) {
            Screen.LOGIN -> LoginScreen(onLoginSuccess = { currentScreen = Screen.HOME })
            Screen.HOME -> HomeScreen(
                isOffline = false,
                balance = balance,
                transfers = transfers,
                onNavigateSend = {
                    sendError = null
                    currentScreen = Screen.SEND
                },
                onNavigateHistory = { currentScreen = Screen.HISTORY },
                onNavigateSecurity = { currentScreen = Screen.SECURITY }
            )
            Screen.SEND -> SendMoneyScreen(
                errorMessage = sendError,
                onSendSubmitted = { recipient, amountMajor ->
                    viewModel.sendMoney(recipient, amountMajor) { error ->
                        if (error == null) {
                            sendError = null
                            currentScreen = Screen.HOME
                        } else {
                            sendError = error
                        }
                    }
                },
                onBack = { currentScreen = Screen.HOME }
            )
            Screen.HISTORY -> HistoryScreen(
                transfers = transfers,
                onBack = { currentScreen = Screen.HOME }
            )
            Screen.SECURITY -> SecurityScreen(
                integrityReport = integrityReport,
                isDarkTheme = isDarkTheme,
                onDarkThemeToggle = { isDarkTheme = it },
                onBack = { currentScreen = Screen.HOME }
            )
        }
    }
}
