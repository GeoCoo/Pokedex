package com.android.impl

import com.android.api.PokemonInteractor
import com.android.api.PokemonsRepository
import com.android.api.PokemonsResponse
import com.android.api.SamplePartialState
import com.android.api.SamplePartialState.Error
import com.android.api.SamplePartialState.Failed
import com.android.api.SamplePartialState.Success
import com.android.pokedex.core.core_resources.R
import com.android.helpers.safeAsync
import com.android.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PokemonInteractorImpl @Inject constructor(
    private val productsRepository: PokemonsRepository,

    ) : PokemonInteractor {
    override suspend fun getPokemons(limit:Int,offset:Int): Flow<SamplePartialState> = flow {
        productsRepository.getPokemons(limit,offset).collect {
            when (it) {

                is PokemonsResponse.Error -> {
                    emit(Error(it.errorMsg.toString()))

                }

                is PokemonsResponse.Failed -> {
                    emit(Failed(it.errorMsg.toString()))

                }

                is PokemonsResponse.Success -> {
                    emit(Success(it.pokemonsResponse?.results?.map { pokemon -> pokemon.toDomain() }))
                }
            }
        }
    }.safeAsync {
        Error(
            it.message ?:  R.string.generic_error_msg.toString()
        )
    }
//
//    override suspend fun sampleGetSinlgeParam(param: Any): Flow<SamplePartialState> = flow {
//        productsRepository.sampleWithParam(param).collect {
//            when (it) {
//                is PokemonsResponse.Error -> {
//                    emit(Error(it.errorMsg.toString()))
//                }
//
//                is PokemonsResponse.Failed -> {
//                    emit(Failed(it.errorMsg.toString()))
//                }
//
//                is PokemonsResponse.Success -> {
//                    emit(Success(listOf()))
//                }
//            }
//        }
//    }.safeAsync {
//        Error(
//            it.message ?: R.string.generic_error_msg.toString()
//        )
//    }
//
//
//    override suspend fun sampleUpdate(param: Any,update: Any): Flow<SamplePartialState> = flow {
//        productsRepository.samplePut(param,update).collect {
//            when (it) {
//                is PokemonsResponse.Error -> {
//                    emit(Error(it.errorMsg.toString()))
//                }
//
//                is PokemonsResponse.Failed -> {
//                    emit(Failed(it.errorMsg.toString()))
//                }
//
//                is PokemonsResponse.Success -> {
//                    emit(Success(listOf()))
//                }
//            }
//        }
//    }.safeAsync {
//        Error(
//            it.message ?: R.string.generic_error_msg.toString()
//        )
//    }
}
