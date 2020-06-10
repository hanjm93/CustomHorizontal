package com.ez.myutils

import android.util.Log
import java.util.*

abstract class HorizontalCalendarListener {
    abstract fun onDateSelected(date: Calendar?, position: Int)

    fun onCalendarScroll(calendarView: HorizontalCalendarView?, dx: Int, dy: Int) {
        Log.d("Calendar", "fun onCalendarScroll() dx : ${dx}, dy : $dy")
    }

    fun onDateLongClicked(date: Calendar?, position: Int): Boolean {
        return false
    }
}