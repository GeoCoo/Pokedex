package com.android.pagerlib.sample

import com.android.api.PokemonInteractor
import com.android.api.PokemonPartialState
import com.android.model.PokemonDomain
import com.android.pagerlib.PaginationRepository
import com.android.pagerlib.PaginationResult
import com.android.pagerlib.integration.PaginationIntegration
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Sample integration showing how to adapt the existing Pokemon data layer
 * to work with the pagination library.
 */
class PokemonPaginationRepository @Inject constructor(
    private val pokemonInteractor: PokemonInteractor
) : PaginationRepository<PokemonDomain> {
    
    override suspend fun loadPage(pageSize: Int, page: Int): PaginationResult<PokemonDomain> {
        return try {
            val offset = page * pageSize
            val response = pokemonInteractor.getPokemons(pageSize, offset).first()
            
            when (response) {
                is PokemonPartialState.Success -> {
                    val items = response.pokemons ?: emptyList()
                    // Since the API doesn't provide total count, we estimate hasNextPage
                    // by checking if we received a full page of items
                    val hasNextPage = items.size == pageSize
                    
                    PaginationResult.Success(
                        items = items,
                        currentPage = page,
                        hasNextPage = hasNextPage,
                        totalCount = null // Not available from the API
                    )
                }
                
                is PokemonPartialState.Failed -> {
                    PaginationResult.Error(
                        throwable = Exception(response.errorMessage),
                        message = response.errorMessage
                    )
                }
                
                is PokemonPartialState.Error -> {
                    PaginationResult.Error(
                        throwable = Exception(response.errorMessage),
                        message = response.errorMessage
                    )
                }
            }
        } catch (e: Exception) {
            PaginationResult.Error(e)
        }
    }
}

/**
 * Alternative approach using the PaginationIntegration utility.
 * This shows how to create a repository adapter without implementing the interface directly.
 */
fun createPokemonPaginationRepository(
    pokemonInteractor: PokemonInteractor
): PaginationRepository<PokemonDomain> {
    return PaginationIntegration.createFlowAdapter(
        loadPage = { pageSize, page ->
            val offset = page * pageSize
            pokemonInteractor.getPokemons(pageSize, offset)
        },
        mapToResult = { response, page ->
            when (response) {
                is PokemonPartialState.Success -> {
                    val items = response.pokemons ?: emptyList()
                    val hasNextPage = items.size == 20 // Assuming default page size of 20
                    
                    PaginationResult.Success(
                        items = items,
                        currentPage = page,
                        hasNextPage = hasNextPage,
                        totalCount = null
                    )
                }
                
                is PokemonPartialState.Failed -> {
                    PaginationResult.Error(
                        throwable = Exception(response.errorMessage),
                        message = response.errorMessage
                    )
                }
                
                is PokemonPartialState.Error -> {
                    PaginationResult.Error(
                        throwable = Exception(response.errorMessage),
                        message = response.errorMessage
                    )
                }
            }
        }
    )
}