// path: server/src/main/kotlin/com/chris/mobilewallet/server/ServerApp.kt
package com.chris.mobilewallet.server

import com.chris.mobilewallet.server.routes.authRoutes
import com.chris.mobilewallet.server.routes.chaosRoutes
import com.chris.mobilewallet.server.routes.walletRoutes
import com.chris.mobilewallet.server.store.InMemoryStore
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module(store: InMemoryStore = InMemoryStore()) {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        )
    }

    routing {
        authRoutes(store)
        walletRoutes(store)
        chaosRoutes(store)
    }
}
