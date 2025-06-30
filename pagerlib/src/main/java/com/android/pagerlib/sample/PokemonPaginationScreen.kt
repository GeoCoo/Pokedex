package com.android.pagerlib.sample

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.android.core_ui.component.LifecycleEffect
import com.android.pagerlib.PaginationSideEffect
import com.android.pagerlib.compose.PaginatedLazyColumn

/**
 * Sample integration screen showing how to use the pagination library
 * with the existing Pokemon data and UI components.
 * 
 * This demonstrates how to replace the existing manual pagination
 * with the PagerLib library.
 */
@Composable
fun PokemonPaginationScreen(
    onPokemonClick: (String) -> Unit,
    viewModel: PokemonPaginationViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = viewModel.viewState.value
    
    // Initialize pagination on screen creation
    LifecycleEffect(
        lifecycleOwner = lifecycleOwner,
        lifecycleEvent = Lifecycle.Event.ON_CREATE
    ) {
        viewModel.initialize()
    }
    
    // Handle side effects
    LaunchedEffect(Unit) {
        viewModel.effect.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                when (effect) {
                    is PaginationSideEffect.ShowError -> {
                        // Handle error (e.g., show snackbar)
                        // You can emit this to a higher-level error handler
                    }
                    is PaginationSideEffect.RefreshCompleted -> {
                        // Handle refresh completion if needed
                    }
                    is PaginationSideEffect.EndReached -> {
                        // Handle end of list reached if needed
                    }
                }
            }
    }
    
    // Use the pagination Compose component
    PaginatedLazyColumn(
        state = state,
        onLoadMore = { viewModel.loadNextPage() },
        onRetry = { viewModel.retry() },
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        itemContent = { pokemon ->
            // Reuse existing Pokemon UI components
            PokemonCard(
                pokemon = pokemon,
                onClick = { onPokemonClick(pokemon.name) }
            )
        }
    )
}

/**
 * Alternative implementation using the existing VerticalPager approach
 * but with pagination library state management.
 */
@Composable
fun PokemonPaginationScreenWithPager(
    onPokemonClick: (String) -> Unit,
    viewModel: PokemonPaginationViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = viewModel.viewState.value
    
    // Initialize pagination
    LifecycleEffect(
        lifecycleOwner = lifecycleOwner,
        lifecycleEvent = Lifecycle.Event.ON_CREATE
    ) {
        viewModel.initialize()
    }
    
    // Handle side effects
    LaunchedEffect(Unit) {
        viewModel.effect.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                when (effect) {
                    is PaginationSideEffect.ShowError -> {
                        // Handle error display
                    }
                    is PaginationSideEffect.RefreshCompleted -> {
                        // Handle refresh
                    }
                    is PaginationSideEffect.EndReached -> {
                        // Handle end reached
                    }
                }
            }
    }
    
    // Use existing VerticalPager with pagination state
    if (state.isLoading && state.items.isEmpty()) {
        // Show loading indicator
        LoadingIndicator()
    } else {
        VerticalPagerWith3VisibleItems(
            pokemons = state.items,
            nextPokemons = {
                // Use pagination library to load more
                viewModel.loadNextPage()
            },
            onClick = onPokemonClick
        )
    }
}

/**
 * Simple Pokemon card component for demonstration.
 * In real implementation, you would use the existing FloatingImageCard
 * or create a new card design.
 */
@Composable
fun PokemonCard(
    pokemon: com.android.model.PokemonDomain,
    onClick: () -> Unit
) {
    // This would be replaced with actual Pokemon card implementation
    // using the existing design system components
    androidx.compose.material3.Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(16.dp)
        ) {
            androidx.compose.material3.Text(
                text = pokemon.name,
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall
            )
            // Add more Pokemon details here
        }
    }
}

/**
 * Loading indicator component.
 * This would typically use the existing LoadingIndicator from core_ui.
 */
@Composable
fun LoadingIndicator() {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.CircularProgressIndicator()
    }
}

/**
 * Placeholder for the existing VerticalPagerWith3VisibleItems.
 * This would be imported from the existing PokemonsScreen.
 */
@Composable
fun VerticalPagerWith3VisibleItems(
    pokemons: List<com.android.model.PokemonDomain>,
    nextPokemons: () -> Unit,
    onClick: (String) -> Unit
) {
    // This would be the actual implementation from PokemonsScreen.kt
    // For demonstration purposes, using a simple list
    androidx.compose.foundation.lazy.LazyColumn {
        items(pokemons) { pokemon ->
            PokemonCard(pokemon = pokemon, onClick = { onClick(pokemon.name) })
        }
    }
}