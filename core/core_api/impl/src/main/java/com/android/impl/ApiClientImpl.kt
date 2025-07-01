package com.android.impl

import com.android.api.ApiClient
import com.android.core_model.PokemonListResponse
import com.android.shared.network.KtorApiClient
import javax.inject.Inject

class ApiClientImpl @Inject constructor(private val ktorApiClient: KtorApiClient) : ApiClient {
    override suspend fun getPokemons(limit:Int,offset:Int): Result<PokemonListResponse> =
        ktorApiClient.getPokemons(limit, offset)

    override suspend fun sampleWithParam(param: Any): Result<Any> =
        ktorApiClient.sampleWithParam(param)

    override suspend fun samplePost(param: Any): Result<Any> =
        ktorApiClient.samplePost(param)

    override suspend fun samplePut(
        param: Any,
        update: Any
    ): Result<Any> = ktorApiClient.samplePut(param, update)
}