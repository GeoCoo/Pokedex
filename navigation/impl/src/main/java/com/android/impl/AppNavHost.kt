package com.android.impl

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.android.api.Screen
import com.android.pokemons.router.pokemonsScreen
import com.android.splash.router.sampleScreen


@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.SplashScreen.route
    ) {
        this.sampleScreen(navController)
        this.pokemonsScreen()
    }
}

