// path: shared/src/commonTest/kotlin/com/chris/mobilewallet/domain/MoneyTest.kt
package com.chris.mobilewallet.domain

import com.chris.mobilewallet.core.Currency
import com.chris.mobilewallet.core.Money
import com.chris.mobilewallet.core.PhoneNumber
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MoneyTest {

    @Test
    fun testMoneyFormattingAndOperations() {
        val m1 = Money(2500_50L, Currency.KES)
        assertEquals("KSh 2500.50", m1.formatFormattedString())

        val m2 = Money(1000_00L, Currency.KES)
        val sum = m1 + m2
        assertEquals(3500_50L, sum.amountMinor)
        assertEquals("KSh 3500.50", sum.formatFormattedString())

        val diff = m1 - m2
        assertEquals(1500_50L, diff.amountMinor)
    }

    @Test
    fun testNegativeMoneyDisallowed() {
        assertFailsWith<IllegalArgumentException> {
            Money(-100L)
        }
    }

    @Test
    fun testKenyanPhoneNumberValidation() {
        val p1 = PhoneNumber("0712345678")
        assertEquals("+254712345678", p1.formattedE164)

        val p2 = PhoneNumber("+254112345678")
        assertEquals("+254112345678", p2.formattedE164)

        assertFailsWith<IllegalArgumentException> {
            PhoneNumber("0201234567")
        }
    }
}
