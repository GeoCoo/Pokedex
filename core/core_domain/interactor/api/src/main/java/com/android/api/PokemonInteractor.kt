package com.android.api

import com.android.model.PokemonDomain
import com.android.model.SinglePokemonDomain
import kotlinx.coroutines.flow.Flow

interface PokemonInteractor {
    suspend fun getPokemons(limit:Int,offset:Int): Flow<PokemonPartialState>

    suspend fun getSinglePokemon(pokemonName: String): Flow<SinglePokemonPartialState>
//    suspend fun sampleUpdate(
//        param: Any,
//        update: Any
//    ): Flow<SamplePartialState>
}




sealed class PokemonPartialState {
    data class Success(val pokemons: List<PokemonDomain>?) : PokemonPartialState()
    data class Failed(val errorMessage: String) : PokemonPartialState()
    data class Error(val errorMessage: String) : PokemonPartialState()
}

sealed class SinglePokemonPartialState {
    data class Success(val singlePokemon: SinglePokemonDomain?) : SinglePokemonPartialState()
    data class Failed(val errorMessage: String) : SinglePokemonPartialState()
    data class Error(val errorMessage: String) : SinglePokemonPartialState()
}

