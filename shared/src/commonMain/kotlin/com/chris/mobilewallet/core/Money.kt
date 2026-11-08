// path: shared/src/commonMain/kotlin/com/chris/mobilewallet/core/Money.kt
package com.chris.mobilewallet.core

import kotlinx.serialization.Serializable

@Serializable
data class Money(
    val amountMinor: Long,
    val currency: Currency = Currency.KES
) : Comparable<Money> {

    init {
        require(amountMinor >= 0) { "Money amount cannot be negative" }
    }

    operator fun plus(other: Money): Money {
        require(currency == other.currency) { "Currency mismatch: $currency vs ${other.currency}" }
        return Money(amountMinor + other.amountMinor, currency)
    }

    operator fun minus(other: Money): Money {
        require(currency == other.currency) { "Currency mismatch: $currency vs ${other.currency}" }
        require(amountMinor >= other.amountMinor) { "Insufficient funds: $amountMinor < ${other.amountMinor}" }
        return Money(amountMinor - other.amountMinor, currency)
    }

    override fun compareTo(other: Money): Int {
        require(currency == other.currency) { "Currency mismatch: $currency vs ${other.currency}" }
        return amountMinor.compareTo(other.amountMinor)
    }

    fun formatFormattedString(): String {
        val major = amountMinor / currency.minorUnitsPerMajor
        val minor = amountMinor % currency.minorUnitsPerMajor
        val paddedMinor = minor.toString().padStart(2, '0')
        return "${currency.symbol} $major.$paddedMinor"
    }

    companion object {
        fun fromMajor(major: Long, currency: Currency = Currency.KES): Money {
            return Money(major * currency.minorUnitsPerMajor, currency)
        }

        fun zero(currency: Currency = Currency.KES): Money {
            return Money(0L, currency)
        }
    }
}
