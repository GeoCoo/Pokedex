package com.android.api


import com.android.core_model.PokemonListResponse
import com.android.core_model.SinglePokemonDto
import kotlinx.coroutines.flow.Flow


interface PokemonsRepository {
    fun getPokemons(limit: Int, offset: Int): Flow<PokemonsResponse>
    fun getSinglePokemon(pokemonName: String): Flow<SinglePokemonRespone>
//    fun samplePost(param:Any): Flow<PokemonsResponse>
//    fun samplePut(param: Any, update: Any): Flow<PokemonsResponse>
}


sealed class PokemonsResponse {
    data class Success(val pokemonsResponse: PokemonListResponse?) : PokemonsResponse()
    data class Failed(val errorMsg: Any) : PokemonsResponse()
    data class Error(val errorMsg: Any) : PokemonsResponse()
}

sealed class SinglePokemonRespone{
    data class Success(val singlePokemon: SinglePokemonDto?) : SinglePokemonRespone()
    data class Failed(val errorMsg: Any) : SinglePokemonRespone()
    data class Error(val errorMsg: Any) : SinglePokemonRespone()
}