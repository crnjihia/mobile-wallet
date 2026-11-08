// path: shared/src/commonMain/kotlin/com/chris/mobilewallet/data/repository/SyncEngine.kt
package com.chris.mobilewallet.data.repository

import com.chris.mobilewallet.core.Result
import com.chris.mobilewallet.data.network.ApiSendMoneyRequest
import com.chris.mobilewallet.data.network.ApiService
import com.chris.mobilewallet.db.AppDatabase
import com.chris.mobilewallet.domain.model.TransferState
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

class SyncEngine(
    private val database: AppDatabase?,
    private val apiService: ApiService,
    private var authTokenSupplier: () -> String = { "mock_auth_token" }
) {
    suspend fun syncPendingTransfers(): Result<Int> {
        val db = database ?: return Result.Success(0)
        val pendingItems = db.appDatabaseQueries.getPendingOutboxItems().executeAsList()
        var syncedCount = 0

        for (item in pendingItems) {
            db.appDatabaseQueries.updateOutboxStatus(
                status = "SENDING",
                retryCount = item.retryCount,
                lastAttemptTimestamp = System.currentTimeMillis(),
                idempotencyKey = item.idempotencyKey
            )

            val request = ApiSendMoneyRequest(
                idempotencyKey = item.idempotencyKey,
                recipientPhone = item.recipientPhone,
                amountMinor = item.amountMinor,
                note = item.note
            )

            val result = apiService.sendMoney(token = authTokenSupplier(), request = request)

            when (result) {
                is Result.Success -> {
                    db.appDatabaseQueries.insertTransaction(
                        id = result.data.id,
                        idempotencyKey = result.data.idempotencyKey,
                        senderPhone = result.data.senderPhone,
                        recipientPhone = result.data.recipientPhone,
                        amountMinor = result.data.amountMinor,
                        feeMinor = result.data.feeMinor,
                        currency = result.data.currency,
                        state = TransferState.SUCCEEDED.name,
                        timestamp = result.data.timestamp,
                        retryCount = item.retryCount.toLong(),
                        lastError = null
                    )
                    db.appDatabaseQueries.deleteOutboxItem(item.idempotencyKey)
                    syncedCount++
                }
                is Result.Error -> {
                    val nextRetry = item.retryCount + 1
                    if (nextRetry >= MAX_RETRIES) {
                        db.appDatabaseQueries.updateOutboxStatus(
                            status = "FAILED",
                            retryCount = nextRetry.toLong(),
                            lastAttemptTimestamp = System.currentTimeMillis(),
                            idempotencyKey = item.idempotencyKey
                        )
                    } else {
                        db.appDatabaseQueries.updateOutboxStatus(
                            status = "RETRYING",
                            retryCount = nextRetry.toLong(),
                            lastAttemptTimestamp = System.currentTimeMillis(),
                            idempotencyKey = item.idempotencyKey
                        )
                    }
                }
                Result.Loading -> {}
            }
        }
        return Result.Success(syncedCount)
    }

    fun calculateBackoffDelayMillis(retryCount: Int): Long {
        val baseDelay = 1000.0
        val maxDelay = 30000.0
        val expDelay = min(maxDelay, baseDelay * 2.0.pow(retryCount.toDouble()))
        val jitter = Random.nextDouble(0.8, 1.2)
        return (expDelay * jitter).toLong()
    }

    companion object {
        const val MAX_RETRIES = 5
    }
}
