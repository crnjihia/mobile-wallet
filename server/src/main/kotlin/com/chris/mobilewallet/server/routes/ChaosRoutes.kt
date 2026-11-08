// path: server/src/main/kotlin/com/chris/mobilewallet/server/routes/ChaosRoutes.kt
package com.chris.mobilewallet.server.routes

import com.chris.mobilewallet.server.models.ChaosRequest
import com.chris.mobilewallet.server.models.ChaosResponse
import com.chris.mobilewallet.server.store.InMemoryStore
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.chaosRoutes(store: InMemoryStore) {
    route("/api/v1/chaos") {
        post {
            val req = call.receive<ChaosRequest>()
            store.chaosEnabled = req.enabled
            store.chaosFailureRate = req.failureRate
            store.chaosDelayMillis = req.delayMillis
            val state = if (req.enabled) "enabled (failureRate=${req.failureRate}, delay=${req.delayMillis}ms)" else "disabled"
            call.respond(
                HttpStatusCode.OK,
                ChaosResponse(
                    enabled = store.chaosEnabled,
                    failureRate = store.chaosFailureRate,
                    delayMillis = store.chaosDelayMillis,
                    message = "Chaos simulation mode $state"
                )
            )
        }
    }
}
