package com.example.teamproject8

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import com.example.teamproject8.WJKfile.ui.MainScreen2
import com.example.teamproject8.ui.theme.TeamProject11Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TeamProject11Theme {
                MainScreen2()
            }
        }
    }
}
