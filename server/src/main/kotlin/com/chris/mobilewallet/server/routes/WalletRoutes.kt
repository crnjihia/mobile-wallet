// path: server/src/main/kotlin/com/chris/mobilewallet/server/routes/WalletRoutes.kt
package com.chris.mobilewallet.server.routes

import com.chris.mobilewallet.server.models.BalanceResponse
import com.chris.mobilewallet.server.models.ErrorResponse
import com.chris.mobilewallet.server.models.HistoryResponse
import com.chris.mobilewallet.server.models.SendMoneyRequest
import com.chris.mobilewallet.server.store.InMemoryStore
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.header
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.coroutines.delay
import kotlin.random.Random

fun Route.walletRoutes(store: InMemoryStore) {
    route("/api/v1/wallet") {
        get("/balance") {
            val token = call.request.header("Authorization")?.removePrefix("Bearer ")
            if (token == null) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Missing authorization token", "UNAUTHORIZED"))
                return@get
            }
            val user = store.getUserByToken(token)
            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Invalid token", "UNAUTHORIZED"))
                return@get
            }
            call.respond(
                HttpStatusCode.OK,
                BalanceResponse(
                    phoneNumber = user.phoneNumber,
                    currency = "KES",
                    balanceMinor = user.balanceMinor,
                    updatedAtTimestamp = System.currentTimeMillis()
                )
            )
        }

        post("/send") {
            if (store.chaosEnabled) {
                if (store.chaosDelayMillis > 0) {
                    delay(store.chaosDelayMillis)
                }
                if (Random.nextDouble() < store.chaosFailureRate) {
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Simulated chaos network/server failure", "CHAOS_FAILURE"))
                    return@post
                }
            }

            val token = call.request.header("Authorization")?.removePrefix("Bearer ")
            if (token == null) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Missing authorization token", "UNAUTHORIZED"))
                return@post
            }
            val user = store.getUserByToken(token)
            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Invalid token", "UNAUTHORIZED"))
                return@post
            }

            val headerIdempotencyKey = call.request.header("X-Idempotency-Key")
            try {
                val req = call.receive<SendMoneyRequest>()
                val idempotencyKey = headerIdempotencyKey ?: req.idempotencyKey
                if (idempotencyKey.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Idempotency key required", "MISSING_IDEMPOTENCY_KEY"))
                    return@post
                }

                val transfer = store.processTransfer(
                    sender = user,
                    idempotencyKey = idempotencyKey,
                    recipientPhone = req.recipientPhone,
                    amountMinor = req.amountMinor
                )
                call.respond(HttpStatusCode.OK, transfer)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(e.message ?: "Invalid transfer request", "INVALID_TRANSFER"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Server processing error", "SERVER_ERROR"))
            }
        }

        get("/history") {
            val token = call.request.header("Authorization")?.removePrefix("Bearer ")
            if (token == null) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Missing authorization token", "UNAUTHORIZED"))
                return@get
            }
            val user = store.getUserByToken(token)
            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Invalid token", "UNAUTHORIZED"))
                return@get
            }

            val history = store.getTransactionHistory(user.phoneNumber)
            call.respond(HttpStatusCode.OK, HistoryResponse(transactions = history))
        }
    }
}
