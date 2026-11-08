// path: server/src/main/kotlin/com/chris/mobilewallet/server/models/UserModels.kt
package com.chris.mobilewallet.server.models

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val phoneNumber: String,
    val fullName: String,
    val pin: String
)

@Serializable
data class LoginRequest(
    val phoneNumber: String,
    val pin: String
)

@Serializable
data class AuthResponse(
    val token: String,
    val userId: String,
    val phoneNumber: String,
    val fullName: String
)

@Serializable
data class ErrorResponse(
    val error: String,
    val code: String
)
