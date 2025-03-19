package com.example.tabletdashboard.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.tabletdashboard.viewmodels.WeatherViewModel

@Composable
fun WeatherWidget(viewModel: WeatherViewModel) {
    val weather = viewModel.weatherState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startWeatherUpdates(lat = 37.7749, lon = -122.4194) // Example: San Francisco
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        val weatherValue = weather.value
        if (weatherValue != null) {
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
            Text("Loading...")
        }
    }
}

/**
 * Code	Description
 * 0	Clear sky
 * 1, 2, 3	Mainly clear, partly cloudy, and overcast
 * 45, 48	Fog and depositing rime fog
 * 51, 53, 55	Drizzle: Light, moderate, and dense intensity
 * 56, 57	Freezing Drizzle: Light and dense intensity
 * 61, 63, 65	Rain: Slight, moderate and heavy intensity
 * 66, 67	Freezing Rain: Light and heavy intensity
 * 71, 73, 75	Snow fall: Slight, moderate, and heavy intensity
 * 77	Snow grains
 * 80, 81, 82	Rain showers: Slight, moderate, and violent
 * 85, 86	Snow showers slight and heavy
 * 95 *	Thunderstorm: Slight or moderate
 * 96, 99 *	Thunderstorm with slight and heavy hail
 */

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
