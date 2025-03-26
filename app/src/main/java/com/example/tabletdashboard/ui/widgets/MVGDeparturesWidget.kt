package com.example.tabletdashboard.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tabletdashboard.api.Departure
import com.example.tabletdashboard.api.Station
import com.example.tabletdashboard.tools.AsyncState
import com.example.tabletdashboard.viewmodels.MVGDeparturesViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MVGDeparturesWidget() {
    val mvgDeparturesViewModel = koinViewModel<MVGDeparturesViewModel>()
    var mvgResponse = mvgDeparturesViewModel.mvgResponse.collectAsState()
    var selectedStation = mvgDeparturesViewModel.selectedStation.collectAsState()

    val mvgDataState = mvgResponse.value
    Column(
        modifier = Modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(8.dp)
        )
    ) {
        when (mvgDataState) {
            is AsyncState.Success -> {
                var departures = mvgDataState.data.departureList


                // Dropdown to select a station
                StationDropdown(
                    selectedStation = selectedStation.value,
                    onStationSelected = { station ->
                        mvgDeparturesViewModel.loadStation(station)
                    }
                )
                // Show departures once the station is selected
                MVGDeparturesList(departures = departures)
            }

            is AsyncState.Error -> {
                // Show an error message if the data could not be loaded
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Error loading MVG Departures",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { mvgDeparturesViewModel.fetchManually() },
                    ) {
                        Text("Retry")
                    }
                }
            }

            else -> {
                Box(Modifier.fillMaxSize()){
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationDropdown(
    selectedStation: Station,
    onStationSelected: (Station) -> Unit
) {
    // Dropdown to allow the user to select a station
    var expanded by remember { mutableStateOf(false) }
    Box(
        Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp)
            )
            .padding(16.dp, 8.dp)
    ) {
        Text(
            text = "${selectedStation.stationName} ▼",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .clickable { expanded = true }
                .padding(16.dp, 8.dp)
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Station.entries.forEach { station ->
                DropdownMenuItem(
                    text = { Text(station.stationName) },
                    onClick = {
                        onStationSelected(station)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun MVGDeparturesList(departures: List<Departure>) {
    // A LazyColumn to display a scrollable list of departures
    LazyColumn(modifier = Modifier.padding(8.dp, 0.dp)) {
        items(departures) { departure ->
            DepartureItem(departure)
        }
    }
}

@Composable
fun DepartureItem(departure: Departure) {
    // A card for each departure item
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
    ) {
        Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier
                    .padding(8.dp)
                    .widthIn(32.dp, 48.dp)
                    .height(24.dp)
                    .background(
                        color = getColorFor(
                            departure.servingLine.name,
                            departure.servingLine.number
                        ), shape = getShapeFor(departure.servingLine.name)
                    )
                    .padding(4.dp),
                textAlign = TextAlign.Center,
                text = departure.servingLine.number,
                color = Color.White,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                modifier = Modifier.weight(1f),
                text = departure.servingLine.direction,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = departure.countdown,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        HorizontalDivider(Modifier.padding(4.dp, 0.dp), color = Color.Gray.copy(alpha = 0.2f))
    }
}

fun getColorFor(lineName: String, lineNumber: String): Color {
    var color = when (lineNumber) {
        "U1" -> Color(0xFF3C7324)
        "U2" -> Color(0xFFC3012D)
        "U3" -> Color(0xFFEC671F)
        "U4" -> Color(0xFF01AB85)
        "U5" -> Color(0xFFBE7B00)
        "U6" -> Color(0xFF0165AF)
        "U7" -> Color(0xFFF9E300)
        "U8" -> Color(0xFF8ECAE6)
        else -> Color.Gray
    }
    if (lineName.contains("Bus")) {
        color = Color(0xFF005E89)
    } else if (lineName.contains("Tram")) {
        color = Color(0xFFE30614)
    }
    return color
}

fun getShapeFor(lineName: String): Shape {
    return when (lineName) {
        "U-Bahn" -> RoundedCornerShape(0.dp, 0.dp, 0.dp, 0.dp)
        "S-Bahn" -> RoundedCornerShape(0.dp, 0.dp, 0.dp, 0.dp)
        "Tram" -> RoundedCornerShape(0.dp, 0.dp, 0.dp, 0.dp)
        else -> RoundedCornerShape(1.dp)
    }
}

@Preview(showBackground = true)
@Composable
fun MVGDeparturesWidgetPreview() {
    MVGDeparturesWidget()
}
