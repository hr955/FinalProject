package com.neppplus.gabozago.utils

import android.graphics.Canvas
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewDecoration(private val height: Float, val padding: Float?, val color: Int) :
    RecyclerView.ItemDecoration() {
    private val paint = Paint()

    init {
        paint.color = color
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        var left = parent.paddingStart.toFloat()
        var right = parent.width - parent.paddingEnd.toFloat()

        if(padding != null){
            left = parent.paddingStart + padding
            right = parent.width - parent.paddingEnd - padding
        }

        for (i in 0 until parent.childCount - 1) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = (child.bottom + params.bottomMargin).toFloat()
            val bottom = top + height

            c.drawRect(left, top, right, bottom, paint)
        }
    }
}