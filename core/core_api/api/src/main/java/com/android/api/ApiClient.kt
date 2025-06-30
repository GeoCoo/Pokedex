package com.android.api

import com.android.core_model.PokemonListResponse
import com.android.core_model.SinglePokemonDto
import retrofit2.Response

interface ApiClient {
    suspend fun getPokemons(limit:Int,offset:Int): Response<PokemonListResponse>
    suspend fun getSinglePokemon(pokemonName: String): Response<SinglePokemonDto>
    suspend fun samplePost(param: Any): Response<Any>
    suspend fun samplePut(param: Any,update: Any): Response<Any>
}