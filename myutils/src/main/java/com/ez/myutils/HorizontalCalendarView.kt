package com.ez.myutils

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView

class HorizontalCalendarView : RecyclerView {
    private var defaultStyle: CalendarItemStyle? = null
    private var selectedItemStyle: CalendarItemStyle? = null
    private var config: HorizontalCalendarConfig? = null
    private var shiftCells = 0
    private val FLING_SCALE_DOWN_FACTOR = 0.5f

    constructor(context: Context?) : super(context!!) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.HorizontalCalendarView,
            0, 0
        )
        try {
            val textColorNormal = a.getColor(R.styleable.HorizontalCalendarView_textColorNormal, Color.LTGRAY)
            val colorTopText = a.getColor(R.styleable.HorizontalCalendarView_colorTopText, textColorNormal)
            val colorMiddleText = a.getColor(R.styleable.HorizontalCalendarView_colorMiddleText, textColorNormal)
            val colorBottomText = a.getColor(R.styleable.HorizontalCalendarView_colorBottomText, textColorNormal)
            val textColorSelected = a.getColor(R.styleable.HorizontalCalendarView_textColorSelected, Color.BLACK)
            val colorTopTextSelected = a.getColor(R.styleable.HorizontalCalendarView_colorTopTextSelected, textColorSelected)
            val colorMiddleTextSelected = a.getColor(R.styleable.HorizontalCalendarView_colorMiddleTextSelected, textColorSelected)
            val colorBottomTextSelected = a.getColor(R.styleable.HorizontalCalendarView_colorBottomTextSelected, textColorSelected)
            val selectedDateBackground = a.getDrawable(R.styleable.HorizontalCalendarView_selectedDateBackground)
            val selectorColor = a.getColor(R.styleable.HorizontalCalendarView_selectorColor, fetchAccentColor())
            val sizeTopText = getRawSizeValue(a, R.styleable.HorizontalCalendarView_sizeTopText, HorizontalCalendarConfig.DEFAULT_SIZE_TEXT_TOP)
            val sizeMiddleText = getRawSizeValue(a, R.styleable.HorizontalCalendarView_sizeMiddleText, HorizontalCalendarConfig.DEFAULT_SIZE_TEXT_MIDDLE)
            val sizeBottomText = getRawSizeValue(a, R.styleable.HorizontalCalendarView_sizeBottomText, HorizontalCalendarConfig.DEFAULT_SIZE_TEXT_BOTTOM)
            defaultStyle = CalendarItemStyle(colorTopText, colorMiddleText, colorBottomText, null)
            selectedItemStyle = CalendarItemStyle(colorTopTextSelected, colorMiddleTextSelected, colorBottomTextSelected, selectedDateBackground)
            config = HorizontalCalendarConfig(sizeTopText, sizeMiddleText, sizeBottomText, selectorColor)
        } finally {
            a.recycle()
        }
    }

    /**
     * get the raw value from a complex value ( Ex: complex = 14sp, returns 14)
     */
    private fun getRawSizeValue(a: TypedArray, index: Int, defValue: Float): Float {
        val outValue = TypedValue()
        val result = a.getValue(index, outValue)
        return if (!result) {
            defValue
        } else TypedValue.complexToFloat(outValue.data)
    }

//    override fun fling(velocityX: Int, velocityY: Int): Boolean {
//        val velocityX = velocityX
        // 이거 0.5f (FLING_SCALE_DOWN_FACTOR) 안 곱해주니까 엄청 부드러운 스크롤 가능해짐
//        velocityX *= FLING_SCALE_DOWN_FACTOR.toInt() // (between 0 for no fling, and 1 for normal fling, or more for faster fling).
//        return super.fling(velocityX, velocityY)
//    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        if (isInEditMode) {
            setMeasuredDimension(widthSpec, 150)
        } else {
            super.onMeasure(widthSpec, heightSpec)
        }
    }

    var smoothScrollSpeed: Float
        get() = layoutManager!!.smoothScrollSpeed
        set(smoothScrollSpeed) {
            layoutManager!!.smoothScrollSpeed = smoothScrollSpeed
        }

    override fun getAdapter(): HorizontalCalendarBaseAdapter<*, *>? {
        return super.getAdapter() as HorizontalCalendarBaseAdapter<*, *>?
    }

    override fun getLayoutManager(): HorizontalLayoutManager? {
        return super.getLayoutManager() as HorizontalLayoutManager?
    }

    private fun fetchAccentColor(): Int {
        val typedValue = TypedValue()
        val a = context.obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.colorAccent))
        val color = a.getColor(0, 0)
        a.recycle()
        return color
    }

    // HorizontalCalendar 에서 꽤 중요한 역할을 하는 듯
    fun applyConfigFromLayout(horizontalCalendar: HorizontalCalendar) {
        horizontalCalendar.config.setupDefaultValues(config)
        horizontalCalendar.defaultStyle.setupDefaultValues(defaultStyle)
        horizontalCalendar.selectedItemStyle.setupDefaultValues(selectedItemStyle)

        // clean, not needed anymore
        config = null
        defaultStyle = null
        selectedItemStyle = null
        shiftCells = horizontalCalendar.numberOfDatesOnScreen / 2
    }

    /**
     * @return position of selected date on center of screen
     */
    // layoutManager 가 null 일 경우, -1 을 return
    // 아닐 경우, layoutManager.findFirstCompletelyVisibleItemPosition() 호출
    //      만약 -1 일 경우, -1 을 return
    //      아닐 경우, firstVisiblePosition + shiftCells 를 return
    val positionOfCenterItem: Int
        get() {
            val layoutManager = layoutManager

            return if (layoutManager == null) {
                -1
            } else {
                // firstVisiblePosition 은 스크린에 보여진 날짜의 위치를 index 값으로 나타냄
                val firstVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (firstVisiblePosition == -1) {
                    -1
                } else {
                    firstVisiblePosition + shiftCells
                }
            }
        }
}