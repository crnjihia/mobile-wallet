// path: server/src/main/kotlin/com/chris/mobilewallet/server/store/InMemoryStore.kt
package com.chris.mobilewallet.server.store

import com.chris.mobilewallet.server.models.TransferResponse
import java.util.concurrent.ConcurrentHashMap

data class UserAccount(
    val userId: String,
    val phoneNumber: String,
    val fullName: String,
    val pin: String,
    var balanceMinor: Long = 50_000_00 // 50,000 KES default initial balance
)

class InMemoryStore {
    private val usersByPhone = ConcurrentHashMap<String, UserAccount>()
    private val usersByToken = ConcurrentHashMap<String, UserAccount>()
    private val transactions = ConcurrentHashMap<String, TransferResponse>()
    private val idempotencyCache = ConcurrentHashMap<String, TransferResponse>()

    @Volatile
    var chaosEnabled: Boolean = false
    @Volatile
    var chaosFailureRate: Double = 0.5
    @Volatile
    var chaosDelayMillis: Long = 0L

    fun registerUser(phoneNumber: String, fullName: String, pin: String): UserAccount {
        require(phoneNumber.matches(Regex("^\\+254[17]\\d{8}$"))) { "Invalid Kenyan phone number format" }
        require(pin.length == 4 && pin.all { it.isDigit() }) { "PIN must be 4 digits" }
        if (usersByPhone.containsKey(phoneNumber)) {
            throw IllegalArgumentException("Phone number already registered")
        }
        val userId = "usr_${System.currentTimeMillis()}"
        val user = UserAccount(userId = userId, phoneNumber = phoneNumber, fullName = fullName, pin = pin)
        usersByPhone[phoneNumber] = user
        return user
    }

    fun loginUser(phoneNumber: String, pin: String): Pair<UserAccount, String>? {
        val user = usersByPhone[phoneNumber] ?: return null
        if (user.pin != pin) return null
        val token = "token_${user.userId}_${System.currentTimeMillis()}"
        usersByToken[token] = user
        return Pair(user, token)
    }

    fun getUserByToken(token: String): UserAccount? {
        return usersByToken[token]
    }

    fun getIdempotentTransfer(key: String): TransferResponse? {
        return idempotencyCache[key]
    }

    fun processTransfer(
        sender: UserAccount,
        idempotencyKey: String,
        recipientPhone: String,
        amountMinor: Long
    ): TransferResponse {
        idempotencyCache[idempotencyKey]?.let { cached ->
            return cached
        }

        require(recipientPhone.matches(Regex("^\\+254[17]\\d{8}$"))) { "Invalid recipient phone number" }
        require(amountMinor > 0) { "Transfer amount must be positive" }
        require(sender.balanceMinor >= amountMinor) { "Insufficient wallet balance" }

        sender.balanceMinor -= amountMinor

        usersByPhone[recipientPhone]?.let { recipient ->
            recipient.balanceMinor += amountMinor
        }

        val transferId = "tx_${System.currentTimeMillis()}"
        val response = TransferResponse(
            id = transferId,
            idempotencyKey = idempotencyKey,
            senderPhone = sender.phoneNumber,
            recipientPhone = recipientPhone,
            amountMinor = amountMinor,
            feeMinor = 0L,
            currency = "KES",
            status = "SUCCEEDED",
            timestamp = System.currentTimeMillis()
        )

        transactions[transferId] = response
        idempotencyCache[idempotencyKey] = response
        return response
    }

    fun getTransactionHistory(phoneNumber: String): List<TransferResponse> {
        return transactions.values.filter {
            it.senderPhone == phoneNumber || it.recipientPhone == phoneNumber
        }.sortedByDescending { it.timestamp }
    }
}
