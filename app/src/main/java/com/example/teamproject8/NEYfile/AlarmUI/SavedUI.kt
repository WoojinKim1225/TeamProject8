package com.example.teamproject8.NEYfile.AlarmUI

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.teamproject8.WJKfile.ViewModel.NavigationDatabaseViewModel

@Composable
fun SavedUI(modifier: Modifier = Modifier, viewModel: NavigationDatabaseViewModel = viewModel()) {

    val SavedItemList by viewModel.navigationItems.collectAsState()

    LazyColumn {
        items(SavedItemList){
            item ->
            SavedItemUI(item)
        }

    }
}