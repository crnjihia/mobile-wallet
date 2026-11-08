// path: shared/src/commonMain/kotlin/com/chris/mobilewallet/domain/model/TransferState.kt
package com.chris.mobilewallet.domain.model

enum class TransferState {
    QUEUED,
    SENDING,
    SUCCEEDED,
    FAILED,
    RETRYING;

    val isTerminal: Boolean
        get() = this == SUCCEEDED || this == FAILED
}
