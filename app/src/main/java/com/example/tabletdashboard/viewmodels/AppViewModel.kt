package com.example.tabletdashboard.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AppViewModel: ViewModel() {

    val isDarkMode = mutableStateOf(true)
}