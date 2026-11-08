// path: shared/src/commonMain/kotlin/com/chris/mobilewallet/core/Result.kt
package com.chris.mobilewallet.core

sealed interface Result<out T> {
    data class Success<out T>(val data: T) : Result<T>
    data class Error(val exception: Throwable, val message: String = exception.message ?: "Unknown error") : Result<Nothing>
    data object Loading : Result<Nothing>
}
