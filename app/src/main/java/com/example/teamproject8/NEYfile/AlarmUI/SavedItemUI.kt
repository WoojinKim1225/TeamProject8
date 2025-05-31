package com.example.teamproject8.NEYfile.AlarmUI

import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teamproject8.R

@Composable
fun SavedItemUI(modifier: Modifier = Modifier) {
    val departLoc: String = "COEX"
    val arrivedLoc: String = "Konkuk"
    val arrivedTime: Int = 1
    val titleIcon: Int = R.drawable.baseline_access_alarm_24
    val departTime: Int = 1

    val locScript = departLoc + " ️-> " + arrivedLoc
    val timeScript = "${departTime.toString()} -> ${arrivedTime.toString()}"
    Card {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(titleIcon),
                    contentDescription = "",
                    modifier = Modifier.padding(10.dp)
                )
                Text(
                    locScript,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            Image(painter = painterResource(titleIcon), contentDescription = "")
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                timeScript,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(10.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = {},
                    modifier = Modifier.padding(5.dp)) {
                    Text("Enabled")
                }
            }

        }
    }
}

@Preview
@Composable
private fun SavedItemUI_prev() {
    SavedItemUI()

}