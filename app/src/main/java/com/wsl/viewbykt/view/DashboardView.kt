package com.wsl.viewbykt.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathDashPathEffect
import android.graphics.PathMeasure
import android.util.AttributeSet
import android.view.View
import com.wsl.viewbykt.utils.px
import kotlin.math.cos
import kotlin.math.sin

private val OPEN_ANGLE = 120
private val DASH_WIDTH = 2f.px
private val DASH_LENGTH = 10f.px

// 间隔的数量，比刻度少1
private val DASH_COUNT = 20

// 起始为0
private val DASH_COUNT_POSITION = 11

private val RADIUS = 150f.px
private val DASH_POSITION_LENGTH = 0.9f * RADIUS

class DashboardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val dash = Path()
    private val path = Path()
    private lateinit var pathMeasure: PathMeasure
    private lateinit var pathDashPathEffect: PathDashPathEffect

    init {
        paint.strokeWidth = 3f.px
        paint.style = Paint.Style.STROKE

        dash.addRect(0f, 0f, DASH_WIDTH, DASH_LENGTH, Path.Direction.CCW)
//        // path 的效果 虚线效果实现表盘刻度
//        // 参2、3 的实际安卓搞反了
//        pathDashPathEffect = PathDashPathEffect(
//            dash,
//            // 是否提前量，空格距离
//            50f,
//            // 间隔距离
//            0f,
//            // 拐角风格，区别不大
//            PathDashPathEffect.Style.ROTATE
//        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        path.reset()
        path.addArc(
            width / 2f - RADIUS, height / 2 - RADIUS,
            width / 2f + RADIUS, height / 2f + RADIUS,
            90 + OPEN_ANGLE / 2f, 360f - OPEN_ANGLE
        )
        pathMeasure = PathMeasure(path, false)

        // path 的效果 虚线效果实现表盘刻度
        pathDashPathEffect = PathDashPathEffect(
            dash,
            // 减去一个空格宽度，再除
            (pathMeasure.length - DASH_WIDTH) / DASH_COUNT,
            0f,
            PathDashPathEffect.Style.ROTATE
        )
    }

    override fun onDraw(canvas: Canvas) {
        // 画弧
        paint.pathEffect = null
        canvas.drawPath(
            path, paint
        )

        // 画刻度
        paint.pathEffect = pathDashPathEffect
//        canvas.drawArc(
//            width / 2f - RADIUS, height / 2 - RADIUS,
//            width / 2f + RADIUS, height / 2f + RADIUS,
//            90 + OPEN_ANGLE / 2f, 360f - OPEN_ANGLE,
//            false, paint
//        )
        canvas.drawPath(
            path, paint
        )

        val markRadians = markToRaians()
        canvas.drawLine(
            width / 2f, height / 2f,
            width / 2f + DASH_POSITION_LENGTH * cos(markRadians).toFloat(),
            height / 2f + DASH_POSITION_LENGTH * sin(markRadians).toFloat(),
            paint
        )
    }

    private fun markToRaians() = Math.toRadians(
        (90 + OPEN_ANGLE / 2f + (360 - OPEN_ANGLE) / DASH_COUNT * DASH_COUNT_POSITION).toDouble()
    )
}