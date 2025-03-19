package com.example.tabletdashboard.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ButtonArrayWidget(
    topLeft: @Composable (Modifier) -> Unit,
    topRight: @Composable (Modifier) -> Unit,
    bottomLeft: @Composable (Modifier) -> Unit,
    bottomRight: @Composable (Modifier) -> Unit
) {
    Column(Modifier.fillMaxSize().padding(0.dp)) {
        Row(
            Modifier.fillMaxWidth().weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            topLeft(Modifier.fillMaxHeight().weight(1f))
            Spacer(Modifier.width(16.dp))
            topRight(Modifier.fillMaxHeight().weight(1f))
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth().weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            bottomLeft(Modifier.fillMaxHeight().weight(1f))
            Spacer(Modifier.width(16.dp))
            bottomRight(Modifier.fillMaxHeight().weight(1f))
        }
    }
}

@Preview(widthDp = 300, heightDp = 300)
@Composable
fun ButtonArrayWidgetPreview() {
    ButtonArrayWidget(
        topLeft = { modifier -> Box(modifier = modifier.background(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp))) },
        topRight =  { modifier -> Box(modifier = modifier.background(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp))) },
        bottomLeft =  { modifier -> Box(modifier = modifier.background(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp))) },
        bottomRight =  { modifier -> Box(modifier = modifier.background(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp))) }
    )
}