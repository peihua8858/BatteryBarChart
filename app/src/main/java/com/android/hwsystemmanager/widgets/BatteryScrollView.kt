package com.android.hwsystemmanager.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView

class BatteryScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : NestedScrollView(
    context, attrs,
    defStyleAttr
) {

    var mDoNotIntercept: Boolean = true
    override fun canScrollVertically(i4: Int): Boolean {
        return true
    }

    fun getDoNotIntercept(): Boolean {
        return this.mDoNotIntercept
    }

    override fun onInterceptTouchEvent(motionEvent: MotionEvent): Boolean {
        return this.mDoNotIntercept && super.onInterceptTouchEvent(motionEvent)
    }

    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        return this.mDoNotIntercept && super.onTouchEvent(motionEvent)
    }

    fun setDoNotIntercept(isDoNotIntercept: Boolean) {
        this.mDoNotIntercept = isDoNotIntercept
    }
}