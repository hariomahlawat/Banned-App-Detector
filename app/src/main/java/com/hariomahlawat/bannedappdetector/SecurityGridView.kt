package com.hariomahlawat.bannedappdetector

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

class SecurityGridView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = 0xFFFFFFFF.toInt()
        strokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            2f,
            resources.displayMetrics
        )
        alpha = (255 * 0.02f).toInt()
    }

    private val step = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        32f,
        resources.displayMetrics
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        var start = -h
        while (start < w) {
            canvas.drawLine(start, h, start + h, 0f, paint)
            start += step
        }
        start = -h
        while (start < w) {
            canvas.drawLine(start, 0f, start + h, h, paint)
            start += step
        }
    }
}
