package com.ez.customhorizontal

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.ez.myutils.HorizontalCalendarView
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        val defaultSelectedDate = Calendar.getInstance()
//
//        val startDate = Calendar.getInstance()
//        startDate.add(Calendar.MONTH, -1)
//
//        val endDate = Calendar.getInstance()
//        endDate.add(Calendar.MONTH, 1)
//
//        val builder = HorizontalCalendar.Builder(this, R.id.calendarView)
//            .range(startDate, endDate)
//            .datesNumberOnScreen(7)
//            .defaultSelectedDate(defaultSelectedDate)
//            .configure()
//            .formatTopText("MMM")
//            .formatMiddleText("dd")
//            .formatBottomText("EEE")
//            .showTopText(true)
//            .showBottomText(true)
//            .textColor(Color.LTGRAY, Color.WHITE)
//            .colorTextMiddle(Color.LTGRAY, Color.parseColor("#ffd54f"))
//            .end()
//
//        builder.build().calendarListener = object : HorizontalCalendarListener() {
//            override fun onDateSelected(date: Calendar?, position: Int) {
//                toast?.cancel()
//                toast = Toast.makeText(this@MainActivity, SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA).format(date!!.time), Toast.LENGTH_LONG)
//                Log.d("MainActivity", "listener : $date")
//                toast?.show()
//            }
//        }
    }
}