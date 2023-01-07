package com.wsl.viewbykt.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.wsl.viewbykt.utils.px

class XfermodeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    private val circleRect = RectF(
        200f.px, 50f.px, 300f.px,
        150f.px
    )

    private val rectRect = RectF(
        150f.px, 100f.px, 250f.px,
        200f.px
    )

    private val bounds = RectF(150f.px, 50f.px, 300f.px, 200f.px)

    private val circleBitmap = Bitmap.createBitmap(
        150f.px.toInt(), 150f.px.toInt(), Bitmap.Config.ARGB_8888
    )

    private val rectBitmap = Bitmap.createBitmap(
        150f.px.toInt(), 150f.px.toInt(), Bitmap.Config.ARGB_8888
    )

    init {
        val canvas = Canvas(circleBitmap)
        paint.color = Color.parseColor("#d81b60")
        canvas.drawOval(
            50f.px, 0f,
            150f.px, 100f.px, paint
        )

        canvas.setBitmap(rectBitmap)
        paint.color = Color.parseColor("#2196f3")
        canvas.drawRect(
            0f, 50f.px,
            100f.px, 150f.px, paint
        )
    }

    override fun onDraw(canvas: Canvas) {
        // 离屏缓冲，耗费资源，指定区域大小取出使用
        val count = canvas.saveLayer(bounds, null)

        canvas.drawBitmap(circleBitmap, 150f.px, 50f.px, paint)
        // 重叠的地方才计算生效，需要带背景透明部分形成的矩形，两个相同大小的bitmap叠加
        paint.xfermode = xfermode

        canvas.drawBitmap(rectBitmap, 150f.px, 50f.px, paint)

        // 恢复正常
        paint.xfermode = null

        // 还回缓冲区域，参数：要返回的位置
        canvas.restoreToCount(count)
    }

//    override fun onDraw(canvas: Canvas) {
//        // 离屏缓冲，耗费资源，指定区域大小取出使用
//        val count = canvas.saveLayer(bounds, null)
//
//        paint.color = Color.parseColor("#d81b60")
//        canvas.drawOval(circleRect, paint)
//        // 重叠计算，需要带背景透明部分形成的矩形，两个相同大小的bitmap叠加
//        paint.xfermode = xfermode
//
//        paint.color = Color.parseColor("#2196f3")
//        canvas.drawRect(rectRect, paint)
//        // 恢复正常
//        paint.xfermode = null
//
//        // 还回缓冲区域，参数：要返回的位置
//        canvas.restoreToCount(count)
//    }
}