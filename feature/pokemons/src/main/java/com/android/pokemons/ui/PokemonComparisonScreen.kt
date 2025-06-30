package com.android.pokemons.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Demonstration screen showing both the original and pagination library approaches.
 * This allows for easy comparison and testing of both implementations.
 */
@Composable
fun PokemonComparisonScreen(
    onPokemonClick: (String) -> Unit
) {
    var usePaginationLibrary by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Toggle button to switch between implementations
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Pokemon List Implementation",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (usePaginationLibrary) "Using PagerLib" else "Using Original",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = usePaginationLibrary,
                        onCheckedChange = { usePaginationLibrary = it }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (usePaginationLibrary) {
                        "• Automatic pagination\n• In-memory caching\n• Better error handling\n• MVI state management"
                    } else {
                        "• Manual pagination\n• No caching\n• Basic error handling\n• Custom state management"
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        
        // Content based on selected implementation
        Box(modifier = Modifier.weight(1f)) {
            if (usePaginationLibrary) {
                PokemonsScreenWithPager(onPokemonClick = onPokemonClick)
            } else {
                PokemonsScreen(onPokemonClick = onPokemonClick)
            }
        }
    }
}

/**
 * Preview for the comparison screen
 */
@Preview(showBackground = true)
@Composable
fun PokemonComparisonScreenPreview() {
    MaterialTheme {
        PokemonComparisonScreen(onPokemonClick = {})
    }
}

/**
 * Usage demonstration showing different ways to use the pagination library
 */
@Composable
fun PaginationLibraryUsageExamples() {
    // Example 1: Using the provided ViewModel with existing UI
    ExampleWithExistingUI()
    
    // Example 2: Using the Compose component with custom UI
    ExampleWithComposeComponent()
    
    // Example 3: Manual pagination control
    ExampleWithManualControl()
}

@Composable
private fun ExampleWithExistingUI() {
    // This shows how to replace the original ViewModel
    val viewModel: PokemonsViewModelWithPager = hiltViewModel()
    val state = viewModel.viewState.value
    
    LaunchedEffect(Unit) {
        viewModel.loadPokemons()
    }
    
    // Use existing UI components with pagination state
    // The VerticalPagerWithPagination component handles the rest
}

@Composable
private fun ExampleWithComposeComponent() {
    // This shows how to use the provided Compose components
    val viewModel: PokemonsViewModelWithPager = hiltViewModel()
    val state = viewModel.viewState.value
    
    // Use the built-in PaginatedLazyColumn component
    // Note: This would require importing from pagerlib
}

@Composable
private fun ExampleWithManualControl() {
    // This shows how to manually control pagination
    val viewModel: PokemonsViewModelWithPager = hiltViewModel()
    val state = viewModel.viewState.value
    
    Column {
        // Custom UI implementation
        // Manual pagination triggers
        Button(
            onClick = { viewModel.loadNextPage() },
            enabled = state.hasNextPage && !state.isLoadingMore
        ) {
            if (state.isLoadingMore) {
                Text("Loading...")
            } else {
                Text("Load More Pokemon")
            }
        }
    }
}