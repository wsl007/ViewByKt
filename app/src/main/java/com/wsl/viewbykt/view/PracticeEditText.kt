package com.wsl.viewbykt.view

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatEditText
import com.wsl.viewbykt.utils.px

private val TEXT_SIZE = 12f.px
private val TEXT_MARGIN = 8f.px

private val HORIZONTAL_OFFSET = 8f.px
private val VERTICAL_OFFSET = 22f.px

private val MOVE_OFFSET = 16f.px

class PracticeEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var labelShow = false

    var useFloatLabel = true
        set(value) {
            if (field != value) {
                field = value
                if (field) {
                    setPadding(
                        paddingLeft,
                        (paddingTop + TEXT_MARGIN + TEXT_SIZE).toInt(),
                        paddingRight,
                        paddingBottom
                    )
                } else {
                    setPadding(
                        paddingLeft,
                        (paddingTop - TEXT_MARGIN - TEXT_SIZE).toInt(),
                        paddingRight,
                        paddingBottom
                    )
                }
            }
        }

    var labelFraction = 0f
        set(value) {
            field = value
            invalidate()
        }

    private val animator by lazy {
        ObjectAnimator.ofFloat(this, "labelFraction", 0f, 1f)
    }

    init {
        paint.textSize = TEXT_SIZE
        if (useFloatLabel) {
            setPadding(
                paddingLeft,
                (paddingTop + TEXT_MARGIN + TEXT_SIZE).toInt(),
                paddingRight,
                paddingBottom
            )
        }
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        if (labelShow && text.isNullOrEmpty()) {
            labelShow = false
            animator.reverse()
        } else if (!labelShow && !text.isNullOrEmpty()) {
            labelShow = true
            animator.start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (useFloatLabel) {
            paint.alpha = (labelFraction * 0xff).toInt()
            Log.i("view-", "alpha---${paint.alpha}")
            val v = VERTICAL_OFFSET + MOVE_OFFSET * (1 - labelFraction)
            canvas.drawText(hint.toString(), HORIZONTAL_OFFSET, v, paint)
        }
    }
}