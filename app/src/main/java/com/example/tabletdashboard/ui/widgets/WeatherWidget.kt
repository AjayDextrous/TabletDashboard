package com.example.tabletdashboard.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import com.example.tabletdashboard.tools.AsyncState
import com.example.tabletdashboard.viewmodels.WeatherViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun WeatherWidget() {
    val viewModel: WeatherViewModel = koinViewModel()
    val weather = viewModel.weatherState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startWeatherUpdates(lat = currentLocation.first, lon = currentLocation.second)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        val weatherStateValue = weather.value
        if (weatherStateValue is AsyncState.Success) {
            val weatherValue = weatherStateValue.data
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = getWeatherIcon(weatherValue.currentWeather.weatherCode),
                    contentDescription = "Weather Icon",
                    modifier = Modifier.size(72.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = "${weatherValue.currentWeather.temperature}${weatherValue.currentWeatherUnits.temperature}",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .clickable(
                        enabled = true,
                        onClickLabel = "Update weather manually",
                        onClick = {
                            viewModel.fetchWeatherManually(
                                currentLocation.first,
                                currentLocation.second
                            )
                        }),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(48.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text("Loading weather...", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}

val Munich = Pair(48.1351, 11.5820)
var currentLocation = Munich

fun getWeatherIcon(weatherCode: Int): ImageVector {
    return when (weatherCode) {
        0 -> Icons.Rounded.WbSunny // Clear sky
        in 1..3 -> Icons.Rounded.Cloud // Mainly clear, partly cloudy, overcast
        in 45..48 -> Icons.Outlined.Cloud // Fog and depositing rime fog TODO: replace with Foggy
        in 51..55 -> Icons.Rounded.Grain // Drizzle: Light, moderate, dense intensity
        in 56..57 -> Icons.Rounded.AcUnit // Freezing Drizzle: Light, dense intensity
        in 61..65 -> Icons.Rounded.Umbrella // Rain: Slight, moderate, heavy intensity
        in 66..67 -> Icons.Rounded.AcUnit // Freezing Rain: Light, heavy intensity
        in 71..75 -> Icons.Rounded.AcUnit // Snow fall: Slight, moderate, heavy intensity
        77 -> Icons.Rounded.AcUnit // Snow grains
        in 80..82 -> Icons.Rounded.Grain // Rain showers: Slight, moderate, violent
        in 85..86 -> Icons.Rounded.AcUnit // Snow showers: Slight, heavy
        95 -> Icons.Rounded.Bolt // Thunderstorm: Slight or moderate
        in 96..99 -> Icons.Rounded.Bolt // Thunderstorm with slight, heavy hail
        else -> Icons.Rounded.HelpOutline // Unknown
    }
}
