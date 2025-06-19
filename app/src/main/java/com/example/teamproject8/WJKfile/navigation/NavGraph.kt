package com.example.teamproject8.WJKfile.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.teamproject8.BuildConfig
import com.example.teamproject8.MYNfile.NaverMapScreen
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
            // Map()
            NaverMapScreen(

                // API 사용하는 Android package가 제한되어 있어서,
                // API 사용할 때 카톡으로 패키지 이름 넘겨주시면 추가해드리겠습니다.

                clientId = BuildConfig.NAVER_CLIENT_ID,         // Naver Maps API Client ID
                clientSecret = BuildConfig.NAVER_CLIENT_SECRET, // Naver Maps API Client Secret
                searchId = BuildConfig.NAVER_SEARCH_CLIENT_ID,  // Naver Search API Client ID
                searchSecret = BuildConfig.NAVER_SEARCH_CLIENT_SECRET,  // Naver Search API Client Secret
                googleApiKey = BuildConfig.GOOGLE_API_KEY       // Google Direction API Key
            )
        }
        composable(Routes.Saved.route){
            Saved(navController)
        }
        composable(Routes.Favorites.route){
            Favorites()
        }
    }
}