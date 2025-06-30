package com.android.api

sealed class Screen(val route: String) {
    object SplashScreen : Screen("splash_screen")
    object PokemonsScreen : Screen("pokemons_screen")
    object PokemonDetailsScreen : Screen("pokemon_detail/{param}") {
        fun createRoute(param: String) = "pokemon_detail/$param"
    }
}