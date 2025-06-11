package com.example.teamproject8.NEYfile.AlarmUI

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.teamproject8.NEYfile.ViewModel.SavedViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.teamproject8.R

@Composable
fun SavedUI(modifier: Modifier = Modifier, viewModel: SavedViewModel = viewModel()) {
//    val SavedItemList = remember { mutableStateListOf<SavedItem>() }
//    val context = LocalContext.current

    val SavedItemList by viewModel.savedItems.collectAsState()

    //DB에서 정보를 꺼내와서 List형태로 만들어야함.
    LazyColumn {
        items(SavedItemList) {
            item:SavedItem ->
            SavedItemUI(item)
        }
    }
}