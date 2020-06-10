package com.ez.myutils

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

class HorizontalLayoutManager internal constructor(context: Context?, reverseLayout: Boolean) : LinearLayoutManager(context, HORIZONTAL, reverseLayout) {
    var smoothScrollSpeed = SPEED_NORMAL

    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State, position: Int) {
        val smoothScroller: LinearSmoothScroller = object : LinearSmoothScroller(recyclerView.context) {
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                Log.d("Manager", "speed test smoothScrollSpeed : ${smoothScrollSpeed}, densityDpi : ${displayMetrics.densityDpi}, return ${smoothScrollSpeed / displayMetrics.densityDpi}")
                return smoothScrollSpeed / displayMetrics.densityDpi
            }
        }
        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }

    companion object {
        const val SPEED_NORMAL = 90f
        const val SPEED_SLOW = 125f
    }
}