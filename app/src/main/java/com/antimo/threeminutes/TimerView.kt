package com.antimo.threeminutes

interface TimerView {
    fun onButtonClicked(isRunning: Boolean)
    fun onRunningChanged(isRunning: Boolean)
    fun onTimerTicked(secondsLeft: Long)
    fun onStatusChanged(newFoodStatus: FoodStatus)
}