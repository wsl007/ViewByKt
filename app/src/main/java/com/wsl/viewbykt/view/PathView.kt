package com.wsl.viewbykt.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.wsl.viewbykt.utils.px

private val RADIUS = 100f.px

class PathView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    private lateinit var pathMeasure: PathMeasure

    init {
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        path.reset()
        path.addCircle(width / 2f, height / 2f, RADIUS, Path.Direction.CCW)
        // 旋转方向不同，叠加裁剪
        path.addRect(
            width / 2f - RADIUS, height / 2f,
            width / 2f + RADIUS, height / 2f + 2 * RADIUS, Path.Direction.CCW
        )
        // 同方向时，裁剪
        path.fillType = Path.FillType.EVEN_ODD
//        path.fillType = Path.FillType.INVERSE_EVEN_ODD

        // 参2 是否自动闭合
        pathMeasure = PathMeasure(path, false)
        pathMeasure.length
        // 获取指定位置的切角
//        pathMeasure.getPosTan()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(path, paint)
    }
}