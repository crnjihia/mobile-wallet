// path: shared/src/commonTest/kotlin/com/chris/mobilewallet/data/SyncEngineTest.kt
package com.chris.mobilewallet.data

import com.chris.mobilewallet.data.network.ApiService
import com.chris.mobilewallet.data.network.HttpClientFactory
import com.chris.mobilewallet.data.repository.SyncEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertTrue

class SyncEngineTest {

    @Test
    fun testExponentialBackoffCalculation() {
        val mockEngine = MockEngine { respond("", HttpStatusCode.OK) }
        val client = HttpClientFactory.create(engine = mockEngine)
        val apiService = ApiService(client)

        val syncEngine = SyncEngine(
            database = null,
            apiService = apiService
        )

        val delay0 = syncEngine.calculateBackoffDelayMillis(0)
        assertTrue(delay0 in 800..1200)

        val delay2 = syncEngine.calculateBackoffDelayMillis(2)
        assertTrue(delay2 in 3200..4800)
    }
}
