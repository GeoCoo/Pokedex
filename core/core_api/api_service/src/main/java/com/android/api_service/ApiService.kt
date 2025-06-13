package com.android.api_service

import com.android.core_model.PokemonListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


const val baseUrl = "https://pokeapi.co/api/v2/"

interface ApiService {
    @GET("pokemon")
    suspend fun getPokemons(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Response<PokemonListResponse>



    @GET("sampleWithParam/{param}")
    suspend fun sampleRetrieveWithParam(
        @Path("param") productId: Any
    ): Response<Any>

    @POST("samplePost")
    suspend fun samplePost(
        @Body request: Any
    ): Response<Any>

    @PUT("samplePutWithParam/{param}")
    suspend fun samplePut(
        @Path("param") param: Any,
        @Body update: Any
    ): Response<Any>
}