// path: shared/src/commonMain/kotlin/com/chris/mobilewallet/data/repository/WalletRepositoryImpl.kt
package com.chris.mobilewallet.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.chris.mobilewallet.core.Currency
import com.chris.mobilewallet.core.Money
import com.chris.mobilewallet.core.PhoneNumber
import com.chris.mobilewallet.core.Result
import com.chris.mobilewallet.data.network.ApiService
import com.chris.mobilewallet.db.AppDatabase
import com.chris.mobilewallet.domain.model.Account
import com.chris.mobilewallet.domain.model.Transfer
import com.chris.mobilewallet.domain.model.TransferState
import com.chris.mobilewallet.domain.repository.WalletRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class WalletRepositoryImpl(
    private val database: AppDatabase,
    private val apiService: ApiService,
    private val syncEngine: SyncEngine
) : WalletRepository {

    override fun getAccount(): Flow<Account?> {
        return database.appDatabaseQueries.getAccount()
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { entity ->
                entity?.let {
                    Account(
                        userId = it.userId,
                        phoneNumber = PhoneNumber(it.phoneNumber),
                        fullName = it.fullName,
                        balance = Money(it.balanceMinor, Currency.KES),
                        isBiometricEnabled = it.isBiometricEnabled == 1L,
                        pinHash = it.pinHash
                    )
                }
            }
    }

    override fun getBalance(): Flow<Money> {
        return getAccount().map { account ->
            account?.balance ?: Money(5123456L, Currency.KES)
        }
    }

    override fun getPendingOutboxTransfers(): Flow<List<Transfer>> {
        return flow {
            while (currentCoroutineContext().isActive) {
                val pending = try {
                    database.appDatabaseQueries.getAllOutboxItems().executeAsList().map { item ->
                        Transfer(
                            id = item.idempotencyKey,
                            idempotencyKey = item.idempotencyKey,
                            senderPhone = PhoneNumber("+254712345678"),
                            recipientPhone = PhoneNumber(item.recipientPhone),
                            amount = Money(item.amountMinor, Currency.KES),
                            fee = Transfer.calculateFee(Money(item.amountMinor, Currency.KES)),
                            state = TransferState.valueOf(item.status),
                            timestamp = item.createdAtTimestamp,
                            retryCount = item.retryCount.toInt()
                        )
                    }
                } catch (e: Exception) { emptyList() }
                emit(pending)
                delay(1000)
            }
        }
    }

    override fun getTransferHistory(): Flow<List<Transfer>> {
        return flow {
            while (currentCoroutineContext().isActive) {
                val pending = try {
                    database.appDatabaseQueries.getAllOutboxItems().executeAsList().map { item ->
                        Transfer(
                            id = item.idempotencyKey,
                            idempotencyKey = item.idempotencyKey,
                            senderPhone = PhoneNumber("+254712345678"),
                            recipientPhone = PhoneNumber(item.recipientPhone),
                            amount = Money(item.amountMinor, Currency.KES),
                            fee = Transfer.calculateFee(Money(item.amountMinor, Currency.KES)),
                            state = TransferState.valueOf(item.status),
                            timestamp = item.createdAtTimestamp,
                            retryCount = item.retryCount.toInt()
                        )
                    }
                } catch (e: Exception) { emptyList() }

                val confirmed = try {
                    database.appDatabaseQueries.getTransactions().executeAsList().map { tx ->
                        Transfer(
                            id = tx.id,
                            idempotencyKey = tx.idempotencyKey,
                            senderPhone = PhoneNumber(tx.senderPhone),
                            recipientPhone = PhoneNumber(tx.recipientPhone),
                            amount = Money(tx.amountMinor, Currency.valueOf(tx.currency)),
                            fee = Money(tx.feeMinor, Currency.valueOf(tx.currency)),
                            state = TransferState.valueOf(tx.state),
                            timestamp = tx.timestamp,
                            retryCount = tx.retryCount.toInt(),
                            lastError = tx.lastError
                        )
                    }
                } catch (e: Exception) { emptyList() }

                val combined = (pending + confirmed).distinctBy { it.id }.sortedByDescending { it.timestamp }
                emit(combined)
                delay(1000)
            }
        }
    }

    override suspend fun fetchRemoteBalance(): Result<Money> {
        val result = apiService.getBalance("mock_auth_token")
        return when (result) {
            is Result.Success -> {
                val remoteMoney = Money(result.data.balanceMinor, Currency.KES)
                database.appDatabaseQueries.updateBalance(remoteMoney.amountMinor, "usr_chris_njihia")
                Result.Success(remoteMoney)
            }
            is Result.Error -> result
            Result.Loading -> Result.Loading
        }
    }

    override suspend fun queueTransfer(
        recipientPhone: PhoneNumber,
        amount: Money,
        note: String
    ): Result<Transfer> {
        val idempotencyKey = "idemp_${System.currentTimeMillis()}_${Random.nextInt(1000, 9999)}"
        val timestamp = System.currentTimeMillis()

        try {
            database.appDatabaseQueries.insertOutboxItem(
                idempotencyKey = idempotencyKey,
                recipientPhone = recipientPhone.formattedE164,
                amountMinor = amount.amountMinor,
                note = note,
                createdAtTimestamp = timestamp,
                retryCount = 0L,
                lastAttemptTimestamp = 0L,
                status = "QUEUED"
            )
        } catch (ignored: Exception) {}

        val fee = Transfer.calculateFee(amount)
        val totalDeduction = amount + fee
        try {
            val currentAcc = database.appDatabaseQueries.getAccount().executeAsOneOrNull()
            if (currentAcc != null) {
                val newBal = currentAcc.balanceMinor - totalDeduction.amountMinor
                database.appDatabaseQueries.updateBalance(newBal, currentAcc.userId)
            }
        } catch (ignored: Exception) {}

        val transfer = Transfer(
            id = idempotencyKey,
            idempotencyKey = idempotencyKey,
            senderPhone = PhoneNumber("+254712345678"),
            recipientPhone = recipientPhone,
            amount = amount,
            fee = fee,
            state = TransferState.QUEUED,
            timestamp = timestamp
        )

        // Attempt background sync asynchronously so UI does not block on socket timeout
        CoroutineScope(Dispatchers.Default).launch {
            try {
                syncEngine.syncPendingTransfers()
            } catch (ignored: Exception) {}
        }

        return Result.Success(transfer)
    }

    override suspend fun syncPendingTransfers(): Result<Unit> {
        try {
            syncEngine.syncPendingTransfers()
        } catch (ignored: Exception) {}
        return Result.Success(Unit)
    }
}
