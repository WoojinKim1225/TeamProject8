package com.example.teamproject8.WJKfile.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.teamproject8.MYNfile.NaverMapScreen
import com.example.teamproject8.NEYfile.AlarmUI.SavedUI
import com.example.teamproject8.WJKfile.model.Routes
import com.example.teamproject8.BuildConfig
import com.example.teamproject8.WJKfile.ui.LogsWithCalender

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Map.route
    ){
        composable(Routes.Map.route){
            // Map()
            NaverMapScreen(
                clientId = BuildConfig.NAVER_CLIENT_ID,         // Naver Maps API Client ID
                clientSecret = BuildConfig.NAVER_CLIENT_SECRET, // Naver Maps API Client Secret
                searchId = BuildConfig.NAVER_SEARCH_CLIENT_ID,  // Naver Search API Client ID
                searchSecret = BuildConfig.NAVER_SEARCH_CLIENT_SECRET,  // Naver Search API Client Secret
                googleApiKey = BuildConfig.GOOGLE_API_KEY       // Google Direction API Key
            )
        }
        composable(Routes.Saved.route){
            SavedUI(navController)
        }
        composable(Routes.Logs.route){
            LogsWithCalender(navController)
        }
    }
}