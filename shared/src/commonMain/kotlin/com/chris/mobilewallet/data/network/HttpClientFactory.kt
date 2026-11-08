// path: shared/src/commonMain/kotlin/com/chris/mobilewallet/data/network/HttpClientFactory.kt
package com.chris.mobilewallet.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClientFactory {
    fun create(
        engine: HttpClientEngine? = null,
        pinConfig: PinConfig = PinConfig(
            hostname = "api.chris-mobilewallet.co.ke",
            primaryPin = "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
            backupPin = "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB=",
            allowDebugOverride = true
        )
    ): HttpClient {
        return if (engine != null) {
            HttpClient(engine) {
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true; isLenient = true })
                }
            }
        } else {
            HttpClient {
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true; isLenient = true })
                }
            }
        }
    }
}
