package com.ez.myutils

class CalendarEvent {
    var color: Int
    var description: String? = null

    constructor(color: Int) {
        this.color = color
    }

    constructor(color: Int, description: String?) {
        this.color = color
        this.description = description
    }

}