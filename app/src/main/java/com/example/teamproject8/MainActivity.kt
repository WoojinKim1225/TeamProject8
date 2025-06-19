package com.example.teamproject8

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.teamproject8.WJKfile.ui.MainScreen2
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat

import com.example.teamproject8.ui.theme.TeamProject11Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        setContent {
            TeamProject11Theme {

/*                NaverMapScreen(

                    // API 사용하는 Android package가 제한되어 있어서,
                    // API 사용할 때 카톡으로 패키지 이름 넘겨주시면 추가해드리겠습니다.

                    clientId = BuildConfig.NAVER_CLIENT_ID,         // Naver Maps API Client ID
                    clientSecret = BuildConfig.NAVER_CLIENT_SECRET, // Naver Maps API Client Secret
                    searchId = BuildConfig.NAVER_SEARCH_CLIENT_ID,  // Naver Search API Client ID
                    searchSecret = BuildConfig.NAVER_SEARCH_CLIENT_SECRET,  // Naver Search API Client Secret
                    googleApiKey = BuildConfig.GOOGLE_API_KEY       // Google Direction API Key
                )*/
                MainScreen2()
            }
        }
    }
}

