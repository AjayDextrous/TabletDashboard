package com.example.tabletdashboard.ui.widgets

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ClockWidget(modifier: Modifier = Modifier) {
    var currentTime by remember { mutableStateOf(getCurrentTime()) }

    // Update time every minute
    LaunchedEffect(Unit) {
        while (true) {
            delay(1_000) // Update every second
            currentTime = getCurrentTime()
        }
    }

    // Adaptive Text using BoxWithConstraints
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp)),

        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(targetState = currentTime) { targetTime ->
            BasicText(
                text = targetTime,
                style = TextStyle(MaterialTheme.colorScheme.onPrimary),
                autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = 60.sp, stepSize = 10.sp)
            )
        }

    }
}

// Function to get the current time in HH:mm format
fun getCurrentTime(): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
}
