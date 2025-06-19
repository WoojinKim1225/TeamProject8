package com.example.teamproject8.WJKfile.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Place

object NavBarItems{
    val BarItems = listOf(
        BarItem(
            title = Routes.Map.route,
            selectIcon = Icons.Default.Place,
            onSelectedIcon = Icons.Outlined.Place,
            route = Routes.Map.route
        ),
        BarItem(
            title = Routes.Saved.route,
            selectIcon = Icons.Default.Menu,
            onSelectedIcon = Icons.Outlined.Menu,
            route = Routes.Saved.route
        ),
        BarItem(
            title = Routes.Logs.route,
            selectIcon = Icons.Default.Favorite,
            onSelectedIcon = Icons.Outlined.FavoriteBorder,
            route = Routes.Logs.route,
        )
    )
}