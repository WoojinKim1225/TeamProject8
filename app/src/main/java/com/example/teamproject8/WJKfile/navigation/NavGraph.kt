package com.example.teamproject8.WJKfile.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.teamproject8.WJKfile.model.Routes
import com.example.teamproject8.WJKfile.ui.Contacts
import com.example.teamproject8.WJKfile.ui.Favorites
import com.example.teamproject8.WJKfile.ui.Home

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home.route
    ){
        composable(Routes.Home.route){
            Home()
        }
        composable(Routes.Contacts.route){
            Contacts()
        }
        composable(Routes.Favorites.route){
            Favorites()
        }
    }
}