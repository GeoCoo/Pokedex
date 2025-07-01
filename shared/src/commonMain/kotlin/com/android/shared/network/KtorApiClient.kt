package com.android.shared.network

import com.android.shared.model.PokemonListResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.SerializationException

class KtorApiClient(private val httpClient: HttpClient) {
    
    companion object {
        private const val BASE_URL = "https://pokeapi.co/api/v2/"
    }
    
    suspend fun getPokemons(limit: Int, offset: Int): Result<PokemonListResponse> {
        return safeApiCall {
            httpClient.get("${BASE_URL}pokemon") {
                parameter("limit", limit)
                parameter("offset", offset)
            }
        }
    }
    
    suspend fun sampleWithParam(param: Any): Result<Any> {
        return safeApiCall {
            httpClient.get("${BASE_URL}sampleWithParam/$param")
        }
    }
    
    suspend fun samplePost(param: Any): Result<Any> {
        return safeApiCall {
            httpClient.post("${BASE_URL}samplePost") {
                contentType(ContentType.Application.Json)
                setBody(param)
            }
        }
    }
    
    suspend fun samplePut(param: Any, update: Any): Result<Any> {
        return safeApiCall {
            httpClient.put("${BASE_URL}samplePutWithParam/$param") {
                contentType(ContentType.Application.Json)
                setBody(update)
            }
        }
    }
    
    private suspend inline fun <reified T> safeApiCall(
        crossinline apiCall: suspend () -> HttpResponse
    ): Result<T> {
        return try {
            val response = apiCall()
            when {
                response.status.isSuccess() -> {
                    Result.success(response.body<T>())
                }
                else -> {
                    Result.failure(
                        NetworkError.HttpError(
                            response.status.value,
                            response.status.description
                        )
                    )
                }
            }
        } catch (e: SerializationException) {
            Result.failure(NetworkError.SerializationError("Failed to parse response", e))
        } catch (e: Exception) {
            Result.failure(NetworkError.NetworkException("Network request failed", e))
        }
    }
}