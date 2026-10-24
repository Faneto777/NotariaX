package com.example.notariax.view

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
object Rutas {
    const val LOGIN = "login"
    const val HOME = "home"
    const val TRAMITE = "tramite"
    const val FIRMA = "firma"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Rutas.LOGIN) {

        composable(Rutas.LOGIN) {
        }

        composable(Rutas.HOME) {
        }

        composable(Rutas.TRAMITE) {
        }

        composable(Rutas.FIRMA) {
        }
    }
}