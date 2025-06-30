package com.android.impl

import com.android.api.ApiClient
import com.android.api_service.ApiService
import com.android.core_model.PokemonListResponse
import com.android.core_model.SinglePokemonDto
import retrofit2.Response
import javax.inject.Inject

class ApiClientImpl @Inject constructor(private val apiService: ApiService) : ApiClient {
    override suspend fun getPokemons(limit: Int, offset: Int): Response<PokemonListResponse> =
        apiService.getPokemons(limit, offset)

    override suspend fun getSinglePokemon(pokemonName: String): Response<SinglePokemonDto> =
        apiService.getSinglePokemon(pokemonName)

    override suspend fun samplePost(param: Any): Response<Any> =
        apiService.samplePost(param)

    override suspend fun samplePut(
        param: Any,
        update: Any
    ): Response<Any> = apiService.samplePut(param, update)
}