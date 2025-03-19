package com.example.tabletdashboard.ui.widgets

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.tabletdashboard.repositories.CalendarEvent
import com.example.tabletdashboard.repositories.CalendarRepository
import com.example.tabletdashboard.repositories.startOfDay
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.max

private const val TAG = "CalendarTimelineWidget"

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CalendarTimelineWidget(modifier: Modifier = Modifier, context: Context) {
    var selectedDays by remember { mutableIntStateOf(3) } // Default to 3 days
    var startDate by remember { mutableStateOf(Calendar.getInstance().startOfDay()) }

    val calendarRepository = koinInject<CalendarRepository>()
    // TODO: replace with flows and viewmodel
    val events = remember(selectedDays, startDate) {
        calendarRepository.fetchCalendarEvents(context, startDate, selectedDays)
    }

    Column(modifier
        .border(1.dp, MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(8.dp))
        .clip(
            RoundedCornerShape(8.dp)
        )) {
        // Header - Select Days and Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    shape = RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp)
                )
                .padding(16.dp, 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dropdown for selecting number of days
            var expanded by remember { mutableStateOf(false) }
            Box {
                Text(
                    text = "$selectedDays Days ▼",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { expanded = true }
                        .padding(16.dp, 0.dp)
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf(1, 3, 4, 5, 7).forEach { dayOption ->
                        DropdownMenuItem(
                            text = { Text("$dayOption Days") },
                            onClick = {
                                selectedDays = dayOption
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Navigation buttons
            Row {
                IconButton(
                    onClick = {
                        startDate = (startDate.clone() as Calendar).also {
                            it.add(
                                Calendar.DAY_OF_YEAR,
                                -selectedDays
                            )
                        }
                    },
                    colors = IconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer, disabledContainerColor = Color.Unspecified, disabledContentColor = Color.Unspecified)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
                }
                Button(onClick = { startDate = Calendar.getInstance().startOfDay() }) {
                    Text("Current")
                }
                IconButton(
                    onClick = { startDate = (startDate.clone() as Calendar).also {
                        it.add(
                            Calendar.DAY_OF_YEAR,
                            selectedDays
                        )
                    } },
                    colors = IconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer, disabledContainerColor = Color.Unspecified, disabledContentColor = Color.Unspecified)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
                }
            }
        }



        // Timeline Grid
//        // Animated content for startDate with sliding transition
//        AnimatedContent(
//            targetState = startDate.clone() as Calendar, // remove the freakin calendar style implementation. use millis.
//            transitionSpec = {
//                // Slide in from left or right depending on comparison
//                if (targetState.before(startDate)) {
//                    // targetStartDate is earlier (before)
//                    slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.End) with slideOutOfContainer(
//                        AnimatedContentTransitionScope.SlideDirection.End
//                    )
//                } else {
//                    // targetStartDate is later (after)
//                    slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Start) with slideOutOfContainer(
//                        AnimatedContentTransitionScope.SlideDirection.Start
//                    )
//                }
//            }
//        ) { targetStartDate ->
//            // Animated content for selectedDays with zoom transition
//            AnimatedContent(
//                targetState = selectedDays,
//                transitionSpec = {
//                    // Zoom in or out depending on the size of selectedDays
//                    if (targetState > selectedDays) {
//                        // Zoom In (selectedDays became bigger)
//                        scaleIn() with scaleOut(targetScale = targetState.toFloat()/selectedDays, transformOrigin = TransformOrigin(0.0f, 0.5f))
//                    } else {
//                        // Zoom Out (selectedDays became smaller)
//                        scaleIn() with scaleOut(targetScale = selectedDays.toFloat()/targetState, transformOrigin = TransformOrigin(0.0f, 0.5f))
//                    }
//                }
//            ) { targetSelectedDays ->
//                    // Timeline view with the animated content
//                    TimelineView(events, targetSelectedDays, targetStartDate)
//            }
//        }
        TimelineView(events, selectedDays, startDate)
    }
}

@Composable
fun TimelineView(events: Map<Calendar, List<CalendarEvent>>, daysToShow: Int, startDate: Calendar) {
    val hours = (0..23).map { it.toString().padStart(2, '0') + ":00" }
    val scrollState = rememberScrollState()
    var currentMinute by remember { mutableIntStateOf(getCurrentMinuteOfDay()) }
    val itemHeight = remember { mutableIntStateOf(60) }
    val totalHeight = itemHeight.intValue * hours.size

    val shownDays = (0..<daysToShow).map {
        val cal = startDate.clone() as Calendar
        cal.add(Calendar.DAY_OF_YEAR, it)
        cal
    }

    // Update time every minute
    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000) // Update every minute
            currentMinute = getCurrentMinuteOfDay()
        }
    }

    LaunchedEffect(Unit) {
        val middlePosition = scrollState.maxValue * 12 / 24
        scrollState.scrollTo(middlePosition)
    }

    Row(modifier = Modifier
        .fillMaxWidth()) {
        Spacer(modifier = Modifier.width(50.dp))
        shownDays.forEach { date ->
            Text(
                text = SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(date.time),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(Color.LightGray.copy(alpha = 0.2f))
                    .padding(4.dp)
                    .weight(1f)
            )
        }

    }

    Box(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)){
        Row(modifier = Modifier
            .fillMaxWidth()
//            .verticalScroll(scrollState)
        ) {
            // Left Column - Time Labels

            Column(modifier = Modifier
                .width(50.dp)
                .height(totalHeight.dp)) {
                hours.forEach { time ->
                    Text(
                        text = time,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .height(itemHeight.intValue.dp)
                            .padding(4.dp, 0.dp)
                    )
                }
            }
            Row(modifier = Modifier.height(totalHeight.dp)) {
                // Right Section - Multi-Day Timeline
                shownDays.forEach { date ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(Color.LightGray.copy(alpha = 0.2f))
                    ) {
                        // Event Grid
                        Box(modifier = Modifier.fillMaxHeight()) {
                            (0..<23).forEach { hour ->
                                HorizontalDivider(thickness = 1.dp, color = Color.LightGray, modifier = Modifier.offset { IntOffset(0, ((hour+1)  * itemHeight.intValue + 1).dp.roundToPx()) })
                            }
                            events[date]?.forEach { event ->
                                EventBox(event)
                            }
                        }
                    }
                }
            }
        }

        HorizontalDivider(Modifier
            .padding(45.dp, 0.dp, 0.dp, 0.dp)
            .fillMaxWidth()
            .offset { IntOffset(0, (totalHeight / 1440F * currentMinute).dp.roundToPx()) },
            color = Color.Red
        )
    }
}

@Composable
fun EventBox(event: CalendarEvent) {
    val eventStartHour = event.start.get(Calendar.HOUR_OF_DAY)
    val eventStartMinute = event.start.get(Calendar.MINUTE)
    val eventEndMinute = event.end.get(Calendar.MINUTE)
    val durationMinutes = if (event.isAllDay) {
        30
    } else {
        max((event.end.timeInMillis - event.start.timeInMillis) / 60_000, 30).toInt()
    }


    val verticalOffset = (eventStartHour * 60 + eventStartMinute) / 60f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp * durationMinutes)
            .padding(4.dp)
            .offset(y = (verticalOffset * 60).dp)
            .border(width = 1.dp, color = Color.LightGray.copy(alpha = 0.8f), shape = RoundedCornerShape(4.dp))
            .background(event.color, shape = RoundedCornerShape(4.dp))
    ) {
        Text(
            text = event.title,
            fontSize = 12.sp,
            color = Color.White,
            modifier = Modifier.padding(4.dp, 0.dp)
        )
    }
}




@Composable
fun RequestCalendarPermission(onGranted: @Composable () -> Unit) {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(hasCalendarPermission(context)) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) hasPermission = true
    }

    if (!hasPermission) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(8.dp)
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Calendar access is required to display events.")
            Button(onClick = { requestPermissionLauncher.launch(Manifest.permission.READ_CALENDAR) }) {
                Text("Grant Permission")
            }
        }
    } else {
        onGranted() // Proceed with the feature
    }
}

fun hasCalendarPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context, Manifest.permission.READ_CALENDAR
    ) == PackageManager.PERMISSION_GRANTED
}

fun getCurrentMinuteOfDay(): Int {
    val now = LocalTime.now()
    return now.hour * 60 + now.minute
}
