package com.antimo.threeminutes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.sdk15.listeners.onClick

class TimerActivity : AppCompatActivity(), TimerView {

    private lateinit var model: TimerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setListeners()
    }

    private fun setListeners() {
        model = ViewModelProviders.of(this).get(TimerViewModel::class.java)
        model.isRunning().observe(this, Observer { onRunningChanged(it) })
        model.getTimer().observe(this, Observer { onTimerTicked(it) })
        model.getStatus().observe(this, Observer { onStatusChanged(it) })

        button.onClick { onButtonClicked(model.isRunning().value!!) }
    }

    override fun onButtonClicked(isRunning: Boolean) {
        if (!isRunning) {
            model.reset()
            model.start()
        } else {
            model.stop()
        }
    }

    override fun onRunningChanged(isRunning: Boolean) {
        button.text = if (isRunning) getString(R.string.stop) else getString(R.string.start)
    }

    override fun onTimerTicked(secondsLeft: Long) {
        timer.text = secondsLeft.toString()
    }

    override fun onStatusChanged(newFoodStatus: FoodStatus) {
        status.text = newFoodStatus.toString()
    }
}
