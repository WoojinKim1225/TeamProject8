package com.example.teamproject8.WJKfile.model

sealed class Routes (val route: String) {
    object Home : Routes("Home")
    object Contacts : Routes("Contacts")
    object Favorites : Routes("Favorites")
}