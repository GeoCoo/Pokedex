package com.android.pagerlib.sample

import androidx.lifecycle.viewModelScope
import com.android.model.PokemonDomain
import com.android.pagerlib.Paginator
import com.android.pagerlib.PaginationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Sample ViewModel showing how to use the pagination library with the existing MVI architecture.
 * This replaces the manual pagination logic in the original PokemonsViewModel.
 */
@HiltViewModel
class PokemonPaginationViewModel @Inject constructor(
    pokemonPaginationRepository: PokemonPaginationRepository
) : PaginationViewModel<PokemonDomain>(
    paginator = Paginator(
        repository = pokemonPaginationRepository,
        pageSize = 20,
        scope = viewModelScope
    )
) {
    
    /**
     * Initialize by loading the first page.
     * This would typically be called from the UI when the screen is created.
     */
    fun initialize() {
        loadFirstPage()
    }
    
    /**
     * Check if more items should be loaded based on the current scroll position.
     * This can be called from the UI when the user is approaching the end of the list.
     */
    fun checkAndLoadMore(currentIndex: Int) {
        if (shouldLoadMore(currentIndex)) {
            loadNextPage()
        }
    }
}