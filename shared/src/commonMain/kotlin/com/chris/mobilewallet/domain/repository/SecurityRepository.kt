// path: shared/src/commonMain/kotlin/com/chris/mobilewallet/domain/repository/SecurityRepository.kt
package com.chris.mobilewallet.domain.repository

import kotlinx.coroutines.flow.Flow

interface SecurityRepository {
    fun isRootedOrJailbroken(): Boolean
    fun isBiometricAvailable(): Boolean
    fun isLocked(): Flow<Boolean>

    suspend fun setAutoLockTimeout(seconds: Long)
    suspend fun lockWallet()
    suspend fun unlockWalletWithPin(pin: String): Boolean
}
