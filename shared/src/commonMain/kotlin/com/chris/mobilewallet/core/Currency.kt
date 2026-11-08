// path: shared/src/commonMain/kotlin/com/chris/mobilewallet/core/Currency.kt
package com.chris.mobilewallet.core

enum class Currency(val code: String, val symbol: String, val minorUnitsPerMajor: Int) {
    KES("KES", "KSh", 100),
    USD("USD", "$", 100)
}
