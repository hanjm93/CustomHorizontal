package com.ez.myutils

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

abstract class HorizontalCalendarBaseAdapter<VH : DateViewHolder?, T : Calendar?> protected constructor(
    private val itemResId: Int,
    val horizontalCalendar: HorizontalCalendar,
    protected var startDate: Calendar,
    endDate: Calendar?,
    private val disablePredicate: HorizontalCalendarPredicate?,
    eventsPredicate: CalendarEventsPredicate?
) : RecyclerView.Adapter<VH>() {

    private val eventsPredicate: CalendarEventsPredicate?
    private val cellWidth: Int
    private var disabledItemStyle: CalendarItemStyle? = null

    @JvmField
    protected var itemsCount: Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context).inflate(itemResId, parent, false)
        val viewHolder = createViewHolder(itemView, cellWidth)
        viewHolder!!.itemView.setOnClickListener(MyOnClickListener(viewHolder))
        viewHolder.itemView.setOnLongClickListener(MyOnLongClickListener(viewHolder))

        if (eventsPredicate != null) {
            initEventsRecyclerView(viewHolder.eventsRecyclerView)
        } else {
            viewHolder.eventsRecyclerView.visibility = View.GONE
        }

        return viewHolder
    }

    // 여기 코드는 나중에 이벤트 할 일 있으면 보자?
    private fun initEventsRecyclerView(recyclerView: RecyclerView) {
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = EventsAdapter(emptyList())
        val layoutManager = GridLayoutManager(recyclerView.context, 4)
        recyclerView.layoutManager = layoutManager
    }

    // abstract 로 만든 함수인데 return 값이 VH 로만 끝나도 되는게 신기함. DateViewHolder 형식으로 반환하는 듯.
    protected abstract fun createViewHolder(itemView: View?, cellWidth: Int): VH

    // 위 함수와 마찬가지로 Calendar 형식으로 값을 반환하는 듯.
    abstract fun getItem(position: Int): T

    override fun getItemCount(): Int {
        return itemsCount
    }

    fun isDisabled(position: Int): Boolean {
        if (disablePredicate == null) {
            return false
        }
        val date: Calendar = getItem(position)!!
        return disablePredicate.test(date)
    }

    protected fun showEvents(viewHolder: VH, date: Calendar?) {
        if (eventsPredicate == null) {
            return
        }
        val events = eventsPredicate.events(date)
        if (events == null || events.isEmpty()) {
            viewHolder!!.eventsRecyclerView.visibility = View.GONE
        } else {
            viewHolder!!.eventsRecyclerView.visibility = View.VISIBLE
            val eventsAdapter = viewHolder.eventsRecyclerView.adapter as EventsAdapter?
            eventsAdapter!!.update(events)
        }
    }

    protected fun applyStyle(viewHolder: VH, date: Calendar?, position: Int) {
        // selectedItemPosition 은 selectedDatePosition 을 부르고
        // selectedDatePosition 은 calendarView.positionOfCenterItem 을 부름
        val selectedItemPosition = horizontalCalendar.selectedDatePosition
        if (disablePredicate != null) {
            val isDisabled = disablePredicate.test(date)
            viewHolder!!.itemView.isEnabled = !isDisabled
            if (isDisabled && disabledItemStyle != null) {
                applyStyle(viewHolder, disabledItemStyle!!)
                viewHolder.selectionView.visibility = View.INVISIBLE
                return
            }
        }

        // Selected Day
        if (position == selectedItemPosition) {
            applyStyle(viewHolder, horizontalCalendar.selectedItemStyle)
            viewHolder!!.selectionView.visibility = View.VISIBLE
        } else {
            applyStyle(viewHolder, horizontalCalendar.defaultStyle)
            viewHolder!!.selectionView.visibility = View.INVISIBLE
        }
    }

    private fun applyStyle(viewHolder: VH, itemStyle: CalendarItemStyle) {
        viewHolder!!.textTop.setTextColor(itemStyle.colorTopText)
        viewHolder.textMiddle.setTextColor(itemStyle.colorMiddleText)
        viewHolder.textBottom.setTextColor(itemStyle.colorBottomText)
        if (Build.VERSION.SDK_INT >= 16) {
            viewHolder.itemView.background = itemStyle.background
        } else {
            viewHolder.itemView.setBackgroundDrawable(itemStyle.background)
        }
    }

    fun update(startDate: Calendar, endDate: Calendar?, notify: Boolean) {
        this.startDate = startDate
        itemsCount = calculateItemsCount(startDate, endDate)
        if (notify) {
            notifyDataSetChanged()
        }
    }

    // 이렇게 추상 함수로 선언을 하면 코드 작동은 어떻게 하는건지 이해가 안가네?
    protected abstract fun calculateItemsCount(startDate: Calendar?, endDate: Calendar?): Int

    // ClickListener 를 따로 만들어서 사용함
    // viewHolder 를 초기 인자값으로 받음 (adapterPosition 을 받기 위함)
    private inner class MyOnClickListener internal constructor(private val viewHolder: RecyclerView.ViewHolder) : View.OnClickListener {
        override fun onClick(v: View) {
            val position = viewHolder.adapterPosition
            if (position == -1) return

            horizontalCalendar.calendarView?.smoothScrollSpeed = HorizontalLayoutManager.SPEED_SLOW
            horizontalCalendar.centerCalendarToPosition(position)
        }

    }

    // LongClickListener 를 제작해서 사용
    // adapterPosition 을 받기 위해, 초기 인자값으로 viewHolder 를 받음
    private inner class MyOnLongClickListener internal constructor(private val viewHolder: RecyclerView.ViewHolder) : OnLongClickListener {
        override fun onLongClick(v: View): Boolean {
            val calendarListener = horizontalCalendar.calendarListener ?: return false
            val position = viewHolder.adapterPosition
            val date: Calendar = getItem(position)!!

            return calendarListener.onDateLongClicked(date, position)
        }

    }

    init {
        if (disablePredicate != null) {
            disabledItemStyle = disablePredicate.style()
        }
        this.eventsPredicate = eventsPredicate
        cellWidth = Utils.calculateCellWidth(horizontalCalendar.context, horizontalCalendar.numberOfDatesOnScreen)
        itemsCount = calculateItemsCount(startDate, endDate)
    }
}