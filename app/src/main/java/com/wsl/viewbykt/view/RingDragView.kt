package com.wsl.viewbykt.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

/**
 * 3/4环形可拖动仪表盘
 */
class RingDragView : View {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    companion object {
        const val startDegree = 16
        const val circleWidth = 60f
        const val defaultValue = 100
        const val padding = 100f
    }

    private var mCenterX = 0f
    private var mCenterY = 0f
    private var mRadius = 0f
    private var insideRadius = 0f
    private lateinit var mArcRectF: RectF
    private var mArcPaint: Paint = Paint()
    private var mLinePaint: Paint
    private var mTextPaint: Paint
    private var scanDegrees = 0
    private lateinit var mSweepGradient: SweepGradient
    private var finalDegree = 0
    private var detector: GestureDetector
    private var canMove = true

    init {
        mArcPaint.isAntiAlias = true
        mArcPaint.strokeWidth = circleWidth
        mArcPaint.color = Color.WHITE
        mArcPaint.style = Paint.Style.STROKE

        mLinePaint = Paint()
        mLinePaint.isAntiAlias = true
        mLinePaint.color = Color.GREEN
        mLinePaint.strokeWidth = 3f

        mTextPaint = Paint()
        mTextPaint.isAntiAlias = true
        mTextPaint.color = Color.RED
        mTextPaint.textSize = 36f
        mTextPaint.textAlign = Paint.Align.CENTER

        detector = GestureDetector(context, object : GestureDetector.OnGestureListener {

            override fun onDown(event: MotionEvent?): Boolean {
                if (event == null)
                    return true
                var x = event.x
                var y = event.y

                if (x > mCenterX)
                    x -= mCenterX
                else
                    x = mCenterX - x
                if (y < mCenterY)
                    y = mCenterY - y
                else
                    y -= mCenterY

                val r = Math.sqrt((x * x + y * y).toDouble())
                //判断是否在空白区域
                canMove = !(r < (mRadius - circleWidth) || r > mRadius)
                parent.requestDisallowInterceptTouchEvent(canMove)
                return true
            }

            override fun onShowPress(e: MotionEvent?) {
            }

            override fun onSingleTapUp(event: MotionEvent?): Boolean {
                if (event == null || !canMove)
                    return true
                //处理父布局可滚动冲突
                parent.requestDisallowInterceptTouchEvent(true)

                var x = event.x
                var y = event.y
                val firstX = x
                val firstY = y

                if (x > mCenterX)
                    x -= mCenterX
                else
                    x = mCenterX - x
                if (y < mCenterY)
                    y = mCenterY - y
                else
                    y -= mCenterY

                val r = Math.sqrt((x * x + y * y).toDouble())

                var acos = Math.acos(x / r)
                //根据cos求角度
                acos = Math.toDegrees(acos)
                if (firstX <= mCenterX && firstY >= mCenterY) {
                    //在第3象限
                    acos = 180 - acos
                } else if (firstX <= mCenterX && firstY <= mCenterY) {
                    //2象限
                    acos += 180
                } else if (firstX >= mCenterX && firstY <= mCenterY) {
                    //1象限
                    acos = 360 - acos
                }
                scanDegrees = acos.toInt()
                //计算度数，每22.5度加1
                if (scanDegrees in 135..360) {
                    scanDegrees -= 135
                    finalDegree = (scanDegrees / 22.5f).toInt()

                    invalidate()
                } else if (scanDegrees <= 65) {
                    scanDegrees = (360 - 135 + acos).toInt()
                    if (scanDegrees > 270)
                        scanDegrees = 270
                    val degrees = scanDegrees / 22.5.toInt()
                    finalDegree = degrees
                    invalidate()

//                    if (scanDegrees == 270)
//                        return false
                } else {
                    scanDegrees = 270
                }
                Log.d("tag", "onSingleTapUp旋转角度=$scanDegrees")
                return true
            }

            override fun onFling(
                e1: MotionEvent?,
                event: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                Log.d("tag", "onFling旋转角度=$scanDegrees")
                return true
            }

            override fun onScroll(
                e1: MotionEvent?,
                event: MotionEvent?,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                if (event == null || !canMove)
                    return true
                parent.requestDisallowInterceptTouchEvent(true)

                var x = event.x
                var y = event.y
                val firstX = x
                val firstY = y

                if (x > mCenterX)
                    x -= mCenterX
                else
                    x = mCenterX - x
                if (y < mCenterY)
                    y = mCenterY - y
                else
                    y -= mCenterY

                val r = Math.sqrt((x * x + y * y).toDouble())

                var acos = Math.acos(x / r)
                //根据cos求角度
                acos = Math.toDegrees(acos)
                if (firstX <= mCenterX && firstY >= mCenterY) {
                    //在第3象限
                    acos = 180 - acos
                } else if (firstX <= mCenterX && firstY <= mCenterY) {
                    //2象限
                    acos += 180
                } else if (firstX >= mCenterX && firstY <= mCenterY) {
                    //1象限
                    acos = 360 - acos
                }
                scanDegrees = acos.toInt()
                //计算度数，每22.5度加1
                if (scanDegrees in 135..360) {
                    scanDegrees -= 135
                    finalDegree = (scanDegrees / 22.5f).toInt()

                    invalidate()
                } else if (scanDegrees <= 65) {
                    scanDegrees = (360 - 135 + acos).toInt()
                    if (scanDegrees > 270)
                        scanDegrees = 270
                    val degrees = scanDegrees / 22.5.toInt()
                    finalDegree = degrees

                    invalidate()
                } else {
                    scanDegrees = 270
                }
                Log.d("tag", "onScroll旋转角度=$scanDegrees")
                return true
            }

            override fun onLongPress(e: MotionEvent?) {
            }
        })
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
        mCenterX = w / 2f
        mCenterY = h / 2f
        mRadius = mCenterY - padding
        insideRadius = mRadius - circleWidth / 2

        mArcRectF = RectF(
            mCenterX - mRadius, padding,
            mCenterX + mRadius, h - padding
        )

        val colors = intArrayOf(
            0xFFE5BD7D.toInt(), 0xFFFAAA64.toInt(),
            0xFFFFFFFF.toInt(), 0xFF6AE2FD.toInt(),
            0xFF8CD0E5.toInt(), 0xFFA3CBCB.toInt(),
            0xFFBDC7B3.toInt(), 0xFFD1C299.toInt(), 0xFFE5BD7D.toInt()
        )
        mSweepGradient = SweepGradient(mCenterX, mCenterX, colors, null)
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null)
            return
        canvas.drawColor(Color.LTGRAY)

        mArcPaint.shader = null
        canvas.drawArc(mArcRectF, 135f, 270f, false, mArcPaint)

        mArcPaint.shader = mSweepGradient
        //画渐变色的圆
        canvas.drawArc(mArcRectF, 135f, scanDegrees.toFloat(), false, mArcPaint)

        for (i in 0 until 72) {
            val startY = mArcRectF.bottom - circleWidth / 2
            var top = startY + circleWidth
            if (i in 9..63) {
                if (i % 9 == 0)
                    top += padding / 4
                canvas.drawLine(mCenterX, startY, mCenterX, top, mLinePaint)
            }
            canvas.rotate(5f, mCenterX, mCenterY)
        }

        val c = mRadius + circleWidth / 2 + 45
        val x = Math.sqrt(c * c / 2.toDouble())

        val cY = mCenterY
        canvas.drawText(
            startDegree.toString(),
            (mCenterX - x).toFloat(),
            (cY + x).toFloat(),
            mTextPaint
        )
        canvas.drawText((startDegree + 2).toString(), mCenterX - c, cY + 10f, mTextPaint)
        canvas.drawText(
            (startDegree + 4).toString(),
            (mCenterX - x).toFloat(),
            (cY - x + 10).toFloat(),
            mTextPaint
        )
        canvas.drawText((startDegree + 6).toString(), mCenterX, cY - c + 10, mTextPaint)
        canvas.drawText(
            (startDegree + 8).toString(),
            (mCenterX + x).toFloat(),
            (cY - x + 10).toFloat(),
            mTextPaint
        )
        canvas.drawText((startDegree + 10).toString(), mCenterX + c, cY + 10f, mTextPaint)
        canvas.drawText(
            (startDegree + 12).toString(),
            (mCenterX + x).toFloat(),
            (cY + x).toFloat(),
            mTextPaint
        )
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null)
            return true
        return detector.onTouchEvent(event)
    }
}