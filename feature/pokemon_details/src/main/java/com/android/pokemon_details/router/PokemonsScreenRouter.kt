package com.android.pokemon_details.router

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.android.api.Screen
import com.android.pokemon_details.ui.PokemonDetailsScreen


fun NavGraphBuilder.pokemonDetailsScreen() {
    composable(
        route = Screen.PokemonDetailsScreen.route,
        arguments = listOf(
            navArgument("param") { type = NavType.StringType
            }
        )
    ){
        val pokemonName = it.arguments?.getString("param") ?:""
        PokemonDetailsScreen(pokemonName)
    }
}