package com.example.tabletdashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tabletdashboard.ui.App
import com.example.tabletdashboard.viewmodels.AppViewModel
import org.koin.androidx.compose.KoinAndroidContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KoinAndroidContext {
                val viewModel = viewModel<AppViewModel>()
                App(viewModel.isDarkMode.value)
            }
        }
    }
}