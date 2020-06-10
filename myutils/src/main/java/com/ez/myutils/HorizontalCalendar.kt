package com.ez.myutils

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class HorizontalCalendar internal constructor(
    builder: Builder, config: HorizontalCalendarConfig, defaultStyle: CalendarItemStyle, selectedItemStyle: CalendarItemStyle
) {

    enum class Mode {
        DAYS, MONTHS
    }

    //region private Fields
    var calendarView: HorizontalCalendarView? = null
    private var mCalendarAdapter: HorizontalCalendarBaseAdapter<*, *>? = null

    // Start & End Dates
    // Builder class 에서도 있는 변수니까 안 헷갈리게 조심
    var startDate: Calendar? = null
    var endDate: Calendar? = null

    // Calendar Mode
    // Builder class 에서도 있는 변수니까 안 헷갈리게 조심
    // mode 를 따로 설정하지 않아 null 일 경우, mode = Mode.DAYS
    private val mode: Mode?

    // Number of Dates to Show on Screen
    val numberOfDatesOnScreen: Int

    // Interface events
    var calendarListener: HorizontalCalendarListener? = null

    private val calendarId: Int

    /* Format, Colors & Font Sizes*/
    val defaultStyle: CalendarItemStyle
    val selectedItemStyle: CalendarItemStyle
    val config: HorizontalCalendarConfig

    /* Init Calendar View */
    // 현재 disablePredicate, eventsPredicate 값은 전부 null
    fun init(rootView: View, defaultSelectedDate: Calendar?, disablePredicate: HorizontalCalendarPredicate?, eventsPredicate: CalendarEventsPredicate?) {
        var disablePredicate = disablePredicate
        // calendarId 는 MainActivity 에서 R.id.calendarView 선언
        calendarView = rootView.findViewById(calendarId)
        calendarView?.setHasFixedSize(true)
        calendarView?.isHorizontalScrollBarEnabled = false
        calendarView?.applyConfigFromLayout(this)

        // snapHelper 는 날짜마다 스프링 튕기는 튕기는 효과니까 나중에 보자?
        val snapHelper = HorizontalSnapHelper()
        snapHelper.attachToHorizontalCalendar(this)

        disablePredicate = if (disablePredicate == null) {
            // 현재 disablePredicate = null 이기 때문에 disablePredicate = defaultDisablePredicate
            defaultDisablePredicate
        } else {
            HorizontalCalendarPredicate.Or(disablePredicate, defaultDisablePredicate)
        }

        mCalendarAdapter = if (mode == Mode.MONTHS) {
            MonthsAdapter(this, startDate, endDate, disablePredicate, eventsPredicate)
        } else {
            // mode = null 일 경우 mode = Mode.DAYS 로 선언
            // class Builder 에서 이야기 했음
            DaysAdapter(this, startDate, endDate, disablePredicate, eventsPredicate)
        }

        calendarView?.adapter = mCalendarAdapter
        calendarView?.layoutManager = HorizontalLayoutManager(calendarView?.context, false)
        calendarView?.addOnScrollListener(HorizontalCalendarScrollListener())

        // 여기 있는 코드가 애니메이션 없이 position 을 가운데로 옮겨주는 코드인 것 같음
        // defaultSelectedDate 는 진짜 오늘 날짜를 말하는 것 같음
        // 주석처리하니까 앱이 실행되면서 오늘 날짜를 보여주지 않고 startDate 를 보여줌
        post(Runnable {
            centerToPositionWithNoAnimation(positionOfDate(defaultSelectedDate))
        })

    }

    /**
     * Select today date and center the Horizontal Calendar to this date
     *
     * @param immediate pass true to make the calendar scroll as fast as possible to reach the date of today
     * ,or false to play default scroll animation speed.
     */
    fun goToday(immediate: Boolean) {
        selectDate(Calendar.getInstance(), immediate)
    }

    /**
     * Select the date and center the Horizontal Calendar to this date
     *
     * @param date      The date to select
     * @param immediate pass true to make the calendar scroll as fast as possible to reach the target date
     * ,or false to play default scroll animation speed.
     */
    // goToday() 함수가 사용되어야 selectDate() 가 실행되는데 여기는 아직 사용 안 함
    private fun selectDate(date: Calendar?, immediate: Boolean) {
        val datePosition = positionOfDate(date)
        if (immediate) {
            centerToPositionWithNoAnimation(datePosition)
            if (calendarListener != null) {
                calendarListener!!.onDateSelected(date, datePosition)
            }
        } else {
            calendarView!!.smoothScrollSpeed = HorizontalLayoutManager.SPEED_NORMAL
            centerCalendarToPosition(datePosition)
        }
    }

    /**
     * Smooth scroll Horizontal Calendar to center this position and select the new centered day.
     *
     * @param position The position to center the calendar to!
     */
    fun centerCalendarToPosition(position: Int) {
        if (position != -1) {
            Log.d("Calendar", "calculateRelativeCenterPosition() centerCalendarToPosition")
            val relativeCenterPosition = Utils.calculateRelativeCenterPosition(position, calendarView!!.positionOfCenterItem, numberOfDatesOnScreen / 2)
            if (relativeCenterPosition == position) {
                return
            }
            calendarView!!.smoothScrollToPosition(relativeCenterPosition)
        }
    }

    /**
     * Scroll Horizontal Calendar to center this position and select the new centered day.
     *
     * @param position The position to center the calendar to!
     */
    private fun centerToPositionWithNoAnimation(position: Int) {
        if (position != -1) {
            // positionOfCenterItem 은
            // layoutManager 가 null 일 경우값 -1 을 return
            // 그렇지 않을 경우 findFirstCompletelyVisibleItemPosition() 에 shiftCells 더한 값을 return
            // shiftCells 는 사용자가 화면에 원하는 numberOfDatesScreen / 2
            // 이거 왜 자꾸 시작값이 왜 2가 나오는지 모르겠음
            // position 이 실제 가운데 있는 값의 index 를 나타내고 있음 
            val oldSelectedItem = calendarView!!.positionOfCenterItem
            Log.d("Calendar", "calculateRelativeCenterPosition() $oldSelectedItem")
            val relativeCenterPosition = Utils.calculateRelativeCenterPosition(position, oldSelectedItem, numberOfDatesOnScreen / 2)

            if (relativeCenterPosition == position) {
                return
            }

            // layoutManager 에서만 scrollToPositionWithOffset 설정 가능
            // RecyclerView 를 확장하는 경우 scrollToPosition 설정 가능
            // 스크롤 시 중간에 약간 끊기는 듯한 느낌이 있음
//            calendarView!!.layoutManager!!.scrollToPositionWithOffset(relativeCenterPosition, 0)
            calendarView!!.scrollToPosition(relativeCenterPosition)
//            calendarView!!.post {
            // HorizontalCalendarView 에서
            // layoutManager 가 null 일 경우, -1 을 return
            // 그렇지 않을 경우 findFirstCompletelyVisibleItemPosition() 소환
            //      만약 그 값이 null 일 경우, -1 을 return
            //      그렇지 않을 경우 findVisiblePosition + shiftCells 를 return
            val newSelectedItem = calendarView!!.positionOfCenterItem
            //refresh to update background colors
            // 여기 주석처리해도 코드에는 문제 없어보임
//                refreshItemsSelector(newSelectedItem, oldSelectedItem)
//            }
        }
    }

    fun refreshItemsSelector(position1: Int, vararg positions: Int) {
        // 아주 잠깐 position1 의 값이 2 뜸
        // 이후 제대로 된 position1 의 값 37 뜸
        // 날짜를 클릭할 때마다 2번 실행되는데,
        //      처음 position1 은 현재 날짜 index
        //      다음 position1 은 이전 날짜 index
        Log.d("HorizontalCalendar", "refreshItemsSelector pos1 : ${position1}, pos : $positions")
        mCalendarAdapter!!.notifyItemChanged(position1, "UPDATE_SELECTOR")
        if (positions.isNotEmpty()) {
            for (pos in positions) {
                mCalendarAdapter!!.notifyItemChanged(pos, "UPDATE_SELECTOR")
            }
        }
    }

    fun isItemDisabled(position: Int): Boolean {
        return mCalendarAdapter!!.isDisabled(position)
    }

    fun refresh() {
        mCalendarAdapter!!.notifyDataSetChanged()
    }

    fun show() {
        calendarView!!.visibility = View.VISIBLE
    }

    fun hide() {
        calendarView!!.visibility = View.INVISIBLE
    }

    //
    fun post(runnable: Runnable?) {
        calendarView!!.post(runnable)
    }

    @TargetApi(21)
    fun setElevation(elevation: Float) {
        calendarView!!.elevation = elevation
    }

    /**
     * @return the current selected date
     */
    val selectedDate: Calendar
        get() = mCalendarAdapter!!.getItem(calendarView!!.positionOfCenterItem)!!

    /**
     * @return position of selected date in Horizontal Calendar
     */
    val selectedDatePosition: Int
        get() = calendarView!!.positionOfCenterItem

    /**
     * @param position The position of date
     * @return the date on this index
     * @throws IndexOutOfBoundsException if position is out of the calendar range
     */
    @Throws(IndexOutOfBoundsException::class)
    fun getDateAt(position: Int): Calendar {
        return mCalendarAdapter!!.getItem(position)!!
    }

    /**
     * @param date The date to search for
     * @return true if the calendar contains this date or false otherwise
     */
    operator fun contains(date: Calendar?): Boolean {
        return positionOfDate(date) != -1
    }

    // context 를 이런식으로 호출하는 것이 메모리 낭비에 더 효과적일까?
    val context: Context
        get() = calendarView!!.context

    fun setRange(startDate: Calendar?, endDate: Calendar?) {
        this.startDate = startDate
        this.endDate = endDate
        mCalendarAdapter!!.update(startDate!!, endDate, false)
    }

    // numberOfDateOnScreen 은 내가 원하는 스크린에 출력되길 바라는 날짜 개수
    val shiftCells: Int
        get() = numberOfDatesOnScreen / 2

    /**
     * @return position of date in Calendar, or -1 if date does not exist
     */
    // date 에는 오늘 날짜 Calendar 값이 들어감
    private fun positionOfDate(date: Calendar?): Int {
        // 일단 if 문은 둘다 false 나옴
        // 그냥 확인 차 startDate 랑 endDate 랑 뭔가 안 맞을까봐 안정적으로 작성한 코드인 듯
        if (Utils.isDateBefore(date!!, startDate!!) || Utils.isDateAfter(date, endDate!!)) {
            return -1
        }

        // 지금까지 mode 는 항상 Mode.DAYS
        val position: Int = if (mode == Mode.DAYS) {
            Log.d("Calendar", "Utils ${Utils.isSameDate(date, startDate!!)}")
            if (Utils.isSameDate(date, startDate!!)) {
                // date 와 startDate 가 같다면 position = 0
                0
            } else {
                // 아니라면 차이나는 날짜 수를 구함
                Utils.daysBetween(startDate!!, date)
            }
        } else {
            if (Utils.isSameMonth(date, startDate!!)) {
                0
            } else {
                Utils.monthsBetween(startDate!!, date)
            }
        }
        val shiftCells = numberOfDatesOnScreen / 2

        return position + shiftCells
    }

    class Builder {
        // viewId 는 이미 MainActivity 에서 만들 때 값이 들어감
        val viewId: Int
        private val rootView: View

        // Start & End Dates
        var startDate: Calendar? = null
        var endDate: Calendar? = null
        private var defaultSelectedDate: Calendar? = null
        var mode: Mode? = null

        // Number of Days to Show on Screen
        var numberOfDatesOnScreen = 0

        // Specified which dates should be disabled
        private var disablePredicate: HorizontalCalendarPredicate? = null

        // Add events to each Date
        private var eventsPredicate: CalendarEventsPredicate? = null
        private var configBuilder: ConfigBuilder? = null

        /**
         * @param rootView pass the rootView for the Fragment where HorizontalCalendar is attached
         * @param viewId   the id specified for HorizontalCalendarView in your layout
         */
        constructor(rootView: View, viewId: Int) {
            this.rootView = rootView
            this.viewId = viewId
        }

        // 여기서는 MainActivity 의 Activity 를 param 으로 받게 됨
        /**
         * @param activity pass the activity where HorizontalCalendar is attached
         * @param viewId   the id specified for HorizontalCalendarView in your layout
         */
        constructor(activity: Activity, viewId: Int) {
            rootView = activity.window.decorView
            this.viewId = viewId
        }

        // MainActivity 에서 호출할 코드
        // 시작할 날짜와 끝나는 날짜를 param 으로 받음
        fun range(startDate: Calendar?, endDate: Calendar?): Builder {
            this.startDate = startDate
            this.endDate = endDate
            return this
        }

        // 이 함수를 쓴 적이 없기 때문에 mode = null
        // 하지만 이후 initDefaultValues() 에서 mode = Mode.DAYS 선언
        fun mode(mode: Mode?): Builder {
            this.mode = mode
            return this
        }

        // MainActivity 에서 호출할 코드
        // 화면에서 날짜 몇 개까지 보여줄껀지
        fun datesNumberOnScreen(numberOfItemsOnScreen: Int): Builder {
            numberOfDatesOnScreen = numberOfItemsOnScreen
            return this
        }

        // MainActivity 에서 호출할 코드
        // 오늘 날짜를 param 으로 집어넣어야겠지
        fun defaultSelectedDate(date: Calendar?): Builder {
            defaultSelectedDate = date
            return this
        }

        // 이 함수를 쓴 적이 없기 때문에 disablePredicate = null
        fun disableDates(predicate: HorizontalCalendarPredicate?): Builder {
            disablePredicate = predicate
            return this
        }

        // 이 함수를 쓴 적이 없기 때문에 eventsPredicate = null
        fun addEvents(predicate: CalendarEventsPredicate?): Builder {
            eventsPredicate = predicate
            return this
        }

        // MainActivity 에서 호출할 코드
        // configure() 선언 이후 ConfigBuilder 내부 함수를 사용할 수 있음
        fun configure(): ConfigBuilder {
            if (configBuilder == null) {
                configBuilder = ConfigBuilder(this)
            }
            return configBuilder!!
        }

        @Throws(IllegalStateException::class)
        private fun initDefaultValues() {
            /* Defaults variables */
            check(!(startDate == null || endDate == null)) {
                "HorizontalCalendar 범위가 정해지지 않았다는데, startDate 랑 endDate 전부 null!"
            }

            if (mode == null) {
                mode = Mode.DAYS
            }

            // Screen 에 보여질 date 개수를 말하는 코드
            // 설정하지 않는다면 기본으로 5개의 날짜를 보여줌
            if (numberOfDatesOnScreen <= 0) {
                numberOfDatesOnScreen = 5
            }

            // 당연히 오늘을 가르켜야 함
            if (defaultSelectedDate == null) {
                defaultSelectedDate = Calendar.getInstance()
            }
        }

        // MainActivity 에서 제일 마지막에 호출되는 함수
        /**
         * @return Instance of [HorizontalCalendar] initiated with builder settings
         */
        @Throws(IllegalStateException::class)
        fun build(): HorizontalCalendar {
            initDefaultValues()

            // 이미 configure() 함수 부분 에서 configBuilder 는 생성되었지만
            // 혹여나 생성되지 않았을 경우를 대비하는 코드
            if (configBuilder == null) {
                configBuilder = ConfigBuilder(this)
                configBuilder!!.end()
            }
            val defaultStyle = configBuilder!!.createDefaultStyle()
            val selectedItemStyle = configBuilder!!.createSelectedItemStyle()
            val config = configBuilder!!.createConfig()

            val horizontalCalendar = HorizontalCalendar(this, config, defaultStyle, selectedItemStyle)
            horizontalCalendar.init(rootView, defaultSelectedDate, disablePredicate, eventsPredicate)

            return horizontalCalendar
        }
    }

    // 이건 또 뭐하는 변수인지... 위에서 disablePredicate 가 null 이라서
    // disablePredicate = defaultDisablePredicate 로 실행됨
    private val defaultDisablePredicate: HorizontalCalendarPredicate = object : HorizontalCalendarPredicate {
        override fun test(date: Calendar?): Boolean {
            return Utils.isDateBefore(date!!, startDate!!) || Utils.isDateAfter(date, endDate!!)
        }

        override fun style(): CalendarItemStyle {
            return CalendarItemStyle(Color.GRAY, null)
        }
    }

    private inner class HorizontalCalendarScrollListener internal constructor() : RecyclerView.OnScrollListener() {
        var lastSelectedItem = -1
        val selectedItemRefresher: Runnable = SelectedItemRefresher()

        // 클릭을 하던 스크롤을 하던 발생하는 함수
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            //On Scroll, agenda is refresh to update background colors
            Log.d("Calendar", "fun onScrolled()")
            post(selectedItemRefresher)

            // 여기서 스크롤을 관리하는 것 같은데 좀만 더 살펴보자
//            if (calendarListener != null) {
//                Log.d("Calender", "fun onScrolled() calendarListener != null")
//                calendarListener!!.onCalendarScroll(calendarView, dx, dy)
//            }
        }

        private inner class SelectedItemRefresher internal constructor() : Runnable {
            override fun run() {
                Log.d("CalendarView", "cycle class SelectedItemRefresher run()")
                val positionOfCenterItem = calendarView!!.positionOfCenterItem
                if (lastSelectedItem == -1 || lastSelectedItem != positionOfCenterItem) {
                    //On Scroll, agenda is refresh to update background colors
                    // 이 코드를 주석처리하면 앱 시작할 때, 어색한 애니메이션은 없어지지만
                    // 다른 날짜를 클릭하면 오늘 날짜 표시, 선택된 날짜 글자 색 강조 효과들이 사라짐
                    Log.d("Calendar", "step class SelectedItemRefresher run()")
                    refreshItemsSelector(positionOfCenterItem)

                    if (lastSelectedItem != -1) {
                        Log.d("Calendar", "stop class SelectedItemRefresher lastSelectedItem != -1 run()")
                        refreshItemsSelector(lastSelectedItem)
                    }
                    lastSelectedItem = positionOfCenterItem
                }
            }
        }
    }
    //endregion
    /**
     * Private Constructor to insure HorizontalCalendar can't be initiated the default way
     */
    init {
        numberOfDatesOnScreen = builder.numberOfDatesOnScreen
        calendarId = builder.viewId
        startDate = builder.startDate
        endDate = builder.endDate
        this.config = config
        this.defaultStyle = defaultStyle
        this.selectedItemStyle = selectedItemStyle
        mode = builder.mode
    }
}