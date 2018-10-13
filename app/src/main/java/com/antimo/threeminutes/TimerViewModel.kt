package com.antimo.threeminutes

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.TimeUnit

class TimerViewModel : ViewModel() {

    private lateinit var running: MutableLiveData<Boolean>
    private lateinit var timer: MutableLiveData<Long>
    private lateinit var status: MutableLiveData<FoodStatus>

    private val timerHandler: Handler = Handler()
    private lateinit var timerRunnable: Runnable

    init {
        timerRunnable = Runnable {
            timer.postValue(timer.value?.minus(1))

            val secondsLeft = timer.value!!
            when {
                secondsLeft > 120 -> status.value = FoodStatus.RAW
                secondsLeft > 30 -> status.value = FoodStatus.CRUNCHY
                secondsLeft > -30 -> status.value = FoodStatus.COOKED
                else -> status.value = FoodStatus.SOGGY
            }

            timerHandler.postDelayed(timerRunnable, TimeUnit.SECONDS.toMillis(1))
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerHandler.removeCallbacks(timerRunnable)
    }

    fun isRunning(): MutableLiveData<Boolean> {
        if (!::running.isInitialized) {
            running = MutableLiveData()
            running.value = false
        }
        return running
    }

    fun getTimer(): MutableLiveData<Long> {
        if (!::timer.isInitialized) {
            timer = MutableLiveData()
            timer.value = TimeUnit.MINUTES.toSeconds(3)
        }
        return timer
    }

    fun getStatus(): MutableLiveData<FoodStatus> {
        if (!::status.isInitialized) {
            status = MutableLiveData()
            status.value = FoodStatus.RAW
        }
        return status
    }

    fun reset() {
        running.value = false
        timer.value = TimeUnit.MINUTES.toSeconds(3)
        status.value = FoodStatus.RAW
    }

    fun start() {
        running.value = true
        timerHandler.postDelayed(timerRunnable, TimeUnit.SECONDS.toMillis(1))
    }

    fun stop() {
        running.value = false
        timerHandler.removeCallbacks(timerRunnable)
    }
}