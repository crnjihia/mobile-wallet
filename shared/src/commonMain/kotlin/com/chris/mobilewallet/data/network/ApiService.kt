// path: shared/src/commonMain/kotlin/com/chris/mobilewallet/data/network/ApiService.kt
package com.chris.mobilewallet.data.network

import com.chris.mobilewallet.core.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

@Serializable
data class ApiBalanceResponse(
    val phoneNumber: String,
    val currency: String,
    val balanceMinor: Long,
    val updatedAtTimestamp: Long
)

@Serializable
data class ApiSendMoneyRequest(
    val idempotencyKey: String,
    val recipientPhone: String,
    val amountMinor: Long,
    val note: String = ""
)

@Serializable
data class ApiTransferResponse(
    val id: String,
    val idempotencyKey: String,
    val senderPhone: String,
    val recipientPhone: String,
    val amountMinor: Long,
    val feeMinor: Long,
    val currency: String,
    val status: String,
    val timestamp: Long
)

class ApiService(
    private val client: HttpClient,
    private val baseUrl: String = "http://10.0.2.2:8080"
) {
    suspend fun getBalance(token: String): Result<ApiBalanceResponse> {
        return try {
            val response = client.get("$baseUrl/api/v1/wallet/balance") {
                header("Authorization", "Bearer $token")
            }.body<ApiBalanceResponse>()
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e, e.message ?: "Network error fetching balance")
        }
    }

    suspend fun sendMoney(token: String, request: ApiSendMoneyRequest): Result<ApiTransferResponse> {
        return try {
            val response = client.post("$baseUrl/api/v1/wallet/send") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                header("X-Idempotency-Key", request.idempotencyKey)
                setBody(request)
            }.body<ApiTransferResponse>()
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e, e.message ?: "Network error sending money")
        }
    }
}
