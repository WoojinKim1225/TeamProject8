package com.example.teamproject8.WJKfile.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.teamproject8.WJKfile.model.Routes
import com.example.teamproject8.WJKfile.ui.Saved
import com.example.teamproject8.WJKfile.ui.Favorites
import com.example.teamproject8.WJKfile.ui.Map

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Map.route
    ){
        composable(Routes.Map.route){
            Map()
        }
        composable(Routes.Saved.route){
            Saved(navController)
        }
        composable(Routes.Favorites.route){
            Favorites()
        }
    }
}