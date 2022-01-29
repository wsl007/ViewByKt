package com.wsl.viewbykt.coordinator

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.Interpolator
import android.widget.LinearLayout
import android.widget.OverScroller
import androidx.core.view.NestedScrollingParent
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.round

/**
 * from: https://github.com/hongyangAndroid/Android-StickyNavLayout/blob/master/app/src/main/java/com/zhy/stickynavlayout/view/StickyNavLayout.java
 * 子view滚动带动整体
 */
class CoordinatorLinearLayout : LinearLayout, NestedScrollingParent {

    companion object {
        const val TAG = "CoordinatorLinearLayout"
    }

    constructor(context: Context) : this(context, null)

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : this(context, null, 0)

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initSet()
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int): Boolean {
        Log.e(TAG, "onStartNestedScroll")
        return true
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        Log.e(TAG, "onNestedScrollAccepted")
    }

    override fun onStopNestedScroll(target: View) {
        Log.e(TAG, "onStopNestedScroll")
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
        Log.e(TAG, "onNestedScroll")
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        val hiddenTop = dy > 0 && scrollY < (mTopViewHeight - mTopViewHeight / 4)
        val showTop = dy < 0 && scrollY >= 0 && target.canScrollVertically(-1)
        Log.e(TAG, "onNestedPreScroll hiddenTop=$hiddenTop showTop=$showTop")
        if (hiddenTop || showTop) {
            scrollBy(0, dy)
            consumed[1] = dy
        }
    }

    private val TOP_CHILD_FLING_THRESHOLD = 3
    override fun onNestedFling(
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        Log.e(TAG, "onNestedFling velocityY=$velocityY")
        var flag = consumed
        //如果是recyclerView 根据判断第一个元素是哪个位置可以判断是否消耗
        //这里判断如果第一个元素的位置是大于TOP_CHILD_FLING_THRESHOLD的
        //认为已经被消耗，在animateScroll里不会对velocityY<0时做处理
        if (target is RecyclerView && velocityY < 0) {
            val firstChild = target.getChildAt(0)
            val childAdapterPosition = target.getChildAdapterPosition(firstChild)
            flag = childAdapterPosition > TOP_CHILD_FLING_THRESHOLD
        }
        if (!flag) {
            animateScroll(velocityY, computeDuration(0f), flag)
        } else {
            animateScroll(velocityY, computeDuration(velocityY), flag)
        }
        return true
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        Log.e(TAG, "onNestedPreFling")
        // 不拦截，传递给子类
        return false
    }

    override fun getNestedScrollAxes(): Int {
        Log.e(TAG, "getNestedScrollAxes")
        return 0
    }

    /**
     * 根据速度计算滚动动画持续时间
     */
    private fun computeDuration(velocityY: Float): Int {
        var distince = 0
        if (velocityY > 0) {
            distince = abs(mTop!!.height - scrollY)
        } else {
            distince = abs(mTop!!.height - (mTop!!.height - scrollY))
        }

        var duration = 0
        var velocityY = abs(velocityY)
        if (velocityY > 0) {
            duration = 3 * round(1000 * (distince / velocityY)).toInt()
        } else {
            val distenceRatio = distince * 1f / height
            duration = ((distenceRatio + 1) * 150).toInt()
        }
        return duration
    }

    private fun animateScroll(velocityY: Float, duration: Int, consumed: Boolean) {
        val currentOffset = scrollY
        val topHeight = mTop!!.height
        if (mOffsetAnimator == null) {
            mOffsetAnimator = ValueAnimator()
//            mOffsetAnimator!!.interpolator = mInterpolator
            mOffsetAnimator!!.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
                override fun onAnimationUpdate(p0: ValueAnimator?) {
                    p0?.apply {
                        if (animatedValue is Int) {
                            scrollTo(0, animatedValue as Int)
                        }
                    }
                }
            })
        } else {
            mOffsetAnimator!!.cancel()
        }
        mOffsetAnimator!!.duration = min(duration, 600).toLong()

        if (velocityY >= 0) {
            mOffsetAnimator!!.setIntValues(currentOffset, topHeight)
            mOffsetAnimator!!.start()
        } else {
            //如果子View没有消耗down事件 那么就让自身滑到0
            if (!consumed) {
                mOffsetAnimator!!.setIntValues(currentOffset, 0)
                mOffsetAnimator!!.start()
            }
        }
    }

    private var mTop: View? = null
    private var mFix: View? = null
    private var mScroll: View? = null
    private var mTopViewHeight = 0
    private lateinit var mScroller: OverScroller
    private var mVelocityTracker: VelocityTracker? = null
    private var mOffsetAnimator: ValueAnimator? = null
    private lateinit var mInterpolator: Interpolator
    private var mTouchSlop = 0
    private var mMaximumVelocity = 0
    private var mMinimumVelocity = 0

    private var mLastY = 0f
    private var mDragging = false

    private fun initSet() {
        orientation = LinearLayout.VERTICAL

        mScroller = OverScroller(context)
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        mMaximumVelocity = ViewConfiguration.get(context).scaledMaximumFlingVelocity
        mMinimumVelocity = ViewConfiguration.get(context).scaledMinimumFlingVelocity
    }

    private fun initVelocityTracker() {
        if (mVelocityTracker == null)
            mVelocityTracker = VelocityTracker.obtain()
    }

    private fun recyclerVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker!!.recycle()
            mVelocityTracker = null
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        mTop = getChildAt(0)
        mFix = getChildAt(1)
        mScroll = getChildAt(2)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        mTop!!.measure(
//            widthMeasureSpec,
//            MeasureSpec.makeMeasureSpec(0, MeasureSpec.AT_MOST)
//        )

        Log.e(TAG, "mTop!!.measuredHeight = ${mTop!!.measuredHeight}")

        val params = mScroll!!.layoutParams
        params.height = measuredHeight - mFix!!.measuredHeight
        setMeasuredDimension(
            measuredWidth,
            mTop!!.measuredHeight + mFix!!.measuredHeight + mScroll!!.measuredHeight
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mTopViewHeight = mTop!!.measuredHeight
    }

    fun fling(velocityY: Int) {
        mScroller.fling(0, scrollY, 0, velocityY, 0, 0, 0, mTopViewHeight)
        invalidate()
    }

    override fun scrollTo(x: Int, y: Int) {
        var newY = y
        if (y < 0)
            newY = 0
        if (y > mTopViewHeight) {
            newY = mTopViewHeight
        }
        if (newY != scrollY) {
            super.scrollTo(x, y)
        }
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.currY)
            invalidate()
        }
    }
}