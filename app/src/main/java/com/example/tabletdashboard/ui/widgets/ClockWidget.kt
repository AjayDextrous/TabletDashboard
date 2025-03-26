package com.example.tabletdashboard.ui.widgets

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ClockWidget(modifier: Modifier = Modifier) {
    var currentTime by remember { mutableStateOf(getCurrentTime()) }
    var indiaTime by remember { mutableStateOf(getIndiaTime()) }
    var currentHour = currentTime.first
    var currentMinute = currentTime.second
    var indiaHour = indiaTime.first
    var indiaMinute = indiaTime.second

    var showIndiaTime by remember { mutableStateOf(false) }
    LaunchedEffect(showIndiaTime) {
        if (showIndiaTime) {
            delay(3000)
            showIndiaTime = false
        }
    }
    val interactionSource = remember { MutableInteractionSource() }

    // Update time every minute
    LaunchedEffect(Unit) {
        while (true) {
            delay(1_000) // Update every second
            currentTime = getCurrentTime()
            indiaTime = getIndiaTime()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp))
            .clickable(enabled = true, onClick = {showIndiaTime = !showIndiaTime}, interactionSource = interactionSource, indication = null),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AnimatedContent(targetState = currentHour, transitionSpec = { slideInVertically(initialOffsetY = {it/2})+ fadeIn() with slideOutVertically(targetOffsetY = {-it/2}) + fadeOut() }) { targetHour ->
                BasicText(
                    text = targetHour,
                    style = TextStyle(MaterialTheme.colorScheme.onPrimary, textAlign = TextAlign.Center),
                    autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = 60.sp, stepSize = 10.sp)
                )
            }
            BasicText(
                text = ":",
                modifier = Modifier.padding(0.dp,0.dp,0.dp,8.dp),
                style = TextStyle(MaterialTheme.colorScheme.onPrimary, textAlign = TextAlign.Center),
                autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = 60.sp, stepSize = 10.sp)
            )
            AnimatedContent(targetState = currentMinute, transitionSpec = { slideInVertically(initialOffsetY = {it/2})+ fadeIn() with slideOutVertically(targetOffsetY = {-it/2}) + fadeOut() }) { targetMinute ->
                BasicText(
                    text = targetMinute,
                    style = TextStyle(MaterialTheme.colorScheme.onPrimary, textAlign = TextAlign.Center),
                    autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = 60.sp, stepSize = 10.sp)
                )
            }
        }
        AnimatedVisibility(showIndiaTime) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AnimatedContent(targetState = indiaHour, transitionSpec = { slideInVertically(initialOffsetY = {it/2})+ fadeIn() with slideOutVertically(targetOffsetY = {-it/2}) + fadeOut() }) { targetHour ->
                    BasicText(
                        text = targetHour,
                        style = TextStyle(MaterialTheme.colorScheme.onPrimary, textAlign = TextAlign.Center),
                        autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = 30.sp, stepSize = 10.sp)
                    )
                }
                BasicText(
                    text = ":",
                    modifier = Modifier.padding(0.dp,0.dp,0.dp,8.dp),
                    style = TextStyle(MaterialTheme.colorScheme.onPrimary, textAlign = TextAlign.Center),
                    autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = 30.sp, stepSize = 10.sp)
                )
                AnimatedContent(targetState = indiaMinute, transitionSpec = { slideInVertically(initialOffsetY = {it/2})+ fadeIn() with slideOutVertically(targetOffsetY = {-it/2}) + fadeOut() }) { targetMinute ->
                    BasicText(
                        text = targetMinute,
                        style = TextStyle(MaterialTheme.colorScheme.onPrimary, textAlign = TextAlign.Center),
                        autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = 30.sp, stepSize = 10.sp)
                    )
                }
            }
        }
    }
}

// Function to get the current time in HH:mm format
fun getCurrentTime(): Pair<String, String> {
    return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")).split(":").let {
        Pair(it[0], it[1])
    }
}

fun getIndiaTime(): Pair<String, String> {
    val indiaTime = LocalTime.now(ZoneId.of("Asia/Kolkata"))
    return indiaTime.format(DateTimeFormatter.ofPattern("HH:mm")).split(":").let {
        Pair(it[0], it[1])
    }
}
