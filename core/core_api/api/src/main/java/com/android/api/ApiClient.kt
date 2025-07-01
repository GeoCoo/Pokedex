package com.android.api

import com.android.core_model.PokemonListResponse

interface ApiClient {
    suspend fun getPokemons(limit:Int,offset:Int): Result<PokemonListResponse>
    suspend fun sampleWithParam(param: Any): Result<Any>
    suspend fun samplePost(param: Any): Result<Any>
    suspend fun samplePut(param: Any,update: Any): Result<Any>
}