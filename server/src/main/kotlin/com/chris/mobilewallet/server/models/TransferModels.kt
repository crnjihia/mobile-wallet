// path: server/src/main/kotlin/com/chris/mobilewallet/server/models/TransferModels.kt
package com.chris.mobilewallet.server.models

import kotlinx.serialization.Serializable

@Serializable
data class BalanceResponse(
    val phoneNumber: String,
    val currency: String = "KES",
    val balanceMinor: Long,
    val updatedAtTimestamp: Long
)

@Serializable
data class SendMoneyRequest(
    val idempotencyKey: String,
    val recipientPhone: String,
    val amountMinor: Long,
    val note: String = ""
)

@Serializable
data class TransferResponse(
    val id: String,
    val idempotencyKey: String,
    val senderPhone: String,
    val recipientPhone: String,
    val amountMinor: Long,
    val feeMinor: Long,
    val currency: String = "KES",
    val status: String,
    val timestamp: Long
)

@Serializable
data class HistoryResponse(
    val transactions: List<TransferResponse>
)

@Serializable
data class ChaosRequest(
    val enabled: Boolean,
    val failureRate: Double = 0.5,
    val delayMillis: Long = 0L
)

@Serializable
data class ChaosResponse(
    val enabled: Boolean,
    val failureRate: Double,
    val delayMillis: Long,
    val message: String
)
