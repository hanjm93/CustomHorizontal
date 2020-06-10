package com.ez.myutils

import java.util.*

interface CalendarEventsPredicate {
    /**
     * @param date the date where the events will be attached to.
     * @return a list of [CalendarEvent] related to this date.
     */
    fun events(date: Calendar?): List<CalendarEvent?>?
}