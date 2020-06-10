package com.ez.myutils

import android.content.Context
import android.graphics.Point
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import java.util.*
import java.util.concurrent.TimeUnit

object Utils {
    fun calculateCellWidth(context: Context, itemsOnScreen: Int): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        val screenWidth = size.x
        return screenWidth / itemsOnScreen
        return ViewGroup.LayoutParams.WRAP_CONTENT
    }

    // HorizontalCalendar 에서 사용되는 코드
    // centerToPositionWithNoAnimation()
    fun calculateRelativeCenterPosition(position: Int, centerItem: Int, shiftCells: Int): Int {
        var relativeCenterPosition = position

        Log.d("Utils", "calculateRelativeCenterPosition()\nposition : ${position}, centerItem : ${centerItem}, shiftCells : $shiftCells")
        // position 먼저 파악해야 해석 가능할 듯
       if (position > centerItem) {
            relativeCenterPosition = position + shiftCells
        } else if (position < centerItem) {
            relativeCenterPosition = position - shiftCells
        }

        return relativeCenterPosition
    }

    /**
     * @return `true` if dates are equal; `false` otherwise
     */
    fun isSameDate(calendar1: Calendar, calendar2: Calendar): Boolean {
        val day = calendar1[Calendar.DAY_OF_MONTH]
        return (isSameMonth(calendar1, calendar2) && day == calendar2[Calendar.DAY_OF_MONTH])
    }

    // 연도랑 달이 같으면 true
    fun isSameMonth(calendar1: Calendar, calendar2: Calendar): Boolean {
        val month = calendar1[Calendar.MONTH]
        val year = calendar1[Calendar.YEAR]
        return (year == calendar2[Calendar.YEAR] && month == calendar2[Calendar.MONTH])
    }

    // date 와 origin(startDate) 를 비교하던데
    fun isDateBefore(date: Calendar, origin: Calendar): Boolean {
        val dayOfYear = date[Calendar.DAY_OF_YEAR]
        val year = date[Calendar.YEAR]

        return if (year < origin[Calendar.YEAR]) {
            true
        } else {
            // 거의 여기서만 호출이 이루어지던데
            // 선택된 날짜인 date 와 startDate 를 비교
            // 거의 false 나옴
            year == origin[Calendar.YEAR] && dayOfYear < origin[Calendar.DAY_OF_YEAR]
        }
    }

    // date 와 origin(endDate) 를 비교하던데
    fun isDateAfter(date: Calendar, origin: Calendar): Boolean {
        val dayOfYear = date[Calendar.DAY_OF_YEAR]
        val year = date[Calendar.YEAR]

        return if (year > origin[Calendar.YEAR]) {
            true
        } else {
            year == origin[Calendar.YEAR] && dayOfYear > origin[Calendar.DAY_OF_YEAR]
        }
    }

    fun daysBetween(startInclusive: Calendar, endExclusive: Calendar): Int {
        zeroTime(startInclusive)
        zeroTime(endExclusive)
        val diff = endExclusive.timeInMillis - startInclusive.timeInMillis
        //result in millis
        // return 값으로는 31, 30 과 같은 일수를 반환함
        return TimeUnit.MILLISECONDS.toDays(diff).toInt()
    }

    fun monthsBetween(startInclusive: Calendar, endExclusive: Calendar): Int {
        val startMonth = startInclusive[Calendar.MONTH]
        val endMonth = endExclusive[Calendar.MONTH]
        val startYear = startInclusive[Calendar.YEAR]
        val endYear = endExclusive[Calendar.YEAR]
        val yearsDiff = endYear - startYear
        return endMonth - startMonth + yearsDiff * 12
    }

    private fun zeroTime(calendar: Calendar) {
        calendar[Calendar.HOUR_OF_DAY] = 0 // 현재 시간 24시간제
        calendar[Calendar.MINUTE] = 0 // 현재 분
        calendar[Calendar.SECOND] = 0 // 현재 초
        calendar[Calendar.MILLISECOND] = 0 // 현재 밀리초
        calendar[Calendar.DST_OFFSET] = 0 // Daylight Saving Time 뭔지 모르겄다
    }
}