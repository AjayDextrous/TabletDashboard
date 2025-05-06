package com.example.tabletdashboard.ui.widgets

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.CountDownTimer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tabletdashboard.viewmodels.PomodoroTimerViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.math.ceil

@Composable
fun PomodoroTimerWidget() {
    val viewModel: PomodoroTimerViewModel = koinViewModel()
    val remainingTime by viewModel.remainingTime.collectAsState()
    val selectedTime by viewModel.selectedTime.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.medium)
            .indication(interactionSource, LocalIndication.current)
            .combinedClickable(
                onClick = {
                    if (isRunning) viewModel.stopTimer() else viewModel.startTimer()
                },
                onLongClick = { showDialog = true },
                onDoubleClick = { viewModel.resetTimer() }
            ),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { remainingTime / selectedTime.toFloat() },
            modifier = Modifier.fillMaxSize().padding(16.dp),
            color = MaterialTheme.colorScheme.onPrimary,
            strokeWidth = 8.dp,
            trackColor = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = ceil(remainingTime / 1000.0).toFormattedTime(),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }

    AnimatedVisibility(showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Select Timer Duration") },
            text = {
                Row {
                    listOf(
                        listOf(1, 5, 15, 45, 90),
                        listOf(10, 30, 60, 120)
                    ).forEach { group ->
                        Column(Modifier.weight(1f).padding(8.dp)) {
                            group.forEach { minutes ->
                                Text(
                                    "$minutes",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.resetTimer(minutes * 60 * 1000L)
                                            showDialog = false
                                        }
                                        .padding(0.dp, 8.dp)
                                        .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                                        .padding(8.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun Double.toFormattedTime(): String {
    val minutes = (this / 60).toInt()
    val seconds = (this % 60).toInt()
    return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
}

