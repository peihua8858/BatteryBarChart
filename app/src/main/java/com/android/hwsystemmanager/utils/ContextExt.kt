package com.android.hwsystemmanager.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat

val Context.isLandscape: Boolean
    get() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

fun Context.parseAttribute(@AttrRes attrRes: Int): Int {
    val typedValue = TypedValue()
    val theme = theme
    theme.resolveAttribute(attrRes, typedValue, true)
    val resourceId = typedValue.resourceId
    if (resourceId != 0) {
        return resourceId
    }
    Logcat.d("ContextExt", "resource get fail, resId is $attrRes")
    return 0
}

fun Context.getColor(resId: Int, z10: Boolean): Int {
    val resourceId = parseAttribute(resId)
    if (resourceId == 0) {
        Logcat.d("ContextExt", "resource get fail, resId is $resId")
        return try {
            getColor(resId)
        } catch (e: Exception) {
            0
        }
    }
    val theme = if (z10) theme else null
    try {
        return resources.getColor(resourceId, theme)
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
    val resourceId = parseAttribute(resId)
    if (resourceId == 0) {
        Logcat.d("ContextExt", "resource get fail, resId is $resId")
        return resources.getDimension(resId)
    }
    try {
        return resources.getDimension(resourceId)
    } catch (e: Throwable) {
        Logcat.d("ContextExt", "Resource no found resId is $resId")
        return 0.0f
    }
}

val Context?.fontScale: Float
    get() = this?.resources?.configuration?.fontScale ?: 1.0f