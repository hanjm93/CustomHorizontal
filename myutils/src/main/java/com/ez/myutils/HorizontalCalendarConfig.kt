package com.ez.myutils

class HorizontalCalendarConfig {
    /* Format & Font Sizes*/
    var formatTopText: String? = null
    var formatMiddleText: String? = null
    var formatBottomText: String? = null
    var sizeTopText = 0f
    var sizeMiddleText = 0f
    var sizeBottomText = 0f
    var selectorColor: Int? = null
    var isShowTopText = false
    var isShowBottomText = false

    constructor() {}
    constructor(sizeTopText: Float, sizeMiddleText: Float, sizeBottomText: Float, selectorColor: Int?) {
        this.sizeTopText = sizeTopText
        this.sizeMiddleText = sizeMiddleText
        this.sizeBottomText = sizeBottomText
        this.selectorColor = selectorColor
    }

    fun setFormatTopText(formatTopText: String?): HorizontalCalendarConfig {
        this.formatTopText = formatTopText
        return this
    }

    fun setFormatMiddleText(formatMiddleText: String?): HorizontalCalendarConfig {
        this.formatMiddleText = formatMiddleText
        return this
    }

    fun setFormatBottomText(formatBottomText: String?): HorizontalCalendarConfig {
        this.formatBottomText = formatBottomText
        return this
    }

    fun setSizeTopText(sizeTopText: Float): HorizontalCalendarConfig {
        this.sizeTopText = sizeTopText
        return this
    }

    fun setSizeMiddleText(sizeMiddleText: Float): HorizontalCalendarConfig {
        this.sizeMiddleText = sizeMiddleText
        return this
    }

    fun setSizeBottomText(sizeBottomText: Float): HorizontalCalendarConfig {
        this.sizeBottomText = sizeBottomText
        return this
    }

    fun setSelectorColor(selectorColor: Int?): HorizontalCalendarConfig {
        this.selectorColor = selectorColor
        return this
    }

    fun setShowTopText(showTopText: Boolean): HorizontalCalendarConfig {
        isShowTopText = showTopText
        return this
    }

    fun setShowBottomText(showBottomText: Boolean): HorizontalCalendarConfig {
        isShowBottomText = showBottomText
        return this
    }

    fun setupDefaultValues(defaultConfig: HorizontalCalendarConfig?) {
        if (defaultConfig == null) {
            return
        }
        if (selectorColor == null) {
            selectorColor = defaultConfig.selectorColor
        }
        if (sizeTopText == 0f) {
            sizeTopText = defaultConfig.sizeTopText
        }
        if (sizeMiddleText == 0f) {
            sizeMiddleText = defaultConfig.sizeMiddleText
        }
        if (sizeBottomText == 0f) {
            sizeBottomText = defaultConfig.sizeBottomText
        }
    }

    companion object {
        const val DEFAULT_SIZE_TEXT_TOP = 14f
        const val DEFAULT_SIZE_TEXT_MIDDLE = 24f
        const val DEFAULT_SIZE_TEXT_BOTTOM = 14f
        const val DEFAULT_FORMAT_TEXT_TOP = "MMM"
        const val DEFAULT_FORMAT_TEXT_MIDDLE = "dd"
        const val DEFAULT_FORMAT_TEXT_BOTTOM = "EEE"
    }
}