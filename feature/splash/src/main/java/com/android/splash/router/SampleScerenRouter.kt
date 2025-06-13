package com.android.splash.router

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.android.api.Screen
import com.android.splash.ui.SplashScreen

fun NavGraphBuilder.sampleScreen(navController: NavHostController) {
    composable(route = Screen.SplashScreen.route) {
        SplashScreen(onLoaded = {
            navController.navigate(Screen.PokemonsScreen.route)
        })
    }
}