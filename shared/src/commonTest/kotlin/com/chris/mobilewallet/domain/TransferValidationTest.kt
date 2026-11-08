// path: shared/src/commonTest/kotlin/com/chris/mobilewallet/domain/TransferValidationTest.kt
package com.chris.mobilewallet.domain

import com.chris.mobilewallet.core.Money
import com.chris.mobilewallet.domain.model.Transfer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TransferValidationTest {

    @Test
    fun testFeeCalculation() {
        val amount1 = Money.fromMajor(100) // 100 KES -> Fee 0
        assertEquals(0L, Transfer.calculateFee(amount1).amountMinor)

        val amount2 = Money.fromMajor(1000) // 1000 KES -> Fee 12 KES
        assertEquals(12_00L, Transfer.calculateFee(amount2).amountMinor)

        val amount3 = Money.fromMajor(10000) // 10,000 KES -> Fee 50 KES
        assertEquals(50_00L, Transfer.calculateFee(amount3).amountMinor)
    }

    @Test
    fun testTransferValidationRules() {
        val balance = Money.fromMajor(5000)

        // Valid transfer
        val validErr = Transfer.validateTransfer(balance, Money.fromMajor(1000))
        assertNull(validErr)

        // Amount below minimum
        val lowErr = Transfer.validateTransfer(balance, Money(500L)) // 5 KES < 10 KES
        assertNotNull(lowErr)

        // Amount exceeds CBK single transaction limit
        val highErr = Transfer.validateTransfer(Money.fromMajor(200000), Money.fromMajor(160000))
        assertNotNull(highErr)

        // Exceeds balance including fee (500 KES amount + 6 KES fee = 506 KES > 500 KES balance)
        val insufficientErr = Transfer.validateTransfer(Money.fromMajor(500), Money.fromMajor(500))
        assertNotNull(insufficientErr)
    }
}
