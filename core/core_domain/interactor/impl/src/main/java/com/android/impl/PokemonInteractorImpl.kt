package com.android.impl

import com.android.api.PokemonInteractor
import com.android.api.PokemonPartialState
import com.android.api.PokemonsRepository
import com.android.api.PokemonsResponse
import com.android.api.SinglePokemonPartialState
import com.android.api.SinglePokemonRespone
import com.android.helpers.safeAsync
import com.android.model.toDomain
import com.android.pokedex.core.core_resources.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PokemonInteractorImpl @Inject constructor(
    private val productsRepository: PokemonsRepository,

    ) : PokemonInteractor {
    override suspend fun getPokemons(limit: Int, offset: Int): Flow<PokemonPartialState> = flow {
        productsRepository.getPokemons(limit, offset).collect {
        when(it){
            is PokemonsResponse.Error -> {
                emit(PokemonPartialState.Error(it.errorMsg.toString()))
            }

            is PokemonsResponse.Failed -> {
                emit(PokemonPartialState.Failed(it.errorMsg.toString()))
            }

            is PokemonsResponse.Success -> {
                emit(PokemonPartialState.Success(it.pokemonsResponse?.results?.map { pokemon -> pokemon.toDomain() }))
            }
        }

        }
    }.safeAsync {
        PokemonPartialState.Error(
            it.message ?: R.string.generic_error_msg.toString()
        )
    }
//            when (it) {
//
//                is PokemonsResponse.Error -> {
//                    emit(Error(it.errorMsg.toString()))
//
//                }
//
//                is PokemonsResponse.Failed -> {
//                    emit(Failed(it.errorMsg.toString()))
//
//                }
//
//                is PokemonsResponse.Success -> {
//                    emit(Success(it.pokemonsResponse?.results?.map { pokemon -> pokemon.toDomain() }))
//                }
//            }
//        }
//    }.safeAsync {
//        Error(
//            it.message ?: R.string.generic_error_msg.toString()
//        )
//    }

    override suspend fun getSinglePokemon(pokemonName: String): Flow<SinglePokemonPartialState> =
        flow {
            productsRepository.getSinglePokemon(pokemonName).collect {
                when (it) {
                    is SinglePokemonRespone.Error -> {
                        emit(SinglePokemonPartialState.Error(it.errorMsg.toString()))
                    }

                    is SinglePokemonRespone.Failed -> {
                        emit(SinglePokemonPartialState.Failed(it.errorMsg.toString()))
                    }

                    is SinglePokemonRespone.Success -> {
                        emit(SinglePokemonPartialState.Success(it.singlePokemon.toDomain()))
                    }
                }
            }
        }.safeAsync {
            SinglePokemonPartialState.Error(
                it.message ?: R.string.generic_error_msg.toString()
            )
        }
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
