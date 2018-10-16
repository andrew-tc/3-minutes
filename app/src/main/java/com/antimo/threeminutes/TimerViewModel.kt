package com.antimo.threeminutes

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.TimeUnit

class TimerViewModel : ViewModel() {

    private lateinit var timerStatus: MutableLiveData<TimerStatus>
    private lateinit var timer: MutableLiveData<Long>
    private lateinit var foodStatus: MutableLiveData<FoodStatus>

    private val timerHandler: Handler = Handler()
    private lateinit var timerRunnable: Runnable

    init {
        timerRunnable = Runnable {
            timer.postValue(timer.value?.minus(1))

            val secondsLeft = timer.value!!
            foodStatus.value = FoodStatus.getStatus(secondsLeft)

            timerHandler.postDelayed(timerRunnable, TimeUnit.SECONDS.toMillis(1))
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerHandler.removeCallbacks(timerRunnable)
    }

    fun getTimerStatus(): MutableLiveData<TimerStatus> {
        if (!::timerStatus.isInitialized) {
            timerStatus = MutableLiveData()
            timerStatus.value = TimerStatus.RESET
        }
        return timerStatus
    }

    fun getTimer(): MutableLiveData<Long> {
        if (!::timer.isInitialized) {
            timer = MutableLiveData()
            timer.value = TimeUnit.MINUTES.toSeconds(3)
        }
        return timer
    }

    fun getFoodStatus(): MutableLiveData<FoodStatus> {
        if (!::foodStatus.isInitialized) {
            foodStatus = MutableLiveData()
            foodStatus.value = FoodStatus.RAW
        }
        return foodStatus
    }

    fun reset() {
        timerStatus.value = TimerStatus.RESET
        timer.value = TimeUnit.MINUTES.toSeconds(3)
        foodStatus.value = FoodStatus.RAW
    }

    fun start() {
        timerStatus.value = TimerStatus.START
        timerHandler.postDelayed(timerRunnable, TimeUnit.SECONDS.toMillis(1))
    }

    fun stop() {
        timerStatus.value = TimerStatus.STOP
        timerHandler.removeCallbacks(timerRunnable)
    }
}