package com.wsl.viewbykt.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

/**
 * 贝塞尔曲线绘制变化的圆
 */
class CubicCircleView : View {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    companion object {
        //绘制圆数值约等于
        const val blackMagic = 0.551915024494f
    }

    private var mPath: Path
    private var mFillCirclePaint: Paint
    private var centerX = 0
    private var centerY = 0f
    private var maxLength = 0f
    private var mInterpolateTime = 0f
    private var stretchDistance = 0f
    private var moveDistance = 0f
    private var cDistance = 0f
    private var radius = 0f
    private var c = 0f
    private var p2: VPoint
    private var p4: VPoint
    private var p1: HPoint
    private var p3: HPoint

    init {
        mFillCirclePaint = Paint()
        mFillCirclePaint.color = 0xFFFE626D.toInt()
        mFillCirclePaint.style = Paint.Style.FILL
        mFillCirclePaint.strokeWidth = 1f
        mFillCirclePaint.isAntiAlias = true

        mPath = Path()
        p2 = VPoint()
        p4 = VPoint()

        p1 = HPoint()
        p3 = HPoint()

        setOnClickListener({
            startAnim()
        })
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Log.i("tag", "width=$width,height=$height")
        centerX = width / 2
        centerY = height / 2f
        radius = 50f
        c = radius * blackMagic
        stretchDistance = radius
        moveDistance = radius * 3 / 5f
        cDistance = c * .45f
        maxLength = width - radius - radius
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null)
            return
        canvas.drawColor(0xFFEEEEEE.toInt())
        mPath.reset()
        canvas.translate(2 * radius, centerY)
        if (mInterpolateTime in 0f..0.2f) {
            model1(mInterpolateTime)
        } else if (mInterpolateTime > 0.2f && mInterpolateTime <= .5f) {
            model2(mInterpolateTime)
        } else if (mInterpolateTime > .5f && mInterpolateTime <= .8f) {
            model3(mInterpolateTime)
        } else if (mInterpolateTime > .8f && mInterpolateTime <= .9f) {
            model4(mInterpolateTime)
        } else if (mInterpolateTime > .9f && mInterpolateTime <= 1) {
            model5(mInterpolateTime)
        }

        var offset = maxLength * (mInterpolateTime - .2f)
        offset = if (offset > 0) offset else 0f
        p1.adjustAllX(offset)
        p2.adjustAllX(offset)
        p3.adjustAllX(offset)
        p4.adjustAllX(offset)

        mPath.moveTo(p1.x, p1.y)
        mPath.cubicTo(p1.right.x, p1.right.y, p2.bottom.x, p2.bottom.y, p2.x, p2.y)
        mPath.cubicTo(p2.top.x, p2.top.y, p3.right.x, p3.right.y, p3.x, p3.y)
        mPath.cubicTo(p3.left.x, p3.left.y, p4.top.x, p4.top.y, p4.x, p4.y)
        mPath.cubicTo(p4.bottom.x, p4.bottom.y, p1.left.x, p1.left.y, p1.x, p1.y)

        canvas.drawPath(mPath, mFillCirclePaint)
    }


    private fun model0() {
        p1.setValueY(radius)
        p3.setValueY(-radius)
        p1.x = 0f
        p3.x = 0f
        p1.left.x = -c
        p3.left.x = -c
        p1.right.x = c
        p3.right.x = c

        p2.setValueX(radius)
        p4.setValueX(-radius)
        p2.y = 0f
        p4.y = 0f
        p2.top.y = -c
        p4.top.y = -c
        p2.bottom.y = c
        p4.bottom.y = c
    }


    private fun model1(time: Float) {
        model0()
        p2.setValueX(radius + stretchDistance * time * 5)
    }

    private fun model2(time: Float) {
        model1(.2f)
        val timeA = (time - .2f) * (10f / 3)
        p1.adjustAllX(stretchDistance / 2 * timeA)
        p3.adjustAllX(stretchDistance / 2 * timeA)
        p2.adjustY(cDistance * timeA)
        p4.adjustY(cDistance * timeA)
    }

    private fun model3(time: Float) {
        model2(.5f)
        val timeA = (time - .5f) * (10f / 3)
        p1.adjustAllX(stretchDistance / 2 * timeA)
        p3.adjustAllX(stretchDistance / 2 * timeA)
        p2.adjustY(-cDistance * timeA)
        p4.adjustY(-cDistance * timeA)

        p4.adjustAllX(stretchDistance / 2 * timeA)
    }


    private fun model4(time: Float) {
        model3(.8f)
        p4.adjustAllX(stretchDistance / 2 * (time - .8f) * 10)
    }

    private fun model5(time: Float) {
        model4(.9f)
        p4.adjustAllX(
            (Math.sin(Math.PI * (time - .9f) * 10f)
                    * (2 / 10f * radius))
                .toFloat()
        )
    }

    class VPoint {
        var x = 0f
        var y = 0f
        var top = PointF()
        var bottom = PointF()

        fun setValueX(x: Float) {
            this.x = x
            top.x = x
            bottom.x = x
        }

        fun adjustY(offset: Float) {
            top.y -= offset
            bottom.y += offset
        }

        fun adjustAllX(offset: Float) {
            this.x += offset
            top.x += offset
            bottom.x += offset
        }
    }

    class HPoint {
        var x = 0f
        var y = 0f
        var left = PointF()
        var right = PointF()

        fun setValueY(y: Float) {
            this.y = y
            left.y = y
            right.y = y
        }

        fun adjustAllX(offset: Float) {
            this.x += offset
            left.x += offset
            right.x += offset
        }
    }

    inner class MoveAnimation : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            super.applyTransformation(interpolatedTime, t)
            mInterpolateTime = interpolatedTime
            Log.i("tag", "mInterpolateTime=$mInterpolateTime")
            invalidate()
        }
    }

    public fun startAnim() {
        mInterpolateTime = 0f
        val move = MoveAnimation()
        move.duration = 5000
//        move.interpolator = AccelerateDecelerateInterpolator()
        startAnimation(move)
    }
}
