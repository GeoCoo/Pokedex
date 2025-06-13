package com.android.pokemons.router

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.android.api.Screen
import com.android.pokemons.ui.PokemonsScreen


fun NavGraphBuilder.pokemonsScreen() {
    composable(route = Screen.PokemonsScreen.route) {
        PokemonsScreen()
    }
}