package com.android.shared.network

import com.android.shared.model.PokemonListResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class KtorApiClient(private val httpClient: HttpClient) {
    
    companion object {
        private const val BASE_URL = "https://pokeapi.co/api/v2/"
    }
    
    suspend fun getPokemons(limit: Int, offset: Int): Result<PokemonListResponse> {
        return try {
            val response = httpClient.get("${BASE_URL}pokemon") {
                parameter("limit", limit)
                parameter("offset", offset)
            }
            Result.success(response.body<PokemonListResponse>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun sampleWithParam(param: Any): Result<Any> {
        return try {
            val response = httpClient.get("${BASE_URL}sampleWithParam/$param")
            Result.success(response.body<Any>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun samplePost(param: Any): Result<Any> {
        return try {
            val response = httpClient.post("${BASE_URL}samplePost") {
                setBody(param)
            }
            Result.success(response.body<Any>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun samplePut(param: Any, update: Any): Result<Any> {
        return try {
            val response = httpClient.put("${BASE_URL}samplePutWithParam/$param") {
                setBody(update)
            }
            Result.success(response.body<Any>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}