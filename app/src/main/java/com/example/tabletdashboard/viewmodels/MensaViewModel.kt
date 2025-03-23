package com.example.tabletdashboard.viewmodels

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabletdashboard.api.MensaMenu
import com.example.tabletdashboard.repositories.MensaRepository
import com.example.tabletdashboard.tools.AsyncState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MensaViewModel(private val mensaRepository: MensaRepository) : ViewModel() {

    private val _menuState = MutableStateFlow<AsyncState<MensaMenu>>(AsyncState.Init)
    val menuState = _menuState.asStateFlow()

    init {
        viewModelScope.launch {
            while(true) {
                val menuResponseState = _menuState.value
                if(menuResponseState == AsyncState.Init || (menuResponseState is AsyncState.Success && menuResponseState.data.isNotCurrent())) {
                    fetchMensaMenu()
                }
                kotlinx.coroutines.delay(60_000)
            }

        }
    }

    private fun MensaMenu.isNotCurrent(): Boolean {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentWeekNumber = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)
        return year != currentYear || this.number != currentWeekNumber
    }

    fun fetchMensaMenu() {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val weekNumber = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)
        _menuState.value = AsyncState.Loading
        viewModelScope.launch {
            mensaRepository.fetchMensaMenu("mensa-arcisstr", year, weekNumber).collect { menu ->
                menu.fold(
                    onSuccess = {
                        _menuState.value = AsyncState.Success(it)
                    },
                    onFailure = {_menuState.value = AsyncState.Error(it.toString())}
                )
            }
        }
    }
}