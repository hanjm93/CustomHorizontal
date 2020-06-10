package com.ez.myutils

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView


class DateViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
    @JvmField
    var constraint: ConstraintLayout = rootView.findViewById(R.id.hc_constraint)

    @JvmField
    var textTop: TextView = rootView.findViewById(R.id.hc_text_top)

    @JvmField
    var textMiddle: TextView = rootView.findViewById(R.id.hc_text_middle)

    @JvmField
    var textBottom: TextView = rootView.findViewById(R.id.hc_text_bottom)

    @JvmField
    var selectionView: View = rootView.findViewById(R.id.hc_selector)

    @JvmField
    var layoutContent: View = rootView.findViewById(R.id.hc_layoutContent)

    @JvmField
    var eventsRecyclerView: RecyclerView = rootView.findViewById(R.id.hc_events_recyclerView)

}