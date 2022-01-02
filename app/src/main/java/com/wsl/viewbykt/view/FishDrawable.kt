package com.wsl.viewbykt.view

import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.animation.LinearInterpolator
import kotlin.math.cos
import kotlin.math.sin

/**
 * 🐟 型 drawable
 */
class FishDrawable : Drawable() {

    private val mPath: Path
    private val mPaint: Paint

    // 除鱼身体的所有透明度
    private val OTHER_ALPHA = 110

    // 鱼身透明度
    private val BODY_ALPHA = 160

    // 转弯更自然的重心
    private val middlePoint: PointF

    // 鱼的主角度
    private var finishMainAngle = 90f

    // 头长
    public val HEAD_RADUIS = 30f

    // 鱼身长度
    private val BODY_LENGTH = 3.2f * HEAD_RADUIS

    // ----- 鱼鳍
    private val FIND_FINS_LENGTH = 0.9f * HEAD_RADUIS
    private val FINS_LENGTH = 1.3f * HEAD_RADUIS

    // ----- 鱼尾
    private val BIG_CIRCLE_RADIUS = 0.7f * HEAD_RADUIS
    private val MIDDLE_CIRCLE_RADIUS = 0.6f * BIG_CIRCLE_RADIUS
    private val SMALL_CIRCLE_RADIUS = 0.3f * MIDDLE_CIRCLE_RADIUS
    private val FIND_MIDDLE_CIRCLE_LENGTH = BIG_CIRCLE_RADIUS + MIDDLE_CIRCLE_RADIUS
    private val FIND_SMALL_CIRLCE_LENGTH = MIDDLE_CIRCLE_RADIUS * (0.4f + 2.7f)
    private val FIND_TRIANGLE_LENGTH = MIDDLE_CIRCLE_RADIUS * 2.7f

    private var currentValue = 0f

    private lateinit var headPoint: PointF

    init {
        mPath = Path()
        mPaint = Paint()
        mPaint.style = Paint.Style.FILL
        mPaint.setARGB(OTHER_ALPHA, 244, 92, 71)
        // 抗锯齿
        mPaint.isAntiAlias = true
        // 防抖
        mPaint.isDither = true

        middlePoint = PointF(4.19f * HEAD_RADUIS, 4.19f * HEAD_RADUIS)

        val valueAnimator = ValueAnimator.ofFloat(0f, 360f)
        valueAnimator.duration = 1000
        valueAnimator.repeatMode = ValueAnimator.RESTART
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.addUpdateListener {
            currentValue = it.animatedValue as Float
            invalidateSelf()
        }
        valueAnimator.start()
    }

    override fun draw(canvas: Canvas) {
        val fishAngle =
            (finishMainAngle + sin(Math.toRadians(currentValue.toDouble())) * 10).toFloat()

        // 绘制圆形鱼头
        headPoint = calculatePoint(middlePoint, BODY_LENGTH / 2, fishAngle)
        canvas.drawCircle(headPoint.x, headPoint.y, HEAD_RADUIS, mPaint)

        // 绘制右鱼鳍
        val rightFinsPoint = calculatePoint(headPoint, FIND_FINS_LENGTH, fishAngle - 110)
        makeFins(canvas, rightFinsPoint, fishAngle, true)
        // 绘制左鱼鳍
        val leftFinsPoint = calculatePoint(headPoint, FIND_FINS_LENGTH, fishAngle + 110)
        makeFins(canvas, leftFinsPoint, fishAngle, false)

        // 身体底部的中心点
        val bodyBottomCenterPoint = calculatePoint(
            headPoint,
            BODY_LENGTH,
            fishAngle - 180
        )
        // 花节肢1
        val middleCircleCenterPoint = makeSegment(
            canvas,
            bodyBottomCenterPoint,
            BIG_CIRCLE_RADIUS,
            MIDDLE_CIRCLE_RADIUS,
            FIND_MIDDLE_CIRCLE_LENGTH,
            fishAngle,
            true
        )
        // 画节肢2
//        val middleCircleCenterPoint = calculatePoint(
//            bodyBottomCenterPoint,
//            FIND_MIDDLE_CIRCLE_LENGTH,
//            fishAngle - 180
//        )
        makeSegment(
            canvas,
            middleCircleCenterPoint,
            MIDDLE_CIRCLE_RADIUS,
            SMALL_CIRCLE_RADIUS,
            FIND_SMALL_CIRLCE_LENGTH,
            fishAngle,
            false
        )

        // 绘制尾部
        makeTriangle(
            canvas,
            middleCircleCenterPoint,
            FIND_TRIANGLE_LENGTH,
            BIG_CIRCLE_RADIUS,
            fishAngle
        )
        makeTriangle(
            canvas,
            middleCircleCenterPoint,
            FIND_TRIANGLE_LENGTH - 10,
            BIG_CIRCLE_RADIUS - 20,
            fishAngle
        )

        // 绘制身体
        makeBody(
            canvas,
            headPoint,
            bodyBottomCenterPoint,
            fishAngle
        )
    }

    private fun makeBody(
        canvas: Canvas,
        headPointF: PointF,
        bodyBottomCenterPointF: PointF,
        fishAngle: Float
    ) {
        // 身体的四个点
        val topLeftPointF = calculatePoint(
            headPointF,
            HEAD_RADUIS,
            fishAngle + 90
        )
        val topRightPointF = calculatePoint(
            headPointF,
            HEAD_RADUIS,
            fishAngle - 90
        )
        val bottomLeftPointF = calculatePoint(
            bodyBottomCenterPointF,
            BIG_CIRCLE_RADIUS,
            fishAngle + 90
        )
        val bottomRightPointF = calculatePoint(
            bodyBottomCenterPointF,
            BIG_CIRCLE_RADIUS,
            fishAngle - 90
        )

        // 二阶
        val controlLeft = calculatePoint(
            headPointF,
            BODY_LENGTH * 0.56f,
            fishAngle + 130
        )
        val controlRight = calculatePoint(
            headPointF,
            BODY_LENGTH * 0.56f,
            fishAngle - 130
        )

        mPath.reset()
        mPath.moveTo(topLeftPointF.x, topLeftPointF.y)
        mPath.quadTo(
            controlLeft.x, controlLeft.y,
            bottomLeftPointF.x, bottomLeftPointF.y
        )
        mPath.lineTo(bottomRightPointF.x, bottomRightPointF.y)
        mPath.quadTo(
            controlRight.x, controlRight.y,
            topRightPointF.x, topRightPointF.y
        )
        mPaint.alpha = BODY_ALPHA
        canvas.drawPath(mPath, mPaint)
    }

    private fun makeTriangle(
        canvas: Canvas,
        startPoint: PointF,
        findCenterLength: Float,
        findEdgeLength: Float,
        fishAngle: Float
    ) {
        val angle =
            (finishMainAngle + sin(Math.toRadians(3 * currentValue.toDouble())) * 30).toFloat()
        // 三角形底部的中心点
        val centerPoint = calculatePoint(
            startPoint, findCenterLength, angle - 180
        )
        val leftPoint = calculatePoint(
            centerPoint, findEdgeLength, angle + 90
        )
        val rightPoint = calculatePoint(
            centerPoint, findEdgeLength, angle - 90
        )
        // 绘制三角形
        mPath.reset()
        mPath.moveTo(startPoint.x, startPoint.y)
        mPath.lineTo(leftPoint.x, leftPoint.y)
        mPath.lineTo(rightPoint.x, rightPoint.y)
        canvas.drawPath(mPath, mPaint)
    }

    private fun makeSegment(
        canvas: Canvas,
        bottomCenterPoint: PointF,
        bigRadius: Float,
        smallRadius: Float,
        findSmallCircleLength: Float,
        fishAngle: Float,
        hasBigCircle: Boolean
    ): PointF {
        val angle = if (hasBigCircle) {
            // cos 先于 sin
            (finishMainAngle + cos(Math.toRadians(2 * currentValue.toDouble())) * 20).toFloat()
        } else {
            (finishMainAngle + sin(Math.toRadians(3 * currentValue.toDouble())) * 30).toFloat()
        }

        // 梯形上底的中心点 中等大的圆的圆心
        val upperCenterPoint = calculatePoint(
            bottomCenterPoint,
            findSmallCircleLength,
            angle - 180
        )

        // 梯形的四个点
        val bottomLeftPoint = calculatePoint(
            bottomCenterPoint,
            bigRadius,
            angle + 90
        )
        val bottomRightPoint = calculatePoint(
            bottomCenterPoint, bigRadius, angle - 90
        )
        val upperLeftPoint = calculatePoint(
            upperCenterPoint, smallRadius, angle + 90
        )
        val upperRightPoint = calculatePoint(
            upperCenterPoint, smallRadius, angle - 90
        )
        if (hasBigCircle) {
            canvas.drawCircle(
                bottomCenterPoint.x,
                bottomCenterPoint.y, bigRadius, mPaint
            )
        }
        canvas.drawCircle(upperCenterPoint.x, upperCenterPoint.y, smallRadius, mPaint)
        //绘制梯形
        mPath.reset()
        mPath.moveTo(bottomLeftPoint.x, bottomLeftPoint.y)
        mPath.lineTo(upperLeftPoint.x, upperLeftPoint.y)
        mPath.lineTo(upperRightPoint.x, upperRightPoint.y)
        mPath.lineTo(bottomRightPoint.x, bottomRightPoint.y)
        canvas.drawPath(mPath, mPaint)

        return upperCenterPoint
    }

    private fun makeFins(
        canvas: Canvas,
        startPoint: PointF,
        fishAngle: Float,
        isRightFins: Boolean
    ) {
        val controlAngle = 115

        val endPoint = calculatePoint(startPoint, FINS_LENGTH, fishAngle - 180)
        val angle = if (isRightFins) fishAngle - controlAngle else fishAngle + controlAngle
        val controlPoint = calculatePoint(startPoint, 1.8f * FINS_LENGTH, angle)

        mPath.reset()
        mPath.moveTo(startPoint.x, startPoint.y)
        mPath.quadTo(
            controlPoint.x, controlPoint.y,
            endPoint.x, endPoint.y
        )
        canvas.drawPath(mPath, mPaint)
    }

    /**
     * 计算相对重心点的坐标
     */
    fun calculatePoint(
        startPointF: PointF,
        length: Float,
        angle: Float
    ): PointF {
        val r = Math.toRadians(angle.toDouble())

        val deltaX = (Math.cos(r) * length).toFloat()
        val deltaY = (-Math.sin(r) * length).toFloat()
        return PointF(
            startPointF.x + deltaX,
            startPointF.y + deltaY
        )
    }

    override fun setAlpha(p0: Int) {
        mPaint.alpha = p0
    }

    override fun setColorFilter(p0: ColorFilter?) {
        mPaint.colorFilter = p0
    }

    /**
     * 这个值，可以根据setAlpha中设置的值进行调整
     * 比如：alpha == 0 设置 TRANSPARENT 透明，完全不显示任何东西
     * alpha == 255 设置 OPAQUE 完全不透明
     * 其他时候，设置 TRANSLUCENT 只有绘制的地方才覆盖地下的内容
     */
    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun getIntrinsicWidth(): Int {
        return (8.38f * HEAD_RADUIS).toInt()
    }

    override fun getIntrinsicHeight(): Int {
        return (8.38f * HEAD_RADUIS).toInt()
    }

    fun getMiddlePointF(): PointF {
        return middlePoint
    }

    fun getHeadPointF(): PointF {
        return headPoint
    }

    fun setFinishMainAngle(angle: Float) {
        finishMainAngle = angle
    }
}