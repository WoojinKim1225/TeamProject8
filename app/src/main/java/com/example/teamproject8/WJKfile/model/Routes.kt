package com.example.teamproject8.WJKfile.model

sealed class Routes (val route: String) {
    object Map : Routes("Map")
    object Saved : Routes("Saved")
    object Logs : Routes("Logs")
}