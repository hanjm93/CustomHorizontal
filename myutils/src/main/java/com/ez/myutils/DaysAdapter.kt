package com.ez.myutils

import android.text.format.DateFormat
import android.util.TypedValue
import android.view.View
import java.util.*

class DaysAdapter(horizontalCalendar: HorizontalCalendar?, startDate: Calendar?, endDate: Calendar?, disablePredicate: HorizontalCalendarPredicate?, eventsPredicate: CalendarEventsPredicate?) :
    HorizontalCalendarBaseAdapter<DateViewHolder?, Calendar?>(R.layout.hc_item_calendar, horizontalCalendar!!, startDate!!, endDate, disablePredicate, eventsPredicate) {

    override fun createViewHolder(itemView: View?, cellWidth: Int): DateViewHolder {
        val holder = DateViewHolder(itemView!!)
        holder.layoutContent.minimumWidth = cellWidth
        holder.constraint.layoutParams.width = cellWidth
        return holder
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val day = getItem(position)
        val config = horizontalCalendar.config
        val selectorColor = horizontalCalendar.config.selectorColor
        if (selectorColor != null) {
            holder.selectionView.setBackgroundColor(selectorColor)
        }
        holder.textMiddle.text = DateFormat.format(
            config.formatMiddleText,
            day
        )
        holder.textMiddle.setTextSize(TypedValue.COMPLEX_UNIT_SP, config.sizeMiddleText)
        if (config.isShowTopText) {
            holder.textTop.text = DateFormat.format(
                config.formatTopText,
                day
            )
            holder.textTop.setTextSize(TypedValue.COMPLEX_UNIT_SP, config.sizeTopText)
        } else {
            holder.textTop.visibility = View.GONE
        }
        if (config.isShowBottomText) {
            holder.textBottom.text = DateFormat.format(
                config.formatBottomText,
                day
            )
            holder.textBottom.setTextSize(TypedValue.COMPLEX_UNIT_SP, config.sizeBottomText)
        } else {
            holder.textBottom.visibility = View.GONE
        }
        showEvents(holder, day)
        applyStyle(holder, day, position)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }
        val date = getItem(position)
        applyStyle(holder, date, position)
    }

    @Throws(IndexOutOfBoundsException::class)
    override fun getItem(position: Int): Calendar {
        if (position >= itemsCount) {
            throw IndexOutOfBoundsException()
        }
        val daysDiff = position - horizontalCalendar.shiftCells
        val calendar = startDate.clone() as Calendar
        calendar.add(Calendar.DATE, daysDiff)
        return calendar
    }

    override fun calculateItemsCount(startDate: Calendar?, endDate: Calendar?): Int {
        var days = 0
        if (startDate != null && endDate != null) {
            days = Utils.daysBetween(startDate, endDate) + 1
        }
        return days + horizontalCalendar.shiftCells * 2
    }


}