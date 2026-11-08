// path: server/src/test/kotlin/com/chris/mobilewallet/server/ServerRoutesTest.kt
package com.chris.mobilewallet.server

import com.chris.mobilewallet.server.models.AuthResponse
import com.chris.mobilewallet.server.models.BalanceResponse
import com.chris.mobilewallet.server.models.ChaosRequest
import com.chris.mobilewallet.server.models.LoginRequest
import com.chris.mobilewallet.server.models.RegisterRequest
import com.chris.mobilewallet.server.models.SendMoneyRequest
import com.chris.mobilewallet.server.models.TransferResponse
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ServerRoutesTest {

    @Test
    fun testUserRegistrationAndLogin() = testApplication {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }

        val regResponse = client.post("/api/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest(phoneNumber = "+254712345678", fullName = "Chris Nairobi", pin = "1234"))
        }
        assertEquals(HttpStatusCode.Created, regResponse.status)
        val authData = regResponse.body<AuthResponse>()
        assertEquals("+254712345678", authData.phoneNumber)

        val loginResponse = client.post("/api/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(phoneNumber = "+254712345678", pin = "1234"))
        }
        assertEquals(HttpStatusCode.OK, loginResponse.status)
    }

    @Test
    fun testWalletBalanceAndIdempotentTransfer() = testApplication {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }

        val reg = client.post("/api/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest(phoneNumber = "+254711111111", fullName = "Sender User", pin = "1234"))
        }.body<AuthResponse>()

        val balanceResp = client.get("/api/v1/wallet/balance") {
            header("Authorization", "Bearer ${reg.token}")
        }
        assertEquals(HttpStatusCode.OK, balanceResp.status)
        val balance = balanceResp.body<BalanceResponse>()
        assertEquals(50_000_00L, balance.balanceMinor)

        val idempotencyKey = "key_test_12345"
        val sendReq = SendMoneyRequest(
            idempotencyKey = idempotencyKey,
            recipientPhone = "+254722222222",
            amountMinor = 1_000_00L
        )

        val firstSend = client.post("/api/v1/wallet/send") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${reg.token}")
            setBody(sendReq)
        }
        assertEquals(HttpStatusCode.OK, firstSend.status)
        val firstTx = firstSend.body<TransferResponse>()
        assertEquals("SUCCEEDED", firstTx.status)

        val secondSend = client.post("/api/v1/wallet/send") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${reg.token}")
            setBody(sendReq)
        }
        assertEquals(HttpStatusCode.OK, secondSend.status)
        val secondTx = secondSend.body<TransferResponse>()
        assertEquals(firstTx.id, secondTx.id, "Idempotency key must return the exact same transaction ID")
    }

    @Test
    fun testChaosToggle() = testApplication {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }

        val chaosResp = client.post("/api/v1/chaos") {
            contentType(ContentType.Application.Json)
            setBody(ChaosRequest(enabled = true, failureRate = 1.0, delayMillis = 10))
        }
        assertEquals(HttpStatusCode.OK, chaosResp.status)
    }
}
