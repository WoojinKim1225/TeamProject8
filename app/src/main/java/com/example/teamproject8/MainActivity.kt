package com.example.teamproject8

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import com.example.teamproject8.MYNfile.NaverMapScreen
import com.example.teamproject8.ui.theme.TeamProject11Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TeamProject11Theme {
                NaverMapScreen(
                    clientId = BuildConfig.NAVER_CLIENT_ID,
                    clientSecret = BuildConfig.NAVER_CLIENT_SECRET
                )
            }
        }
    }
}