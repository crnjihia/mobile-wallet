// path: composeApp/src/commonMain/kotlin/com/chris/mobilewallet/ui/viewmodel/WalletViewModels.kt
package com.chris.mobilewallet.ui.viewmodel

import com.chris.mobilewallet.core.Currency
import com.chris.mobilewallet.core.Money
import com.chris.mobilewallet.core.PhoneNumber
import com.chris.mobilewallet.core.Result
import com.chris.mobilewallet.domain.model.Transfer
import com.chris.mobilewallet.domain.repository.WalletRepository
import com.chris.mobilewallet.security.AutoLockManager
import com.chris.mobilewallet.security.IntegrityChecker
import com.chris.mobilewallet.security.IntegrityReport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    val walletRepository: WalletRepository,
    val integrityChecker: IntegrityChecker,
    val autoLockManager: AutoLockManager
) {
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _transfers = MutableStateFlow<List<Transfer>>(emptyList())
    val transfers: StateFlow<List<Transfer>> = _transfers.asStateFlow()

    private val _balance = MutableStateFlow(Money(5123456L, Currency.KES))
    val balance: StateFlow<Money> = _balance.asStateFlow()

    private val _integrityReport = MutableStateFlow(integrityChecker.evaluateIntegrity())
    val integrityReport: StateFlow<IntegrityReport> = _integrityReport.asStateFlow()

    init {
        scope.launch(Dispatchers.Default) {
            try {
                walletRepository.getBalance().collect { bal ->
                    _balance.value = bal
                }
            } catch (ignored: Exception) {}
        }
        scope.launch(Dispatchers.Default) {
            try {
                walletRepository.getTransferHistory().collect { dbList ->
                    if (dbList.isNotEmpty()) {
                        _transfers.value = (_transfers.value + dbList).distinctBy { it.id }.sortedByDescending { it.timestamp }
                    }
                }
            } catch (ignored: Exception) {}
        }
    }

    fun sendMoney(recipientRaw: String, amountMajor: Long, onComplete: (String?) -> Unit) {
        scope.launch {
            try {
                val phone = PhoneNumber(recipientRaw)
                val amount = Money.fromMajor(amountMajor)
                val validationError = Transfer.validateTransfer(_balance.value, amount)
                if (validationError != null) {
                    onComplete(validationError)
                    return@launch
                }

                val result = walletRepository.queueTransfer(phone, amount, "Transfer via Mobile Wallet App")
                if (result is Result.Success) {
                    val newTransfer = result.data
                    val fee = Transfer.calculateFee(amount)
                    _balance.value = _balance.value - (amount + fee)
                    _transfers.value = (listOf(newTransfer) + _transfers.value).distinctBy { it.id }.sortedByDescending { it.timestamp }
                    onComplete(null)
                } else {
                    onComplete("Failed to queue transfer")
                }
            } catch (e: Exception) {
                onComplete(e.message ?: "Invalid phone number or amount")
            }
        }
    }
}
