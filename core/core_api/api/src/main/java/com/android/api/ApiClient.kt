package com.android.api

import com.android.core_model.PokemonListResponse
import retrofit2.Response

interface ApiClient {
    suspend fun getPokemons(limit:Int,offset:Int): Response<PokemonListResponse>
    suspend fun sampleWithParam(param: Any): Response<Any>
    suspend fun samplePost(param: Any): Response<Any>
    suspend fun samplePut(param: Any,update: Any): Response<Any>
}