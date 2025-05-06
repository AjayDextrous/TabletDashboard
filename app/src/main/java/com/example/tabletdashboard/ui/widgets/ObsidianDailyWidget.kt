package com.example.tabletdashboard.ui.widgets


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.Settings
import android.widget.TextView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.setPadding
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.tabletdashboard.viewmodels.ObsidianDailyViewModel
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun ObsidianDailyWidget() {
    val viewModel = koinViewModel<ObsidianDailyViewModel>()
    val context = LocalContext.current
    val fileContent = remember { mutableStateOf("Loading...") }

    LaunchedEffect(Unit) {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val fileName = "${today.format(formatter)}.md"
        val filePath = "/storage/self/primary/ObsidianKB/Journal/$fileName"
        val file = File(filePath)

        fileContent.value = if (file.exists()) {
            try {
                file.readText()
            } catch (e: IOException) {
                "Error reading file: ${e.message}"
            }
        } else {
            "Journal file for today not found:\n$filePath"
        }
    }

    Surface(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Text(
                text = fileContent.value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun ObsidianDailyWidgetWithPermissionCheck() {
    val viewModel = koinViewModel<ObsidianDailyViewModel>()
    val context = LocalContext.current
    val activity = context as? Activity
    val fileContent = remember { mutableStateOf<String?>(null) }
    val hasPermission = remember { mutableStateOf(checkStoragePermission()) }

    LaunchedEffect(hasPermission.value) {
        if (hasPermission.value) {
            fileContent.value = loadTodayJournalFile()
        }
    }

    Surface(modifier = Modifier.fillMaxSize().border(
        width = 1.dp,
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(8.dp)
    ), color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(8.dp)) {
        if (hasPermission.value) {
            fileContent.value?.let {
                val spanned = viewModel.markwon.toMarkdown(it)
                val size = MaterialTheme.typography.headlineSmall.fontSize.value
                val color = MaterialTheme.colorScheme.onPrimaryContainer.toArgb()
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    AndroidView(modifier = Modifier.fillMaxWidth(), factory = { context ->
                        TextView(context).apply {
                            textSize = size
                            setPadding(8.dp.value.toInt())
                            text = spanned
                            setBackgroundColor(color)
                        }
                    })
                }
            } ?: Text("Loading journal...")
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("This app needs access to manage external storage to view your journal.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    requestStoragePermission(context)
                }) {
                    Text("Grant Permission")
                }
            }
        }
    }

    // Optional: refresh permission state on resume
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                hasPermission.value = checkStoragePermission()
            }
        })
    }
}

fun checkStoragePermission(): Boolean {
    return Environment.isExternalStorageManager()
}

fun requestStoragePermission(context: Context) {
    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
    context.startActivity(intent)
}

fun loadTodayJournalFile(): String {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val fileName = "${today.format(formatter)}.md"
    val filePath = "/storage/self/primary/ObsidianKB/Journal/$fileName"
    val file = File(filePath)

    return if (file.exists()) {
        try {
            file.readText()
        } catch (e: IOException) {
            "Error reading file: ${e.message}"
        }
    } else {
        "Journal file for today not found:\n$filePath"
    }
}

