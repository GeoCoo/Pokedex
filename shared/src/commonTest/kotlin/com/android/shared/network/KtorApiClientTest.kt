package com.android.shared.network

import kotlin.test.Test
import kotlin.test.assertNotNull

class KtorApiClientTest {
    
    @Test
    fun testHttpClientCreation() {
        val httpClient = createHttpClient()
        assertNotNull(httpClient)
    }
    
    @Test
    fun testKtorApiClientCreation() {
        val httpClient = createHttpClient()
        val apiClient = KtorApiClient(httpClient)
        assertNotNull(apiClient)
    }
}