package com.wsl.viewbykt.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
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
private val RADIUS = 150f.px

private val ANGLES = floatArrayOf(60f, 90f, 150f, 60f)
private val COLORS = listOf(
    Color.parseColor("#c2185b"),
    Color.parseColor("#00acc1"),
    Color.parseColor("#558b2f"),
    Color.parseColor("#5d4037")
)

private val OFFSET_LENGTH = 20f.px

class PieView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    private var offsetPosition = 1

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        path.reset()
        path.addArc(
            width / 2f - RADIUS, height / 2 - RADIUS,
            width / 2f + RADIUS, height / 2f + RADIUS,
            90 + OPEN_ANGLE / 2f, 360f - OPEN_ANGLE
        )
    }

    override fun onDraw(canvas: Canvas) {
        var startAngle = 0f
        for ((index, angle) in ANGLES.withIndex()) {
            paint.color = COLORS[index]

            if (index == offsetPosition) {
                canvas.save()
                val temp = Math.toRadians(startAngle + angle / 2f.toDouble()).toFloat()
                val xOffset = cos(temp)
                val yOffset = sin(temp)
                canvas.translate(OFFSET_LENGTH * xOffset, OFFSET_LENGTH * yOffset)
            }

            canvas.drawArc(
                width / 2f - RADIUS, height / 2 - RADIUS,
                width / 2f + RADIUS, height / 2f + RADIUS,
                startAngle, angle,
                // 是否连到中心点
                true,
                paint
            )

            if (index == offsetPosition) {
                canvas.restore()
            }

            startAngle += angle
        }
    }
}