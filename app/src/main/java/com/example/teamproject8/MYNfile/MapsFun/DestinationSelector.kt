package com.example.teamproject8.MYNfile.MapsFun

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder

@Composable
fun DestinationSelector(
    modifier: Modifier = Modifier,
    currentLocation: LatLng,
    clientId: String,
    clientSecret: String,
    onDestinationSelected: (LatLng, String?) -> Unit
) {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle(context)
    var naverMap by remember { mutableStateOf<NaverMap?>(null) }
    var query by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<PlaceResult>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 🔍 검색 UI
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("목적지 검색") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    coroutineScope.launch {
                        searchResults = searchPlaces(query, clientId, clientSecret)
                    }
                }) {
                    Text("검색")
                }
            }

            // 🔍 검색 결과
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                if (searchResults.isEmpty() && query.isNotBlank()) {
                    item {
                        Text(
                            text = "결과 없음",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                } else {
                    items(searchResults) { item ->
                        Text(
                            text = "${item.name}\n${item.roadAddress ?: item.address ?: "주소 정보 없음"}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onDestinationSelected(item.location, item.name)
                                    query = ""
                                    searchResults = emptyList()
                                }
                                .padding(8.dp)
                        )
                    }
                }
            }

            // 🗺️ 지도 출력
            AndroidView(
                factory = {
                    mapView.apply {
                        getMapAsync { map ->
                            naverMap = map

                            Marker().apply {
                                position = currentLocation
                                captionText = "현재 위치"
                                setMap(map)
                            }

                            map.moveCamera(CameraUpdate.scrollTo(currentLocation))

                            map.setOnMapLongClickListener { _, latLng ->
                                coroutineScope.launch {
                                    val address = reverseGeocode(latLng, clientId, clientSecret)
                                    onDestinationSelected(latLng, address)
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

data class PlaceResult(
    val name: String,
    val location: LatLng,
    val address: String?,
    val roadAddress: String?
)

suspend fun searchPlaces(query: String, clientId: String, clientSecret: String): List<PlaceResult> {
    return withContext(Dispatchers.IO) {
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val url =
                "https://openapi.naver.com/v1/search/local.json?query=$encodedQuery&display=5&start=1&sort=random"

            val request = Request.Builder()
                .url(url)
                .addHeader("X-Naver-Client-Id", "9ExLakbVtELjLL0YSKDm")
                .addHeader("X-Naver-Client-Secret", "CKz2vb03xe")
                .addHeader("Content-Type", "application/json")
                .build()

            val response = OkHttpClient().newCall(request).execute()
            val body = response.body?.string() ?: return@withContext emptyList()
            val json = JSONObject(body)
            val items = json.getJSONArray("items")

            List(items.length()) { i ->
                val item = items.getJSONObject(i)
                val name = item.getString("title").replace(Regex("<.*?>"), "")
                val address = item.getString("address")
                val roadAddress = item.optString("roadAddress", null)
                val mapx = item.getString("mapx").toDouble() / 1E7
                val mapy = item.getString("mapy").toDouble() / 1E7

                PlaceResult(
                    name = name,
                    location = LatLng(mapy, mapx),
                    address = address,
                    roadAddress = roadAddress
                )
            }
        } catch (e: Exception) {
            Log.e("SearchPlaces", "Error: ${e.message}", e)
            emptyList()
        }
    }
}

suspend fun reverseGeocode(latLng: LatLng, clientId: String, clientSecret: String): String? {
    return withContext(Dispatchers.IO) {
        try {
            val url = "https://maps.apigw.ntruss.com/map-reversegeocode/v2/gc" +
                    "?coords=${latLng.longitude},${latLng.latitude}" +
                    "&output=json&orders=roadaddr,addr,legalcode,admcode"

            val request = Request.Builder()
                .url(url)
                .addHeader("x-ncp-apigw-api-key-id", clientId)
                .addHeader("x-ncp-apigw-api-key", clientSecret)
                .build()

            val response = OkHttpClient().newCall(request).execute()
            val body = response.body?.string() ?: return@withContext null
            val json = JSONObject(body)
            val results = json.getJSONArray("results")
            if (results.length() > 0) {
                val firstResult = results.getJSONObject(0)
                val region = firstResult.getJSONObject("region")
                val land = firstResult.getJSONObject("land")
                val region1 = region.getJSONObject("area1").getString("name")
                val region2 = region.getJSONObject("area2").getString("name")
                val region3 = region.getJSONObject("area3").getString("name")
                val region4 = region.getJSONObject("area4").optString("name", "")

                val roadName = land.optString("name", "")
                val number1 = land.optString("number1", "")
                val number2 = land.optString("number2", "")
                val roadNumber = if (number2.isNotEmpty()) "$number1-$number2" else number1

                listOf(region1, region2, region3, region4, roadName, roadNumber)
                    .filter { it.isNotEmpty() }
                    .joinToString(" ")
            } else null
        } catch (e: Exception) {
            Log.e("ReverseGeocode", "Error: ${e.message}", e)
            null
        }
    }
}