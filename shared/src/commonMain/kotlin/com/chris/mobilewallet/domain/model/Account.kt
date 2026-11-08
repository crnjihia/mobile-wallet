// path: shared/src/commonMain/kotlin/com/chris/mobilewallet/domain/model/Account.kt
package com.chris.mobilewallet.domain.model

import com.chris.mobilewallet.core.Money
import com.chris.mobilewallet.core.PhoneNumber

data class Account(
    val userId: String,
    val phoneNumber: PhoneNumber,
    val fullName: String,
    val balance: Money,
    val isBiometricEnabled: Boolean = false,
    val pinHash: String
)
