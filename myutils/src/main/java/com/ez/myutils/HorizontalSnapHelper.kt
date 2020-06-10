package com.ez.myutils

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView

class HorizontalSnapHelper : LinearSnapHelper() {
    private var horizontalCalendar: HorizontalCalendar? = null
    private var calendarView: HorizontalCalendarView? = null

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        val snapView = super.findSnapView(layoutManager)
        Log.d("Helper", "findSnapView() ${calendarView!!.scrollState}")
        // scrollState 에 대한 조사 필요
        if (calendarView!!.scrollState != RecyclerView.SCROLL_STATE_DRAGGING) {
            val selectedItemPosition: Int

            selectedItemPosition = if (snapView == null) {
                // no snapping required
                // selectedDatePosition 은 calendarView.positionOfCenterItem 을 return
                Log.d("Helper", "Nice snapView == null... so selectedItemPosition = ${horizontalCalendar!!.selectedDatePosition}")
                horizontalCalendar!!.selectedDatePosition
            } else {
                val snapDistance = calculateDistanceToFinalSnap(layoutManager, snapView)!!

                if (snapDistance[0] != 0 || snapDistance[1] != 0) {
                    return snapView
                }
                // 선택한 날짜의 index 위치를 반환
                layoutManager.getPosition(snapView)
            }
            notifyCalendarListener(selectedItemPosition)
        }

        return snapView
    }

    private fun notifyCalendarListener(selectedItemPosition: Int) {
        if (!horizontalCalendar!!.isItemDisabled(selectedItemPosition)) {
            horizontalCalendar!!.calendarListener?.onDateSelected(horizontalCalendar!!.getDateAt(selectedItemPosition), selectedItemPosition)
        }
    }

    @Throws(IllegalStateException::class)
    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        // Do nothing
    }

    @Throws(IllegalStateException::class)
    fun attachToHorizontalCalendar(horizontalCalendar: HorizontalCalendar?) {
        this.horizontalCalendar = horizontalCalendar
        calendarView = horizontalCalendar!!.calendarView
        super.attachToRecyclerView(calendarView)
    }
}