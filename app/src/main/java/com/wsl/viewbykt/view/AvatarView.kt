package com.wsl.viewbykt.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.wsl.viewbykt.R
import com.wsl.viewbykt.utils.px

private val IMAGE_WIDTH = 150f.px
private val IMAGE_PADDING = 15f.px

class AvatarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val hintPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    private val bounds = RectF(
        IMAGE_PADDING, IMAGE_PADDING, IMAGE_PADDING + IMAGE_WIDTH,
        IMAGE_PADDING + IMAGE_WIDTH
    )

    init {
        hintPaint.strokeWidth = 1f.px
        hintPaint.style = Paint.Style.STROKE
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

    }

    override fun onDraw(canvas: Canvas) {
        // 离屏缓冲，耗费资源，指定区域大小取出使用
        val count = canvas.saveLayer(bounds, null)
        canvas.drawOval(
            IMAGE_PADDING, IMAGE_PADDING, IMAGE_PADDING + IMAGE_WIDTH,
            IMAGE_PADDING + IMAGE_WIDTH, paint
        )
        paint.xfermode = xfermode
        canvas.drawBitmap(
            getAvatar(IMAGE_WIDTH.toInt()),
            IMAGE_PADDING,
            IMAGE_PADDING,
            paint
        )
        // 恢复正常
        paint.xfermode = null

        // 还回缓冲区域，参数：要返回的位置
        canvas.restoreToCount(count)

        canvas.drawOval(
            IMAGE_PADDING, IMAGE_PADDING, IMAGE_PADDING + IMAGE_WIDTH,
            IMAGE_PADDING + IMAGE_WIDTH, hintPaint
        )
    }

    private fun getAvatar(width: Int): Bitmap {
        val options = BitmapFactory.Options()
        // 只读尺寸
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher, options)
        options.inJustDecodeBounds = false
        // 原来
        options.inDensity = options.outWidth
        // 实际
        options.inTargetDensity = width
        return BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher, options)
    }
}