package com.example.tabletdashboard.tools

sealed class AsyncState<out T> {
    object Init : AsyncState<Nothing>()
    object Loading : AsyncState<Nothing>()
    data class Success<out T>(val data: T) : AsyncState<T>()
    data class Error(val message: String) : AsyncState<Nothing>()
}