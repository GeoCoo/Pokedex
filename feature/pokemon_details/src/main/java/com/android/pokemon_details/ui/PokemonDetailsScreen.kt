package com.android.pokemon_details.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.android.core_ui.component.LifecycleEffect
import com.android.core_ui.component.LoadingIndicator


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailsScreen(pokemonName: String) {

    val viewModel = hiltViewModel<PokemonDetailsViewModel>()
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = viewModel.viewState.value

    LifecycleEffect(
        lifecycleOwner = lifecycleOwner, lifecycleEvent = Lifecycle.Event.ON_CREATE
    ) {
        viewModel.setEvent(Event.GetPokemonDetails(pokemonName))
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text(text = pokemonName.replaceFirstChar { it.uppercase() }) })
    }, content = { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (state.isLoading) LoadingIndicator()
            else {
                state.pokemonData?.let { pokemon ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Display Pokemon details here
                        Text(text = "Pokemon Name: ${pokemon.name}")
                        // Add more details as needed
                    }
                } ?: run {
                    Text(text = "Pokemon not found")
                }
            }

        }

    })


    LaunchedEffect(Unit) {
        viewModel.effect.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->

            }
    }
}