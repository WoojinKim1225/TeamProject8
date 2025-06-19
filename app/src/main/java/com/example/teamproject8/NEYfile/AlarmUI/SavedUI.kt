package com.example.teamproject8.NEYfile.AlarmUI

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.teamproject8.WJKfile.RoomDB.Navigations.NavigationDatabase
import com.example.teamproject8.WJKfile.ViewModel.NavigationDatabaseViewModel
import com.example.teamproject8.WJKfile.ViewModel.NavigationViewModelFactory

@Composable
fun SavedUI(
    navController: NavController, viewModel: NavigationDatabaseViewModel = viewModel(
        factory = NavigationViewModelFactory(
            NavigationDatabase.getDBInstance(navController.context).getItemDao()
        )
    )
) {
    val SavedItemList by viewModel.navigationItems.collectAsState()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(top = 30.dp, bottom = 16.dp)
    ) {
        items(SavedItemList) { item ->
            SavedItemUI(item)
        }

    }
}