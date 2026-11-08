// path: server/src/main/kotlin/com/chris/mobilewallet/server/routes/AuthRoutes.kt
package com.chris.mobilewallet.server.routes

import com.chris.mobilewallet.server.models.AuthResponse
import com.chris.mobilewallet.server.models.ErrorResponse
import com.chris.mobilewallet.server.models.LoginRequest
import com.chris.mobilewallet.server.models.RegisterRequest
import com.chris.mobilewallet.server.store.InMemoryStore
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes(store: InMemoryStore) {
    route("/api/v1/auth") {
        post("/register") {
            try {
                val req = call.receive<RegisterRequest>()
                val user = store.registerUser(req.phoneNumber, req.fullName, req.pin)
                val (_, token) = store.loginUser(req.phoneNumber, req.pin)!!
                call.respond(
                    HttpStatusCode.Created,
                    AuthResponse(
                        token = token,
                        userId = user.userId,
                        phoneNumber = user.phoneNumber,
                        fullName = user.fullName
                    )
                )
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(error = e.message ?: "Bad Request", code = "INVALID_INPUT"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse(error = "Internal server error", code = "SERVER_ERROR"))
            }
        }

        post("/login") {
            try {
                val req = call.receive<LoginRequest>()
                val result = store.loginUser(req.phoneNumber, req.pin)
                if (result == null) {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse(error = "Invalid phone number or PIN", code = "UNAUTHORIZED"))
                } else {
                    val (user, token) = result
                    call.respond(
                        HttpStatusCode.OK,
                        AuthResponse(
                            token = token,
                            userId = user.userId,
                            phoneNumber = user.phoneNumber,
                            fullName = user.fullName
                        )
                    )
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse(error = "Internal server error", code = "SERVER_ERROR"))
            }
        }
    }
}
