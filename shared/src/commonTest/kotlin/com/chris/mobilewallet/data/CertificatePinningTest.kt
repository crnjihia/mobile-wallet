// path: shared/src/commonTest/kotlin/com/chris/mobilewallet/data/CertificatePinningTest.kt
package com.chris.mobilewallet.data

import com.chris.mobilewallet.data.network.ApiBalanceResponse
import com.chris.mobilewallet.data.network.ApiService
import com.chris.mobilewallet.data.network.HttpClientFactory
import com.chris.mobilewallet.core.Result
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CertificatePinningTest {

    @Test
    fun testMockEngineNetworkResponse() = runTest {
        val mockEngine = MockEngine { request ->
            respond(
                content = """{"phoneNumber":"+254712345678","currency":"KES","balanceMinor":5000000,"updatedAtTimestamp":1700000000000}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClientFactory.create(engine = mockEngine)
        val apiService = ApiService(client)

        val result = apiService.getBalance("valid_token")
        assertTrue(result is Result.Success)
        val data = (result as Result.Success<ApiBalanceResponse>).data
        assertEquals("+254712345678", data.phoneNumber)
        assertEquals(5000000L, data.balanceMinor)
    }
}
