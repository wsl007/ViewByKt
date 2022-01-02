package com.wsl.viewbykt.view

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import kotlin.math.atan2

/**
 * 点击 使鱼游动
 */
class FishRelativeLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RelativeLayout(context, attrs) {

    private val mPaint: Paint
    private val ivFish: ImageView
    private val fishDrawable: FishDrawable
    private var touchX = 0f
    private var touchY = 0f
    private var ripple = 0f
        set(value) {
            circleAlpha = (150 * (1 - value)).toInt()
            field = value
            invalidate()
        }
    private var circleAlpha = 0

    init {
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 8f

        setWillNotDraw(false)

        ivFish = ImageView(context)
        val params = ViewGroup.LayoutParams(
            LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        ivFish.layoutParams = params

        fishDrawable = FishDrawable()
        ivFish.setImageDrawable(fishDrawable)
//        ivFish.setBackgroundColor(Color.GREEN)

        addView(ivFish)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPaint.alpha = circleAlpha
        canvas.drawCircle(touchX, touchY, ripple * 150, mPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        touchX = event.x
        touchY = event.y

        val objectAnimator = ObjectAnimator.ofFloat(this, "ripple", 0f, 1f)
        objectAnimator.duration = 1000
        objectAnimator.start()

        makeTrail()

        return super.onTouchEvent(event)
    }

    private fun makeTrail() {
        val fishRelativeMiddle = fishDrawable.getMiddlePointF()
        val fishMiddle = PointF(
            ivFish.x + fishRelativeMiddle.x,
            ivFish.y + fishRelativeMiddle.y
        )

        val fishRelativeHead = fishDrawable.getHeadPointF()
        val fishHead = PointF(
            ivFish.x + fishRelativeHead.x,
            ivFish.y + fishRelativeHead.y
        )

        val touch = PointF(touchX, touchY)
        val angle = includeAngle(fishMiddle, fishHead, touch)
        val delta = includeAngle(
            fishMiddle, PointF(fishMiddle.x + 1, fishMiddle.y),
            fishHead
        )

        // 鱼游动曲线的控制点
        val controlPointF = fishDrawable.calculatePoint(
            fishMiddle, fishDrawable.HEAD_RADUIS * 1.6f,
            angle / 2 + delta
        )

        val path = Path()
        path.moveTo(
            fishMiddle.x - fishRelativeMiddle.x,
            fishMiddle.y - fishRelativeMiddle.y
        )
        path.cubicTo(
            fishHead.x - fishRelativeMiddle.x,
            fishHead.y - fishRelativeMiddle.y,
            controlPointF.x - fishRelativeMiddle.x,
            controlPointF.y - fishRelativeMiddle.y,
            touchX - fishRelativeMiddle.x,
            touchY - fishRelativeMiddle.y
        )

        val objectAnimator = ObjectAnimator.ofFloat(ivFish, "x", "y", path)
        objectAnimator.duration = 2000

        val pathMeasure = PathMeasure(path, false)
        val tan = floatArrayOf(0f, 0f)

        objectAnimator.addUpdateListener {
            // 动画进度百分比
            val value = it.animatedFraction
            // 获取切线tan
            pathMeasure.getPosTan(pathMeasure.length * value, null, tan)

            val angle = Math.toDegrees(atan2(-tan[1].toDouble(), tan[0].toDouble()))
            fishDrawable.setFinishMainAngle(angle.toFloat())
        }

        objectAnimator.start()
    }

    private fun setMoveRipple(ripple: Float) {
        circleAlpha = (150 * (1 - ripple)).toInt()
        this.ripple = ripple

        invalidate()
    }

    /**
     * 向量求夹角
     */
    private fun includeAngle(o: PointF, a: PointF, b: PointF): Float {
        // 向量 oa * ob = (ax - ox) + (ay - oy) * (by - oy)
        val aob = (a.x - o.x) * (b.x - o.x) + (a.y - o.y) * (b.y - o.y)
        val oaLength = Math.sqrt(
            ((a.x - o.x) * (a.x - o.x) + (a.y - o.y) * (a.y - o.y)).toDouble()
        )
        val obLength = Math.sqrt(
            ((b.x - o.x) * (b.x - o.x) + (b.y - o.y) * (b.y - o.y)).toDouble()
        )

        // cosaob = (oa * ob) / (|oa|*|ob|)
        val cosaob = aob / (oaLength * obLength)

        // toDegrees: 弧度转为度数 acos: 反余弦
        val angleaob = Math.toDegrees(Math.acos(cosaob)).toFloat()
        // 判断方向
        val direction = (a.x - b.y) / (a.x - b.x) - (o.y - b.y) / (o.x - b.x)
        if (direction == 0f) {
            return if (aob > 0) {
                0f
            } else {
                180f
            }
        } else {
            return if (direction > 0) {
                -angleaob
            } else {
                angleaob
            }
        }
    }
}