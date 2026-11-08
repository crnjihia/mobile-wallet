// path: shared/src/commonMain/kotlin/com/chris/mobilewallet/domain/repository/WalletRepository.kt
package com.chris.mobilewallet.domain.repository

import com.chris.mobilewallet.core.Money
import com.chris.mobilewallet.core.PhoneNumber
import com.chris.mobilewallet.core.Result
import com.chris.mobilewallet.domain.model.Account
import com.chris.mobilewallet.domain.model.Transfer
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    fun getAccount(): Flow<Account?>
    fun getBalance(): Flow<Money>
    fun getTransferHistory(): Flow<List<Transfer>>
    fun getPendingOutboxTransfers(): Flow<List<Transfer>>

    suspend fun fetchRemoteBalance(): Result<Money>
    suspend fun queueTransfer(recipientPhone: PhoneNumber, amount: Money, note: String): Result<Transfer>
    suspend fun syncPendingTransfers(): Result<Unit>
}
