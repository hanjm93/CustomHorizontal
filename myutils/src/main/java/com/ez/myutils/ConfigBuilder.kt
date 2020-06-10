package com.ez.myutils

import android.graphics.drawable.Drawable

class ConfigBuilder(private val calendarBuilder: HorizontalCalendar.Builder) {
    /* Format & Font Sizes*/
    private var sizeTopText = 0f
    private var sizeMiddleText = 0f
    private var sizeBottomText = 0f

    private var selectorColor: Int? = null

    private var formatTopText: String? = null
    private var formatMiddleText: String? = null
    private var formatBottomText: String? = null

    private var showTopText = true
    // showMiddleText 는 무조건 true 겠지?
    private var showBottomText = true

    /* Colors and Background*/
    private var colorTextTop = 0
    private var colorTextTopSelected = 0
    private var colorTextMiddle = 0
    private var colorTextMiddleSelected = 0
    private var colorTextBottom = 0
    private var colorTextBottomSelected = 0

    private var selectedItemBackground: Drawable? = null

    // 여기 있는 모든 함수는 MainActivity 에서 configure() 이후 사용 가능한 함수

    /**
     * Set the text size of the labels in scale-independent pixels
     *
     * @param sizeTopText    the Top text size, in SP
     * @param sizeMiddleText the Middle text size, in SP
     * @param sizeBottomText the Bottom text size, in SP
     */
    fun textSize(sizeTopText: Float, sizeMiddleText: Float, sizeBottomText: Float): ConfigBuilder {
        this.sizeTopText = sizeTopText
        this.sizeMiddleText = sizeMiddleText
        this.sizeBottomText = sizeBottomText
        return this
    }

    /**
     * Set the text size of the top label in scale-independent pixels
     *
     * @param size the Top text size, in SP
     */
    fun sizeTopText(size: Float): ConfigBuilder {
        sizeTopText = size
        return this
    }

    /**
     * Set the text size of the middle label in scale-independent pixels
     *
     * @param size the Middle text size, in SP
     */
    fun sizeMiddleText(size: Float): ConfigBuilder {
        sizeMiddleText = size
        return this
    }

    /**
     * Set the text size of the bottom label in scale-independent pixels
     *
     * @param size the Bottom text size, in SP
     */
    fun sizeBottomText(size: Float): ConfigBuilder {
        sizeBottomText = size
        return this
    }

    fun selectorColor(selectorColor: Int?): ConfigBuilder {
        this.selectorColor = selectorColor
        return this
    }

    fun formatTopText(format: String?): ConfigBuilder {
        formatTopText = format
        return this
    }

    fun formatMiddleText(format: String?): ConfigBuilder {
        formatMiddleText = format
        return this
    }

    fun formatBottomText(format: String?): ConfigBuilder {
        formatBottomText = format
        return this
    }

    fun showTopText(value: Boolean): ConfigBuilder {
        showTopText = value
        return this
    }

    fun showBottomText(value: Boolean): ConfigBuilder {
        showBottomText = value
        return this
    }

    fun textColor(textColorNormal: Int, textColorSelected: Int): ConfigBuilder {
        colorTextTop = textColorNormal
        colorTextMiddle = textColorNormal
        colorTextBottom = textColorNormal
        colorTextTopSelected = textColorSelected
        colorTextMiddleSelected = textColorSelected
        colorTextBottomSelected = textColorSelected
        return this
    }

    fun colorTextTop(textColorNormal: Int, textColorSelected: Int): ConfigBuilder {
        colorTextTop = textColorNormal
        colorTextTopSelected = textColorSelected
        return this
    }

    fun colorTextMiddle(textColorNormal: Int, textColorSelected: Int): ConfigBuilder {
        colorTextMiddle = textColorNormal
        colorTextMiddleSelected = textColorSelected
        return this
    }

    fun colorTextBottom(textColorNormal: Int, textColorSelected: Int): ConfigBuilder {
        colorTextBottom = textColorNormal
        colorTextBottomSelected = textColorSelected
        return this
    }

    fun selectedDateBackground(background: Drawable?): ConfigBuilder {
        selectedItemBackground = background
        return this
    }

    // HorizontalCalendar.Builder 와 MainActivity 에서 꼭 end() 를 호출하던데...
    // 단순히 TopText MiddleText BottomText 글자 사이즈만 건드리는 듯 함
    fun end(): HorizontalCalendar.Builder {
        if (formatMiddleText == null) {
            formatMiddleText = HorizontalCalendarConfig.DEFAULT_FORMAT_TEXT_MIDDLE
        }

        if (formatTopText == null && showTopText) {
            formatTopText = HorizontalCalendarConfig.DEFAULT_FORMAT_TEXT_TOP
        }

        if (formatBottomText == null && showBottomText) {
            formatBottomText = HorizontalCalendarConfig.DEFAULT_FORMAT_TEXT_BOTTOM
        }
        return calendarBuilder
    }

    // 밑에 있는 두 개의 함수 createDefaultStyle(), createSelectedItemStyle() 이후 실행되는 함수
    // HorizontalCalendar.Builder 에서 build() 될 때 사용되고 있음
    fun createConfig(): HorizontalCalendarConfig {
        val config = HorizontalCalendarConfig(sizeTopText, sizeMiddleText, sizeBottomText, selectorColor)
        config.formatTopText = formatTopText
        config.formatMiddleText = formatMiddleText
        config.formatBottomText = formatBottomText
        config.isShowTopText = showTopText
        config.isShowBottomText = showBottomText
        return config
    }

    // 기본 스타일. HorizontalCalendar.Builder 에서 build() 될 때 사용되고 있음
    fun createDefaultStyle(): CalendarItemStyle {
        return CalendarItemStyle(colorTextTop, colorTextMiddle, colorTextBottom, null)
    }

    // 선택 스타일. HorizontalCalendar.Builder 에서 build() 될 때 사용되고 있음
    fun createSelectedItemStyle(): CalendarItemStyle {
        return CalendarItemStyle(colorTextTopSelected, colorTextMiddleSelected, colorTextBottomSelected, selectedItemBackground)
    }

}