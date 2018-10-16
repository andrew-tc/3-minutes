package com.antimo.threeminutes

enum class FoodStatus(private val _threshold: Long) : StatusThreshold {
    RAW(120), CRUNCHY(30), COOKED(-30), SOGGY(Long.MIN_VALUE);

    override fun getThreshold(): Long {
        return _threshold
    }

    companion object {
        fun getStatus(secondsLeft: Long): FoodStatus {
            return values().first { secondsLeft > it.getThreshold() }
        }
    }
}