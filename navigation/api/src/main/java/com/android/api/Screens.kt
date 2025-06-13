package com.android.api

sealed class Screen(val route: String) {
    object SplashScreen : Screen("sampleScreen")
    object PokemonsScreen : Screen("pokemonsScreen")
    object SampleScreenWIthMultiParams : Screen("sampleScreen/{param_one}/param_two/{param_two}") {
        fun createRoute(paramOne: Int?, paramTwo: Boolean) =
            "sampleScreen/$paramOne/param_two/$paramTwo"
    }
    object SampleScreenWithParam : Screen("sampleScreen/{param}") {
        fun createRoute(param: Int) = "sampleScreen/$param"
    }
}