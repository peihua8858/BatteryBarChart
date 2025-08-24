package com.android.settings.util

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import com.android.hwsystemmanager.utils.Logcat

val Context.isLandscape: Boolean
    get() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

fun Context.parseAttribute(@AttrRes attrRes: Int): Int {
    var i8: Int = 0
    val typedValue = TypedValue()
    if (theme.resolveAttribute(attrRes, typedValue, true) && (typedValue.resourceId.also {
            i8 = it
        }) != 0
    ) {
        return i8
    }
    Logcat.d("ContextExt", "resource get fail, resId is $attrRes")
    return 0
}

fun Context.getColor(resId: Int, z10: Boolean): Int {
    val d10: Int = parseAttribute(resId)
    if (d10 == 0) {
        Logcat.d("ContextExt", "resource get fail, resId is $resId")
        return 0
    }
    val theme = if (z10) theme else null
    try {
        return resources.getColor(d10, theme)
    } catch (unused: Resources.NotFoundException) {
        Logcat.d("ContextExt", "Resource no found resId is $resId")
        return 0
    }
}

fun Context.getColor(resId: Int): Int {
    return ContextCompat.getColor(this, resId)
}

fun Context.dp2px(dp: Float): Float {
    return (dp * resources.displayMetrics.density + 0.5f)
}

fun Context.dp2px(dp: Int): Int {
    return dp2px(dp.toFloat()).toInt()
}

fun Context.getDimension(resId: Int): Float {
    val value = parseAttribute(resId)
    if (value == 0) {
        Logcat.d("ContextExt", "resource get fail, resId is $resId")
        return 0.0f
    }
    try {
        return resources.getDimension(resId)
    } catch (e: Throwable) {
        Logcat.d("ContextExt", "Resource no found resId is $resId")
        return 0.0f
    }
}

val Context?.fontScale: Float
    get() = this?.resources?.configuration?.fontScale ?: 1.0f