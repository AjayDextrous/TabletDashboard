package com.example.tabletdashboard.viewmodels

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PomodoroTimerViewModel : ViewModel() {

    private val _selectedTime = MutableStateFlow(30 * 60 * 1000L)
    val selectedTime: StateFlow<Long> = _selectedTime.asStateFlow()

    private val _remainingTime = MutableStateFlow(_selectedTime.value)
    val remainingTime: StateFlow<Long> = _remainingTime.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private var timer: CountDownTimer? = null
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)

    fun startTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(_remainingTime.value, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _remainingTime.value = millisUntilFinished
            }

            override fun onFinish() {
                _isRunning.value = false
                toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 500)
                timer?.cancel()
                _remainingTime.value = _selectedTime.value
            }
        }.start()
        _isRunning.value = true
    }

    fun stopTimer() {
        timer?.cancel()
        _isRunning.value = false
    }

    fun resetTimer(newTime: Long = _selectedTime.value) {
        timer?.cancel()
        _selectedTime.value = newTime
        _remainingTime.value = newTime
        _isRunning.value = false
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
        toneGenerator.release()
    }
}


