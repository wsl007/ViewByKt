package com.wsl.viewbykt.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.min
import android.view.animation.RotateAnimation


/**
 * 刀锋loading
 * learn from
 * https://mp.weixin.qq.com/s/39Td7zr31kViWqzD5-lxnA
 * https://github.com/samwangds/DemoFactory/blob/master/app/src/main/java/demo/com/sam/demofactory/view/SwordLoadingView.kt
 */
class SwordLoadingView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    private val paint = Paint()
    private val xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    private val rotateMatrix = Matrix()
    private val camera = Camera()
    private var anim: ValueAnimator? = null

    init {
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // canvas设置抗锯齿
//        canvas.drawFilter = PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawColor(Color.BLACK)

        paint.color = Color.BLUE
        drawSword(canvas, 35f, -45f, 0f)
        paint.color = Color.RED
        drawSword(canvas, 50f, 10f, 120f)
        paint.color = Color.GREEN
        drawSword(canvas, 35f, 55f, 240f)
    }

    private fun drawSword(canvas: Canvas, rotateX: Float, rotateY: Float, startZ: Float) {
        // 绘制月弧
        val layoutId = canvas.saveLayer(
            0f, 0f, width.toFloat(),
            height.toFloat(), null, Canvas.ALL_SAVE_FLAG
        )
        // 矩阵变换
        rotateMatrix.reset()
        camera.save()
        camera.rotateX(rotateX)
        camera.rotateY(rotateY)
        anim?.apply {
            camera.rotateZ(startZ + animatedValue as Float)
        }
        camera.getMatrix(rotateMatrix)
        camera.restore()

        val halfW = width / 2f
        val halfH = height / 2f
        // preTranslate方法的作用是在旋转之间先把内容移动,使旋转中心为中间
        rotateMatrix.preTranslate(-halfW, -halfH)
        // postTranslate方法是在变换之后再将内容移动,回到原来位置
        rotateMatrix.postTranslate(halfW, halfH)
        canvas.concat(rotateMatrix)

        val radius = min(width, height) / 3f

        canvas.drawCircle(halfW, halfH, radius, paint)
        paint.color = Color.BLACK
        paint.xfermode = xfermode
        canvas.drawCircle(halfW, halfH - 0.05f * radius, radius * 1.01f, paint)
        canvas.restoreToCount(layoutId)
        paint.xfermode = null
    }

    private fun anim() {
        anim = ValueAnimator.ofFloat(0f, -360f)
            .apply {
                // 处理起始时的停顿感
                interpolator = null
                repeatCount = RotateAnimation.INFINITE
                duration = 1000

                addUpdateListener {
                    invalidate()
                }
            }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
//        anim()
//        anim?.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        anim?.cancel()
        anim = null
    }
}