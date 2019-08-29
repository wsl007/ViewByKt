package com.wsl.viewbykt.viewgroup

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import kotlin.math.max

/**
 * 子元素重叠显示，
 * 点击切换
 */
class OverlapPage : ViewGroup {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //测量子view
        measureChildren(widthMeasureSpec, heightMeasureSpec)

        val width = measureWidth(widthMeasureSpec)
        val height = measureHeight(heightMeasureSpec)

        setMeasuredDimension(width, height)
    }


    private fun measureWidth(widthMeasureSpec: Int): Int {
        var width = 0

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize
        } else {
            //如果为wrap_content，则取所有子view宽度和
            for (i in 0 until childCount) {
                val child = this[i]
                val layoutParams = child.layoutParams as LayoutParams
                width += child.measuredWidth
                +layoutParams.leftMargin
                +layoutParams.rightMargin
            }
        }
        return width
    }

    private fun measureHeight(heightMeasureSpec: Int): Int {
        val height: Int
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize
        } else {
            //如果高度为wrap_content,取最高子view高
            var maxHeight = 0
            for (i in 0 until childCount) {
                val child = this[i]
                val layoutParams = child.layoutParams as LayoutParams
                maxHeight = max(
                    maxHeight,
                    child.measuredHeight
                            + layoutParams.topMargin
                            + layoutParams.bottomMargin
                )
            }
            height = maxHeight
        }
        return height
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (index in 0 until childCount) {
            val child = this[index]
            val baseLine = getBaseLineByChild(child)
            layoutChild(child, baseLine)
        }
    }

    /**
     * 获取基准线
     */
    private fun getBaseLineByChild(child: View) = when (indexOfChild(child)) {
        0 -> width / 4 //左边线 最底层子view在左边
        1 -> width / 2 + width / 4 //右边的线
        2 -> width / 2
        else -> 0
    }

    /**
     * 布局子view
     */
    private fun layoutChild(child: View, baseLine: Int) {
        val childWidth = child.measuredWidth
        val childHeight = child.measuredHeight
        val baseCenterY = height / 2
        val left = baseLine - childWidth / 2
        val right = left + childWidth

        val top = baseCenterY - childHeight / 2
        val bottom = top + childHeight

        val lp = child.layoutParams as LayoutParams
        child.layout(
            left + paddingLeft + lp.leftMargin,
            top + paddingTop + lp.topMargin,
            right - paddingRight + lp.rightMargin,
            bottom - paddingBottom + lp.bottomMargin
        )
    }

    class LayoutParams : MarginLayoutParams {

        var scale = 0f
        var alpha = 0f

        constructor(c: Context, attrs: AttributeSet) : super(c, attrs)
        constructor(width: Int, height: Int) : super(width, height)
        constructor(source: ViewGroup.LayoutParams) : super(source)
    }
}