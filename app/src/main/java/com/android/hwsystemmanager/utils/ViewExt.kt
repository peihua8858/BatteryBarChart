package com.android.hwsystemmanager.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.text.TextPaint
import android.text.TextUtils
import android.view.View
import androidx.annotation.Keep
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.settings.util.fontScale
import com.android.settings.util.isLandscape
import java.util.Locale
import kotlin.math.abs


/**
 * [View]宽度展开折叠动画
 * @param   isExpend  true:展开  false:收起
 * @param   width     展开时的宽度
 * @param duration 动画时长
 * @author dingpeihua
 * @date 2025/7/11 16:32
 **/
@JvmOverloads
fun View.animationWidth(isExpend: Boolean, width: Int = 0, duration: Long = 300) {
    var offset = width
    if (offset == 0) {
        val widthId = 0x20133542
        offset = getTag(widthId) as? Int ?: 0
        if (offset == 0) {
            offset = this.width
            if (offset == 0) {
                measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                offset = measuredWidth
            }
            setTag(widthId, offset)
        }
    }
    val viewWrapper = ViewWrapper(this)
    val animation = if (isExpend) ObjectAnimator.ofInt(
        viewWrapper, "width", 0, width
    )
    else ObjectAnimator.ofInt(viewWrapper, "width", width, 0)
    animation.duration = duration
    animation.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            if (!isExpend) this@animationWidth.visibility = View.GONE
        }
    })
    animation.addUpdateListener {
        viewWrapper.setWidth(it.animatedValue as Int)
        if (isExpend) this@animationWidth.visibility = View.VISIBLE
    }
    animation.start()
}


/**
 * [View]高度展开折叠动画
 * @param   isExpend  true:展开  false:收起
 * @param   height     展开时的宽度
 * @param duration 动画时长
 * @author dingpeihua
 * @date 2025/7/11 16:31
 **/
@JvmOverloads
fun View.animationHeight(isExpend: Boolean, height: Int = 0, duration: Long = 300) {
    var offset = height
    if (offset == 0) {
        val heightId = 0x20133543
        offset = getTag(heightId) as? Int ?: 0
        if (offset == 0) {
            offset = this.height
            if (offset == 0) {
                measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                offset = measuredHeight
            }
            setTag(heightId, offset)
        }
        setTag(heightId, offset)
    }
    val viewWrapper = ViewWrapper(this)
    val animation = if (isExpend) ObjectAnimator.ofInt(
        viewWrapper, "height", 0, offset
    )
    else ObjectAnimator.ofInt(viewWrapper, "height", offset, 0)
    animation.duration = duration
    animation.addListener(object : AnimatorListenerAdapter() {

        override fun onAnimationEnd(animation: Animator) {
            if (!isExpend) this@animationHeight.visibility = View.GONE
        }
    })
    animation.addUpdateListener {
        val value = it.animatedValue as Int
        viewWrapper.setHeight(value)
        if (isExpend && value > 0) {
            this@animationHeight.visibility = View.VISIBLE
        }
    }
    animation.start()
}


private class ViewWrapper(val view: View) {

    @Keep
    fun setWidth(width: Int) {
        view.layoutParams.width = width
        view.requestLayout() //必须调用，否则宽度改变但UI没有刷新
    }

    fun getWidth(): Int {
        return view.layoutParams.width
    }

    @Keep
    fun setHeight(height: Int) {
        view.layoutParams.height = height
        view.requestLayout() //必须调用，否则宽度改变但UI没有刷新
    }

    fun getHeight(): Int {
        return view.layoutParams.height
    }
}

var RecyclerView.spanCount: Int
    get() {
        val manager = layoutManager
        if (manager is GridLayoutManager) {
            return manager.spanCount
        } else if (manager is StaggeredGridLayoutManager) {
            return manager.spanCount
        }
        return 1
    }
    set(value) {
        val manager = layoutManager
        if (manager is GridLayoutManager) {
            manager.spanCount = value
        } else if (manager is StaggeredGridLayoutManager) {
            manager.spanCount = value
        }
    }

val View.isLandscape: Boolean
    get() = context.isLandscape

fun View.dp2px(dp: Float): Float {
    return (dp * resources.displayMetrics.density + 0.5f)
}

fun View.dp2px(dp: Int): Int {
    return dp2px(dp.toFloat()).toInt()
}

fun View.px2dp(px: Float): Float {
    return px / resources.displayMetrics.density
}

val isLayoutRtl: Boolean
    get() = TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_RTL

fun View.createPaint(textSize: Int): TextPaint {
    return context.createPaint(textSize)
}

fun Context?.r(): Boolean {
    if (abs(fontScale - 2.0f) < 1.0E-7f) {
        return true
    }
    return false
}

fun Context?.v(): Boolean {
    if (abs(fontScale - 3.2f) < 1.0E-7f) {
        return true
    }
    return false
}