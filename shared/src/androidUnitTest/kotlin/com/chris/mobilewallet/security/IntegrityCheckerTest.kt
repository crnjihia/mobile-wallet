// path: shared/src/androidUnitTest/kotlin/com/chris/mobilewallet/security/IntegrityCheckerTest.kt
package com.chris.mobilewallet.security

import kotlin.test.Test
import kotlin.test.assertNotNull

class IntegrityCheckerTest {

    @Test
    fun testIntegrityEvaluation() {
        val checker = IntegrityChecker()
        val report = checker.evaluateIntegrity(IntegrityPolicy.WARN_ONLY)
        assertNotNull(report)
        assertNotNull(report.signalsDetected)
    }
}
