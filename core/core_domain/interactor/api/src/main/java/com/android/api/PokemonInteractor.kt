package com.android.api

import com.android.model.PokemonDomain
import kotlinx.coroutines.flow.Flow

interface PokemonInteractor {
    suspend fun getPokemons(limit:Int,offset:Int): Flow<SamplePartialState>

//    suspend fun sampleGetSinlgeParam(param: Any): Flow<SamplePartialState>
//    suspend fun sampleUpdate(
//        param: Any,
//        update: Any
//    ): Flow<SamplePartialState>
}




sealed class SamplePartialState {
    data class Success(val pokemons: List<PokemonDomain>?) : SamplePartialState()
    data class Failed(val errorMessage: String) : SamplePartialState()
    data class Error(val errorMessage: String) : SamplePartialState()
}

