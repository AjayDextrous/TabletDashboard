package com.example.tabletdashboard.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tabletdashboard.api.Dish
import com.example.tabletdashboard.api.DishType
import com.example.tabletdashboard.api.Label
import com.example.tabletdashboard.api.MensaMenu
import com.example.tabletdashboard.api.MenuDay
import com.example.tabletdashboard.api.PriceDetail
import com.example.tabletdashboard.api.Prices
import com.example.tabletdashboard.tools.AsyncState
import com.example.tabletdashboard.viewmodels.MensaViewModel
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun MensaMenuWidget(modifier: Modifier = Modifier) {

    val mensaViewModel = koinViewModel<MensaViewModel>()
    val mensaMenuResponseState = mensaViewModel.menuState.collectAsState()
    val mensaMenuResponse = mensaMenuResponseState.value
    val today = LocalDateTime.now().toLocalDate()

    Column(modifier = modifier
        .fillMaxSize()
        .border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(8.dp)
        )
        .padding(16.dp, 0.dp)) {
        when (mensaMenuResponse) {
            AsyncState.Loading -> {
                Box(Modifier.fillMaxSize()){
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.Center)
                    )
                }
            }
            AsyncState.Init -> {
                Box(Modifier.fillMaxSize()) {

                }
            }
            is AsyncState.Error -> {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text(
                        text = "Error loading Mensa Menu",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { mensaViewModel.fetchMensaMenu() },
                    ) {
                        Text("Retry")
                    }
                }
            }
            is AsyncState.Success -> {
                val mensaMenu = mensaMenuResponse.data
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Mensa Menu",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    items(mensaMenu.days) { menuDay ->
                        val day = LocalDate.parse(menuDay.date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        if(!day.isBefore(today)){
                            MenuDayItem(menuDay)
                        }
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
fun MenuDayItem(menuDay: MenuDay) {
    Column(modifier = Modifier.padding(vertical = 0.dp)) {
        Text(
            text = menuDay.date,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )

        menuDay.dishes.forEach { dish ->
            DishItem(dish)
        }
    }
}

@Composable
fun DishItem(dish: Dish) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.medium
            ),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            // Title at the top
            Text(
                text = dish.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )

            // Dish type
            Text(
                text = dish.dishType.name.replace("_", " "),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.height(16.dp)) // Pushes the labels and price to the bottom

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Labels on the bottom left
                Text(
                    text = dish.labels.joinToString(separator = "") { mapLabelToEmoji(it) },
                    color = MaterialTheme.colorScheme.onPrimary
                )

                // Price on the bottom right
                Text(
                    text = dish.prices.students.toDisplayString(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

    }
}

private fun PriceDetail.toDisplayString(): String {
    val stringBuilder = StringBuilder()
    if(basePrice > 0) {
        stringBuilder.append("$basePrice €")
    }
    if(basePrice > 0 && pricePerUnit > 0) {
        stringBuilder.append(" + ")
    }
    if(pricePerUnit > 0) {
        stringBuilder.append("$pricePerUnit €/$unit")
    }
    return stringBuilder.toString()
}

fun mapLabelToEmoji(label: Label): String {
    return when (label) {
        Label.GLUTEN -> "🌾"
        Label.LACTOSE -> "🥛"
        Label.SHELL_FRUITS -> "🥜"
        Label.VEGETARIAN -> "🥦"
        Label.VEGAN -> "🌱"
        Label.MEAT -> "🥩"
        Label.FISH -> "🐟"
        Label.SOY -> "🫘"
        Label.BEEF -> "🐄"
        Label.PORK -> "🐖"
        Label.CHICKEN_EGGS -> "🥚"
        else -> ""
    }
}

@Preview(showBackground = true)
@Composable
fun MensaMenuWidgetPreview() {
    val sampleMenu = MensaMenu(
        number = 1,
        year = 2024,
        version = "1.0",
        days = listOf(
            MenuDay(
                date = "2025-03-18",
                dishes = listOf(
                    Dish(
                        name = "Spaghetti Bolognese",
                        dishType = DishType.PASTA,
                        prices = Prices(
                            students = PriceDetail(2.5, 0.0, "plate"),
                            staff = PriceDetail(4.0, 0.0, "plate"),
                            guests = PriceDetail(5.5, 0.0, "plate")
                        ),
                        labels = listOf(Label.MEAT, Label.GLUTEN)
                    ),
                    Dish(
                        name = "Vegan Curry",
                        dishType = DishType.WOK,
                        prices = Prices(
                            students = PriceDetail(3.0, 0.0, "plate"),
                            staff = PriceDetail(4.5, 0.0, "plate"),
                            guests = PriceDetail(6.0, 0.0, "plate")
                        ),
                        labels = listOf(Label.VEGAN, Label.SOY)
                    )
                )
            )
        )
    )

    MensaMenuWidget()
}
