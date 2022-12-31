package com.wsl.viewbykt.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import com.wsl.viewbykt.R

class ColorTrackTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : androidx.appcompat.widget.AppCompatTextView(context, attrs) {

    private var mOriginPaint: Paint
    private var mChangePaint: Paint

    private var startX = 0f
    private var mCurrentProgress = 0.5f

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.ColorTrackTextView)
        val originColor = array.getColor(R.styleable.ColorTrackTextView_originColor, 0)
        val changeColor = array.getColor(R.styleable.ColorTrackTextView_changeColor, 0)

        mOriginPaint = getPaintByColor(originColor)
        mChangePaint = getPaintByColor(changeColor)

        array.recycle()
    }

    private fun getPaintByColor(color: Int): Paint {
        val paint = Paint()
        paint.color = color
        // 抗锯齿
        paint.isAntiAlias = true
        // 防抖动
        paint.isDither = true
        paint.textSize = textSize
        return paint
    }

    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
        val text = text.toString()
        val bounds = Rect()
        mChangePaint.getTextBounds(text, 0, text.length, bounds)
        startX = width / 2 - bounds.width() / 2f

        canvas.save()
        val middle = (mCurrentProgress * width).toInt()
        var rect = Rect(0, 0, middle, height)
        canvas.clipRect(rect)

        val fontMetrics = mOriginPaint.fontMetrics
        val dy = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
        val baseLine = height / 2 + dy
        canvas.drawText(text, startX, baseLine, mOriginPaint)
        canvas.restore()

        canvas.save()
        rect = Rect(middle, 0, width, height)
        canvas.clipRect(rect)
        canvas.drawText(text, startX, baseLine, mChangePaint)
        canvas.restore()
    }
}