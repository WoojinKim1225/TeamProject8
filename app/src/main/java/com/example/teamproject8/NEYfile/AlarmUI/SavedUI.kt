package com.example.teamproject8.NEYfile.AlarmUI

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun SavedUI(modifier: Modifier = Modifier) {
    val SavedItemList = remember { mutableStateListOf<SavedItem>() }

    //DB에서 정보를 꺼내와서 List형태로 만들어야함.

    LazyColumn {
        items(SavedItemList){ item ->
            SavedItemUI(item)
        }
    }
}