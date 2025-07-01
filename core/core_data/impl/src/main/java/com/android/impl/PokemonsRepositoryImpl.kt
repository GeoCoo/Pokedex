package com.android.impl

import com.android.api.ApiClient
import com.android.api.PokemonsRepository
import com.android.api.PokemonsResponse
import com.android.api.ResourceProvider
import com.android.pokedex.core.core_resources.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class PokemonsRepositoryImpl @Inject constructor(
    private val apiClient: ApiClient,
    private val resourceProvider: ResourceProvider,
) : PokemonsRepository {

    override fun getPokemons(limit: Int, offset: Int): Flow<PokemonsResponse> = flow {
        val result = apiClient.getPokemons(limit, offset)

        result.fold(
            onSuccess = { response ->
                if (response.results.isNotEmpty()) {
                    emit(PokemonsResponse.Success(response))
                } else {
                    emit(
                        PokemonsResponse.Error(
                            errorMsg = resourceProvider.getString(
                                R.string.generic_error_msg
                            )
                        )
                    )
                }
            },
            onFailure = { exception ->
                emit(
                    PokemonsResponse.Error(
                        errorMsg = resourceProvider.getString(
                            R.string.generic_error_msg
                        )
                    )
                )
            }
        )
    }.catch {
        emit(PokemonsResponse.Failed(errorMsg = it.localizedMessage ?: ""))
    }

//    override fun sampleWithParam(param: Any): Flow<PokemonsResponse> = flow {
//        val response = apiClient.sampleWithParam(param)
//
//        when {
//            response.isSuccessful && response.body() != null -> {
//                emit(PokemonsResponse.Success(listOf()))
//            }
//
//            else -> {
//                emit(PokemonsResponse.Failed(""))
//            }
//        }
//    }.catch {
//        emit(PokemonsResponse.Error(errorMsg = it.localizedMessage ?: ""))
//
//    }
//
//    override fun samplePost(param: Any): Flow<PokemonsResponse> =
//        flow {
//            val response = apiClient.samplePost(param)
//            when {
//                response.isSuccessful && response.body() != null -> {
//                    emit(PokemonsResponse.Success(listOf()))
//                }
//
//                else -> {
//                    emit(PokemonsResponse.Failed(""))
//                }
//            }
//        }.catch {
//            emit(PokemonsResponse.Failed(errorMsg = it.localizedMessage ?: ""))
//        }
//
//    override fun samplePut(
//        param: Any,
//        update: Any
//    ): Flow<PokemonsResponse> = flow {
//        val response = apiClient.samplePut(param, update)
//
//        when {
//            response.isSuccessful && response.body() != null -> {
//                emit(PokemonsResponse.Success(listOf()))
//            }
//
//            else -> {
//                emit(PokemonsResponse.Failed(""))
//            }
//        }
//    }
}