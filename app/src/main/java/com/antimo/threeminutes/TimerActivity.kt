package com.antimo.threeminutes

import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_timer.*
import org.jetbrains.anko.sdk15.listeners.onClick
import java.util.*
import kotlin.math.absoluteValue

class TimerActivity : AppCompatActivity() {

    private lateinit var model: TimerViewModel
    private var beeper: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        setListeners()
    }

    override fun onResume() {
        super.onResume()
        setupBeeper()
    }

    override fun onStop() {
        super.onStop()
        beeper?.release()
        beeper = null
    }

    private fun setListeners() {
        model = ViewModelProviders.of(this).get(TimerViewModel::class.java)
        model.getTimerStatus().observe(this, Observer { onTimerStatusChanged(it) })
        model.getTimer().observe(this, Observer { onTimerChanged(it) })
        model.getFoodStatus().observe(this, Observer { onFoodStatusChanged(it) })

        button.onClick { onButtonClicked(model.getTimerStatus().value!!) }
    }

    private fun setupBeeper() {
        beeper = MediaPlayer()

        // set source
        val afd = resources.openRawResourceFd(R.raw.beep) ?: return
        beeper?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        afd.close()

        // set stream
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val attributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            beeper?.setAudioAttributes(attributes)
        } else {
            beeper?.setAudioStreamType(AudioManager.STREAM_ALARM)
        }

        beeper?.prepareAsync()
    }

    private fun onButtonClicked(status: TimerStatus) {
        when (status) {
            TimerStatus.RESET -> model.start()
            TimerStatus.START -> model.stop()
            TimerStatus.STOP -> model.reset()
        }
    }

    private fun onTimerStatusChanged(newStatus: TimerStatus) {
        button.text = when (newStatus) {
            TimerStatus.RESET -> getString(R.string.start)
            TimerStatus.START -> getString(R.string.stop)
            TimerStatus.STOP -> getString(R.string.reset)
        }
    }

    private fun onTimerChanged(secondsLeft: Long) {
        // set progress
        if (model.getTimerStatus().value == TimerStatus.RESET) {
            progress.max = secondsLeft.toInt()
        }
        progress.progress = secondsLeft.toInt()

        // set text
        val minutes = (secondsLeft / 60).absoluteValue
        val seconds = (secondsLeft % 60).absoluteValue
        var clock = String.format(Locale.US, "%02d:%02d", minutes, seconds)

        if (secondsLeft < 0) {
            clock = "-$clock"
            timer.setTextColor(Color.RED)
        } else {
            timer.setTextColor(Color.BLACK)
        }
        timer.text = clock

        // play beep
        if (secondsLeft < 10) {
            beeper?.start()
        }
    }

    private fun onFoodStatusChanged(newStatus: FoodStatus) {
        status.text = newStatus.toString()
    }
}
