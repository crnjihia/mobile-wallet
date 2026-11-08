// path: shared/src/commonMain/kotlin/com/chris/mobilewallet/core/PhoneNumber.kt
package com.chris.mobilewallet.core

import kotlinx.serialization.Serializable

@Serializable
data class PhoneNumber(val rawValue: String) {

    val formattedE164: String

    init {
        val cleaned = rawValue.replace("\\s|-".toRegex(), "")
        val normalized = when {
            cleaned.startsWith("+254") -> cleaned
            cleaned.startsWith("254") -> "+$cleaned"
            cleaned.startsWith("07") || cleaned.startsWith("01") -> "+254${cleaned.drop(1)}"
            else -> throw IllegalArgumentException("Invalid Kenyan phone number format: $rawValue")
        }

        require(normalized.matches(Regex("^\\+254[17]\\d{8}$"))) {
            "Phone number must be a valid Kenyan Safaricom/Airtel/Telkom number (+2547XXXXXXXX or +2541XXXXXXXX)"
        }
        formattedE164 = normalized
    }

    override fun toString(): String = formattedE164
}
