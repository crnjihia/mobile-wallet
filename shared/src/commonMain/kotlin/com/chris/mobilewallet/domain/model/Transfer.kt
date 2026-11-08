// path: shared/src/commonMain/kotlin/com/chris/mobilewallet/domain/model/Transfer.kt
package com.chris.mobilewallet.domain.model

import com.chris.mobilewallet.core.Money
import com.chris.mobilewallet.core.PhoneNumber

data class Transfer(
    val id: String,
    val idempotencyKey: String,
    val senderPhone: PhoneNumber,
    val recipientPhone: PhoneNumber,
    val amount: Money,
    val fee: Money,
    val state: TransferState,
    val timestamp: Long,
    val retryCount: Int = 0,
    val lastError: String? = null
) {
    companion object {
        const val MAX_SINGLE_TRANSFER_MINOR = 150_000_00L // 150,000 KES Central Bank of Kenya / M-Pesa limit
        const val MIN_TRANSFER_MINOR = 10_00L // 10 KES minimum limit

        fun calculateFee(amount: Money): Money {
            val amountMinor = amount.amountMinor
            val feeMinor = when {
                amountMinor <= 100_00 -> 0L
                amountMinor <= 500_00 -> 6_00L
                amountMinor <= 1_000_00 -> 12_00L
                amountMinor <= 5_000_00 -> 30_00L
                else -> 50_00L
            }
            return Money(feeMinor, amount.currency)
        }

        fun validateTransfer(senderBalance: Money, amount: Money): String? {
            if (amount.amountMinor < MIN_TRANSFER_MINOR) {
                return "Minimum transfer amount is KSh 10.00"
            }
            if (amount.amountMinor > MAX_SINGLE_TRANSFER_MINOR) {
                return "Maximum single transfer limit is KSh 150,000.00"
            }
            val fee = calculateFee(amount)
            val totalDeduction = amount + fee
            if (senderBalance.amountMinor < totalDeduction.amountMinor) {
                return "Insufficient balance (Total required including fee: ${totalDeduction.formatFormattedString()})"
            }
            return null
        }
    }
}
