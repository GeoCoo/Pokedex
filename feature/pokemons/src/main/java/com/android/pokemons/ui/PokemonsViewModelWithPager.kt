package com.android.pokemons.ui

import androidx.lifecycle.viewModelScope
import com.android.api.PokemonInteractor
import com.android.api.PokemonPartialState
import com.android.model.PokemonDomain
import com.android.pagerlib.Paginator
import com.android.pagerlib.PaginationRepository
import com.android.pagerlib.PaginationResult
import com.android.pagerlib.PaginationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Alternative Pokemon ViewModel using the pagination library.
 * This demonstrates how to replace manual pagination logic with PagerLib.
 * 
 * This provides the same functionality as the original PokemonsViewModel
 * but with better state management, caching, and error handling.
 */
@HiltViewModel
class PokemonsViewModelWithPager @Inject constructor(
    pokemonInteractor: PokemonInteractor
) : PaginationViewModel<PokemonDomain>(
    paginator = Paginator(
        repository = createPokemonRepository(pokemonInteractor),
        pageSize = 20,
        scope = viewModelScope
    )
) {
    
    /**
     * Initialize the Pokemon list by loading the first page.
     * Call this when the screen is created.
     */
    fun loadPokemons() {
        loadFirstPage()
    }
    
    /**
     * Check if more Pokemon should be loaded based on current scroll position.
     * This replaces the manual pagination trigger in the UI.
     */
    fun checkPaginationNeeds(currentIndex: Int) {
        if (shouldLoadMore(currentIndex, threshold = 3)) {
            loadNextPage()
        }
    }
    
    companion object {
        /**
         * Creates a PaginationRepository adapter for the Pokemon API.
         */
        private fun createPokemonRepository(
            pokemonInteractor: PokemonInteractor
        ): PaginationRepository<PokemonDomain> {
            return object : PaginationRepository<PokemonDomain> {
                override suspend fun loadPage(pageSize: Int, page: Int): PaginationResult<PokemonDomain> {
                    return try {
                        val offset = page * pageSize
                        val response = pokemonInteractor.getPokemons(pageSize, offset).first()
                        
                        when (response) {
                            is PokemonPartialState.Success -> {
                                val items = response.pokemons ?: emptyList()
                                PaginationResult.Success(
                                    items = items,
                                    currentPage = page,
                                    hasNextPage = items.size == pageSize, // Estimate based on page size
                                    totalCount = null // Not provided by API
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
        }
    }
}