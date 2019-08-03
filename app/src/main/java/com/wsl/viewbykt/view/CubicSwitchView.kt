package com.wsl.viewbykt.view

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.wsl.viewbykt.R
import kotlin.math.abs
import kotlin.math.min

/**
 * 3阶贝塞尔曲线
 * 切换开关
 * https://github.com/GwonHyeok/StickySwitch/blob/master/stickyswitch/src/main/kotlin/io/ghyeok/stickyswitch/widget/StickySwitch.kt
 */
class CubicSwitchView : View {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs, defStyleAttr)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context, attrs, defStyleAttr, defStyleRes
    ) {
        init(attrs, defStyleAttr, defStyleRes)
    }

    var leftIcon: Drawable? = null
        set(drawable) {
            field = drawable
            invalidate()
        }
    var rightIcon: Drawable? = null

    var leftText = ""
    var rightText = ""
    var sliderBackgroundColor = 0xFF181821.toInt()
    var switchColor = 0xFF2371FA.toInt()
    var textColor = 0xFF181821.toInt()

    var iconSize = 100
    var iconPadding = 70

    private val sliderBackgroundPaint = Paint()
    private val sliderBackgroundRect = RectF()

    private val switchBackgroundPaint = Paint()
    private val leftTextPaint = Paint()
    private val leftTextRect = Rect()
    private val rightTextPaint = Paint()
    private val rightTextRect = Rect()

    private var leftTextSize = 50f
    private var rightTextSize = 50f
    private val textAlphaMax = 255
    private val textAlphaMin = 163

    private var leftTextAlpha = textAlphaMax
    private var rightTextAlpha = textAlphaMin

    private var textSize = 50
    private var selectedTextSize = 50
    private var isSwitchOn = false

    private var animatePercent: Double = 0.0
    private var animateBounceRate: Double = 1.0

    enum class AnimationType {
        LINE,
        CURVED
    }

    var animationType = AnimationType.LINE

    var animatorSet: AnimatorSet? = null
    var animatonDuration: Long = 600
    var textVisibility = TextVisibility.VISIBILE

    enum class TextVisibility {
        VISIBILE,
        INVISIBILE,
        GONE
    }

    init {
        isClickable = true
    }

    private fun init(attrs: AttributeSet?, defStyleAttr: Int = 0, defStyleRes: Int = 0) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CubicSwitchView, defStyleAttr, defStyleRes)

        leftIcon = typedArray.getDrawable(R.styleable.CubicSwitchView_ss_leftIcon)
        leftText = typedArray.getString(R.styleable.CubicSwitchView_ss_leftText) ?: leftText

        rightIcon = typedArray.getDrawable(R.styleable.CubicSwitchView_ss_rightIcon)
        rightText = typedArray.getString(R.styleable.CubicSwitchView_ss_rightText) ?: rightText

        iconSize = typedArray.getDimensionPixelSize(R.styleable.CubicSwitchView_ss_iconSize, iconSize)
        iconPadding = typedArray.getDimensionPixelSize(R.styleable.CubicSwitchView_ss_iconPadding, iconPadding)

        textSize = typedArray.getDimensionPixelSize(R.styleable.CubicSwitchView_ss_textSize, textSize)
        selectedTextSize =
            typedArray.getDimensionPixelSize(R.styleable.CubicSwitchView_ss_selectedTextSize, selectedTextSize)

        leftTextSize = selectedTextSize.toFloat()
        rightTextSize = textSize.toFloat()

        sliderBackgroundColor =
            typedArray.getColor(R.styleable.CubicSwitchView_ss_sliderBackgroundColor, sliderBackgroundColor)

        switchColor = typedArray.getColor(R.styleable.CubicSwitchView_ss_switchColor, switchColor)

        textColor = typedArray.getColor(R.styleable.CubicSwitchView_ss_textColor, textColor)

        animatonDuration =
            typedArray.getInt(R.styleable.CubicSwitchView_ss_animationDuration, animatonDuration.toInt()).toLong()

        animationType = AnimationType.values()[typedArray.getInt(
            R.styleable.CubicSwitchView_ss_animationType,
            AnimationType.LINE.ordinal
        )]

        typedArray.recycle()
    }

    private val connectionPath = Path()
    val xParam = 1 / 2f //Math.sin(Math.PI / 6).toFloat()
    val yParam = 0.86602540378f //Math.cos(Math.PI / 6).toFloat()


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureText()

        val diameter = (iconPadding + iconSize / 2) * 2
        val textWith = leftTextRect.width() + rightTextRect.width()
        val measuredTextHeight = if (textVisibility == TextVisibility.GONE)
            0 else selectedTextSize * 2

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var heightSize = 0

        when (heightMode) {
            MeasureSpec.UNSPECIFIED -> heightSize = heightMeasureSpec
            MeasureSpec.AT_MOST -> heightSize = diameter + measuredTextHeight
            MeasureSpec.EXACTLY -> heightSize = MeasureSpec.getSize(heightMeasureSpec)
        }

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var widthSize = 0

        when (widthMode) {
            MeasureSpec.UNSPECIFIED -> widthSize = widthMeasureSpec
            MeasureSpec.AT_MOST -> widthSize = diameter * 2 + textWith
            MeasureSpec.EXACTLY -> widthSize = MeasureSpec.getSize(widthMeasureSpec)
        }

        setMeasuredDimension(widthSize, heightSize)
    }

    /**
     * Draw Sticky Switch View
     *
     * Animation
     *
     * 0% ~ 50%
     * radius : circle radius -> circle radius / 2
     * x      : x -> x + widthSpace
     * y      : y
     *
     * 50% ~ 100%
     * radius : circle radius / 2 -> circle radius
     * x      : x + widthSpace -> x + widthSpace
     * y      : y
     *
     * @param canvas the canvas on which the background will be drawn
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null)
            return

        val iconMarginLeft = iconPadding
        val iconMarginBottom = iconPadding
        val iconMarginRight = iconPadding
        val iconMarginTop = iconPadding

        val iconWidth = iconSize;
        val iconHeight = iconSize;

        val sliderRadius = iconMarginTop + iconHeight / 2f
        val circleRadius = sliderRadius

        sliderBackgroundPaint.color = sliderBackgroundColor
        sliderBackgroundRect.set(
            0f, 0f, measuredWidth.toFloat(),
            (iconMarginTop + iconHeight + iconMarginBottom).toFloat()
        )
        canvas.drawRoundRect(sliderBackgroundRect, sliderRadius, sliderRadius, sliderBackgroundPaint)

        canvas.save()

        switchBackgroundPaint.color = switchColor

        val isBefore = animatePercent in 0.0..0.5
        val widthSpace = measuredWidth - circleRadius * 2
        val ocX = circleRadius + widthSpace * min(1.0, animatePercent * 2)
        val ocY = circleRadius
        val ocRadius = circleRadius * (if (isBefore) 1.0 - animatePercent else animatePercent)

        val ccX = (circleRadius + widthSpace * (if (isBefore) 0.0 else abs(0.5 - animatePercent) * 2))
        val ccY = circleRadius
        val ccRadius = ocRadius

        val rectL = ccX
        val rectR = ocX

        canvas.drawCircle(
            ocX.toFloat(), ocY, evaluateBounceRate(ocRadius).toFloat(),
            switchBackgroundPaint
        )
        canvas.drawCircle(
            ccX.toFloat(), ccY, evaluateBounceRate(ccRadius).toFloat(),
            switchBackgroundPaint
        )

        if (animationType == AnimationType.LINE) {
            val rectT = circleRadius - circleRadius / 2
            val rectB = circleRadius + circleRadius / 2

            canvas.drawCircle(
                ccX.toFloat(), ccY, evaluateBounceRate(ccRadius).toFloat(),
                switchBackgroundPaint
            )
            canvas.drawRect(
                rectL.toFloat(),
                rectT, rectR.toFloat(), rectB, switchBackgroundPaint
            )
        } else if (animationType == AnimationType.CURVED) {
            if (animatePercent > 0 && animatePercent < 1) {
                connectionPath.rewind()

                val rectLCurve = rectL.toFloat() + ccRadius.toFloat() * xParam
                val rectRCurve = rectR.toFloat() - ccRadius.toFloat() * xParam

                val rectTCurve = circleRadius - ccRadius.toFloat() * yParam
                val rectBCurve = circleRadius + ccRadius.toFloat() * yParam

                val middlePointX = (rectRCurve + rectLCurve) / 2
                val middlePointY = (rectTCurve + rectBCurve) / 2

                connectionPath.moveTo(rectLCurve, rectTCurve)

                connectionPath.cubicTo(
                    rectLCurve,
                    rectTCurve,
                    middlePointX,
                    middlePointY,
                    rectRCurve,
                    rectTCurve
                )
                connectionPath.lineTo(
                    rectRCurve,
                    rectBCurve
                )
                connectionPath.cubicTo(
                    rectRCurve,
                    rectBCurve,
                    middlePointX,
                    middlePointY,
                    rectLCurve,
                    rectBCurve
                )
                connectionPath.close()

                canvas.drawPath(connectionPath, switchBackgroundPaint)
            }
        }

        canvas.restore()

        leftIcon?.run {
            canvas.save()
            setBounds(
                iconMarginLeft,
                iconMarginTop, iconMarginLeft + iconWidth,
                iconMarginTop + iconHeight
            )
            alpha = if (isSwitchOn) 153 else 255
            draw(canvas)
            canvas.restore()
        }
        rightIcon?.run {
            canvas.save()
            setBounds(
                measuredWidth - iconWidth - iconMarginRight,
                iconMarginTop, measuredWidth - iconMarginRight,
                iconMarginTop + iconHeight
            )
            alpha = if (!isSwitchOn) 153 else 255
            draw(canvas)
            canvas.restore()
        }

        val bottomSpaceHeight = measuredHeight - circleRadius * 2

        leftTextPaint.color = textColor
        leftTextPaint.alpha = leftTextAlpha
        rightTextPaint.color = textColor
        rightTextPaint.alpha = rightTextAlpha

        leftTextPaint.textSize = leftTextSize
        rightTextPaint.textSize = rightTextSize

        if (textVisibility == TextVisibility.VISIBILE) {
            measureText()

            val leftTextX = (circleRadius * 2 - leftTextRect.width()) * 0.5
            val leftTextY = circleRadius * 2 + bottomSpaceHeight * .5 + leftTextRect.height() * .25

            canvas.save()
            canvas.drawText(
                leftText, leftTextX.toFloat(),
                leftTextY.toFloat(), leftTextPaint
            )
            canvas.restore()

            val rightTextX = measuredWidth - circleRadius - rightTextRect.width() * .5
            val rightTextY = circleRadius * 2 + bottomSpaceHeight * .5
            +rightTextRect.height() * .25

            canvas.save()
            canvas.drawText(
                rightText, rightTextX.toFloat(),
                rightTextY.toFloat(), rightTextPaint
            )
            canvas.restore()
        }
    }

    private fun evaluateBounceRate(value: Double)
            : Double = value * animateBounceRate

    private fun measureText() {
        leftTextPaint.getTextBounds(
            leftText, 0, leftText.length,
            leftTextRect
        )
        rightTextPaint.getTextBounds(
            rightText, 0, rightText.length,
            rightTextRect
        )
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (isEnabled.not() || isClickable.not()) return false
        when (event?.action) {
            ACTION_UP -> {
                isSwitchOn = isSwitchOn.not()
                animateCheckState(isSwitchOn)
                notifySelectedChange()
            }
        }
        return super.onTouchEvent(event)
    }

    private fun notifySelectedChange() {

    }

    private fun animateCheckState(newCheckedState: Boolean) {
        this.animatorSet = AnimatorSet()
        animatorSet?.playTogether(
            getLiquidAnimator(newCheckedState),
            leftTextSizeAnimator(newCheckedState),
            rightTextSizeAnimatior(newCheckedState),
            leftTextAlphaAnimator(newCheckedState),
            rightTextAlphaAnimator(newCheckedState),
            getBounceAnimator()
        )
        animatorSet?.start()
        Log.i("tag","animatorSet click")
    }

    private fun getLiquidAnimator(newCheckedState: Boolean): Animator {
        val liquidAnimtor = ValueAnimator.ofFloat(animatePercent.toFloat(), if (newCheckedState) 1f else 0f)
        liquidAnimtor.duration = animatonDuration
        liquidAnimtor.interpolator = AccelerateInterpolator()
        liquidAnimtor.addUpdateListener {
            animatePercent = (it.animatedValue as Float).toDouble()
        }
        return liquidAnimtor
    }

    private fun leftTextSizeAnimator(newCheckedState: Boolean): Animator {
        val toTextSize = if (newCheckedState) textSize else selectedTextSize
        val animator = ValueAnimator.ofFloat(leftTextSize, toTextSize.toFloat())
        animator.interpolator = AccelerateInterpolator()
        animator.startDelay = animatonDuration / 3
        animator.duration = animatonDuration - animatonDuration / 3
        animator.addUpdateListener {
            leftTextSize = it.animatedValue as Float
        }
        return animator
    }

    private fun rightTextSizeAnimatior(newCheckedState: Boolean): Animator {
        val toTextSize = if (newCheckedState) selectedTextSize else textSize
        val animator = ValueAnimator.ofFloat(rightTextSize, toTextSize.toFloat())
        animator.interpolator = AccelerateInterpolator()
        animator.startDelay = animatonDuration / 3
        animator.duration = animatonDuration - animatonDuration / 3
        animator.addUpdateListener {
            rightTextSize = it.animatedValue as Float
        }
        return animator
    }

    private fun leftTextAlphaAnimator(newCheckedState: Boolean): Animator {
        val toAlpha = if (newCheckedState) textAlphaMin else textAlphaMax
        val animator = ValueAnimator.ofInt(leftTextAlpha, toAlpha)
        animator.interpolator = AccelerateInterpolator()
        animator.startDelay = animatonDuration / 3
        animator.duration = animatonDuration - animatonDuration / 3
        animator.addUpdateListener {
            leftTextAlpha = it.animatedValue as Int
        }
        return animator
    }

    private fun rightTextAlphaAnimator(newCheckedState: Boolean): Animator {
        val toAlpha = if (newCheckedState) textAlphaMax else textAlphaMin
        val animator = ValueAnimator.ofInt(rightTextAlpha, toAlpha)
        animator.interpolator = AccelerateInterpolator()
        animator.startDelay = animatonDuration / 3
        animator.duration = animatonDuration - animatonDuration / 3
        animator.addUpdateListener {
            rightTextAlpha = it.animatedValue as Int
        }
        return animator
    }

    private fun getBounceAnimator(): Animator {
        val anim = ValueAnimator.ofFloat(1f, .9f, 1f)
        anim.duration = (animatonDuration * .41).toLong()
        anim.duration = animatonDuration
        anim.interpolator = DecelerateInterpolator()
        anim.addUpdateListener {
            animateBounceRate = (it.animatedValue as Float).toDouble()
            invalidate()
        }
        return anim
    }
}
