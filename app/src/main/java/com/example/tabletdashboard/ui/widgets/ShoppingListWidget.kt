package com.example.tabletdashboard.ui.widgets

// Import statements
import android.app.Application
import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Data model for a shopping item.
data class ShoppingItem(
    val id: Int,
    val text: String,
    val done: Boolean
)

// ViewModel that holds the list of shopping items and persists them locally.
class ShoppingListViewModel(application: Application) : AndroidViewModel(application) {

    // Backing list as a mutable state list for Compose to react to changes.
    var items = mutableStateListOf<ShoppingItem>()
        private set

    // Use SharedPreferences for simple local storage.
    private val sharedPreferences = application.getSharedPreferences("shopping_list_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    init {
        loadItems()
    }

    // Add a new item to the list.
    fun addItem(text: String) {
        val newItem = ShoppingItem(id = generateId(), text = text, done = false)
        items.add(newItem)
        backupItems()
    }

    // Remove an item from the list.
    fun removeItem(item: ShoppingItem) {
        items.remove(item)
        backupItems()
    }

    // Toggle the done status for an item.
    fun toggleItem(item: ShoppingItem) {
        val index = items.indexOf(item)
        if (index != -1) {
            // Update the item with a new copy reflecting the toggled done status.
            items[index] = items[index].copy(done = !item.done)
            backupItems()
        }
    }

    // Save the current list as JSON to SharedPreferences.
    private fun backupItems() {
        val json = gson.toJson(items)
        sharedPreferences.edit() { putString("shopping_list", json) }
    }

    // Load the list from SharedPreferences.
    private fun loadItems() {
        val json = sharedPreferences.getString("shopping_list", null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<ShoppingItem>>() {}.type
            val list: List<ShoppingItem> = gson.fromJson(json, type)
            items.addAll(list)
        }
    }

    // Generate a unique ID for a new item.
    private fun generateId(): Int {
        return if (items.isEmpty()) 1 else items.maxOf { it.id } + 1
    }
}

@Composable
fun ShoppingListWidget(viewModel: ShoppingListViewModel = viewModel()) {
    // Local state for the text input.
    val newItemText = remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .border(width = 1.dp, color = MaterialTheme.colorScheme.onPrimary, shape = RoundedCornerShape(8.dp))
        .padding(16.dp)) {

        // Title
        Text("Shopping List", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // List of items with swipe-to-dismiss functionality.
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(
                items = viewModel.items,
                key = { it.id }
            ) { item ->
                val dismissState = rememberSwipeToDismissBoxState()
                SwipeToDismissBox(
                    state = dismissState,
                    backgroundContent = {
                        // Simple background for swipe action (customize as needed).
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            Text("Deleting...", modifier = Modifier.align(Alignment.CenterStart))
                        }
                    }) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Checkbox to show done status.
                            Checkbox(
                                checked = item.done,
                                onCheckedChange = { viewModel.toggleItem(item) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = item.text, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
            }
        }

        // Text field and button to add new items.
        OutlinedTextField(
            value = newItemText.value,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                if (newItemText.value.isNotBlank()) {
                    viewModel.addItem(newItemText.value)
                    newItemText.value = ""
                }
            }),
            onValueChange = { newItemText.value = it },
            label = { Text("Add new item") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
//        Button(
//            onClick = {
//                if (newItemText.value.isNotBlank()) {
//                    viewModel.addItem(newItemText.value)
//                    newItemText.value = ""
//                }
//            },
//            modifier = Modifier.align(Alignment.End)
//        ) {
//            Text("Add")
//        }
    }
}
