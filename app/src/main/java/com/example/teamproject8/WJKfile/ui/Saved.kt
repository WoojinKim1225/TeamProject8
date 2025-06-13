package com.example.teamproject8.WJKfile.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.teamproject8.WJKfile.RoomDB.NavigationDatabase
import com.example.teamproject8.WJKfile.RoomDB.NavigationEntity
import com.example.teamproject8.WJKfile.ViewModel.NavigationDatabaseViewModel
import com.example.teamproject8.WJKfile.ViewModel.NavigationViewModelFactory

@Composable
fun Saved(navController: NavController,
          navigationViewModel: NavigationDatabaseViewModel = viewModel(
                 factory = NavigationViewModelFactory(
                     NavigationDatabase.getDBInstance(navController.context).getItemDao()
                 )
             )
) {
    val navigationItems by navigationViewModel.navigationItems.collectAsState(initial = emptyList())

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text("Navigation Menu", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 16.dp))

        // (선택 사항) 샘플 데이터 추가 버튼
        Button(onClick = { navigationViewModel.addSampleNavigationItems() }) {
            Text("Add Sample Items (If Empty)")
        }
        Spacer(modifier = Modifier.size(16.dp))

        if (navigationItems.isEmpty()) {
            Text("No navigation items found. Try adding some sample items.")
        } else {
            LazyColumn {
                items(navigationItems, key = { item -> item.id }) { navItem ->
                    NavigationListItem(
                        navItem = navItem,
                        onItemClick = {
                            // 항목 클릭 시 해당 route로 네비게이션
                            // navController.navigate(navItem.route)
                            // 또는 다른 액션 수행
                            println("Clicked item: ${navItem.title}, route: ${navItem.route}")
                        }
                    )
                    Divider() // 각 항목 사이에 구분선 추가
                }
            }
        }
    }
}

@Composable
fun NavigationListItem(
    navItem: NavigationEntity,
    onItemClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onItemClick)
                .padding(vertical = 12.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 아이콘 표시 (실제 아이콘 리소스 ID 사용)
            // android.R.drawable.ic_menu_help는 플레이스홀더입니다. 실제 navItem.icon 사용
            Icon(
                painter = painterResource(id = navItem.icon), // navItem.icon에 실제 drawable 리소스 ID가 있어야 함
                contentDescription = navItem.title,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = navItem.title, style = MaterialTheme.typography.bodyLarge)
        }
        Text(text = "origin: ${navItem.origin}, destination: ${navItem.destination}")
    }

}

@Preview
@Composable
private fun SavedPreview() {
    MaterialTheme {
        Saved(NavController(LocalContext.current))
    }
}