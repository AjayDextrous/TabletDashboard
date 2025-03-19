package com.example.tabletdashboard.viewmodels

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabletdashboard.api.MensaMenuResponse
import com.example.tabletdashboard.repositories.MensaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MensaViewModel : ViewModel() {

    private val mensaRepository = MensaRepository()

    private val _menuState = MutableStateFlow<MensaMenuResponse?>(null)
    val menuState = _menuState.asStateFlow()

    fun fetchMensaMenu() {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val weekNumber = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)
        viewModelScope.launch {
            mensaRepository.fetchMensaMenu("mensa-arcisstr", year, weekNumber).collect { menu ->
                _menuState.value = menu
            }
        }
    }
}