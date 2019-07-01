package com.wsl.viewbykt.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * 半环形可拖动仪表盘
 */
class RingDragView : View {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    companion object {
        const val startDegree = 16
        const val circleWidth = 60f
        const val defaultValue = 100
    }

    private var mCenter = 0f
    private var mRadius = 0
    private var insideRadius = 0f
    private lateinit var mArcRectF: RectF
    private lateinit var mArcPaint: Paint
    private lateinit var mLinePaint: Paint
    private lateinit var mTextPaint: Paint
    private var scanDegrees = 0
    private lateinit var mSweepGradient: SweepGradient
    private var canMove = true
    private var defaultWidth = 0
    private var finalDegree = 0

    init {
        mArcPaint = Paint()
        mArcPaint.isAntiAlias = true
        mArcPaint.strokeWidth = circleWidth
        mArcPaint.color = Color.WHITE
        mArcPaint.style = Paint.Style.STROKE

        mLinePaint = Paint()
        mLinePaint.isAntiAlias = true
        mLinePaint.color = 0xffaaaaaa.toInt()
        mLinePaint.strokeWidth = 2f

        mTextPaint = Paint()
        mTextPaint.isAntiAlias = true
        mTextPaint.color = 0xff666666.toInt()
        mTextPaint.textSize = 30f
        mTextPaint.textAlign = Paint.Align.CENTER
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureWidth(heightMeasureSpec))
    }

    private fun measureWidth(widthMeasureSpec: Int): Int {
        val mode = MeasureSpec.getMode(widthMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        return when (mode) {
            MeasureSpec.AT_MOST ->
                Math.min(defaultValue, width)
            else ->
                width
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCenter = 720 / 2f
        mRadius = 720 / 2 - 100
        insideRadius = mRadius - circleWidth / 2

        mArcRectF = RectF(
            mCenter - mRadius,
            mCenter - mRadius,
            mCenter + mRadius,
            mCenter + mRadius
        )

        val colors = intArrayOf(
            0xFFE5BD7D.toInt(), 0xFFFAAA64.toInt(),
            0xFFFFFFFF.toInt(), 0xFF6AE2FD.toInt(),
            0xFF8CD0E5.toInt(), 0xFFA3CBCB.toInt(),
            0xFFBDC7B3.toInt(), 0xFFD1C299.toInt(), 0xFFE5BD7D.toInt()
        )
        mSweepGradient = SweepGradient(
            mCenter, mCenter,
            colors, null
        )
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null)
            return
        mArcPaint.shader = null
        canvas.drawArc(mArcRectF, 135f, 270f, false, mArcPaint)

        mArcPaint.shader = mSweepGradient
        var top = 0f
        for (i in 0 until 120) {
            top = mCenter - mRadius - circleWidth / 2
            if (i <= 45 || i >= 75) {
                if (i % 15 == 0)
                    top -= 25
                canvas.drawLine(
                    mCenter,
                    mCenter - mRadius + 30,
                    mCenter, top, mLinePaint
                )
            }
            canvas.rotate(3f, mCenter, mCenter)
        }

        val c = mRadius + circleWidth / 2 + 45
        val x = Math.sqrt(c * c / 2.toDouble())

        canvas.drawText(startDegree.toString() + "", (mCenter - x).toFloat(), (mCenter + x).toFloat(), mTextPaint)
        canvas.drawText((startDegree + 2).toString() + "", mCenter - c, mCenter + 10, mTextPaint)
        canvas.drawText(
            (startDegree + 4).toString() + "",
            (mCenter - x).toFloat(),
            (mCenter - x + 10).toFloat(),
            mTextPaint
        )
        canvas.drawText((startDegree + 6).toString() + "", mCenter, mCenter - c + 10, mTextPaint)
        canvas.drawText(
            (startDegree + 8).toString() + "",
            (mCenter + x).toFloat(),
            (mCenter - x + 10).toFloat(),
            mTextPaint
        )
        canvas.drawText((startDegree + 10).toString() + "", mCenter + c, mCenter + 10, mTextPaint)
        canvas.drawText(
            (startDegree + 12).toString() + "",
            (mCenter + x).toFloat(),
            (mCenter + x).toFloat(),
            mTextPaint
        )
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null)
            return false
        when (event.action) {
            MotionEvent.ACTION_DOWN ->
                canMove = true
            MotionEvent.ACTION_MOVE -> {
                if (!canMove)
                    return false
                var x = event.x
                var y = event.y
                val firstX = x
                val firstY = y

                if (x > mCenter)
                    x -= mCenter
                else
                    x = mCenter - x
                if (y < mCenter)
                    y = mCenter - y
                else
                    y -= mCenter

                val r = Math.sqrt((x * x + y * y).toDouble())
                if (r < (mRadius - 80) || r > (mRadius + 80)) {
                    canMove = false
                    return false
                }

                var acos = Math.acos(x / r)
                acos = Math.toDegrees(acos)
                if (mCenter in firstX..firstY) {
                    acos = 180 - acos
                } else if (firstX <= mCenter && firstY <= mCenter) {
                    acos += 180
                } else if (mCenter in firstY..firstX) {
                    acos = 360 - acos
                }
                scanDegrees = acos.toInt()
                if (scanDegrees in 135..360) {
                    scanDegrees -= 135
                    val degress = scanDegrees / 22.5
                    finalDegree = degress.toInt()

                    invalidate()
                } else if (scanDegrees <= 65) {
                    scanDegrees = (360 - 135 + acos).toInt()
                    if (scanDegrees > 270)
                        scanDegrees = 270
                    val degrees = scanDegrees / 22.5.toInt()
                    finalDegree = degrees
                    if (scanDegrees > 270)
                        return false
                    invalidate()
                } else {
                    scanDegrees = 270
                }
                return true
            }
            else ->
                return true
        }
        return true
    }
}