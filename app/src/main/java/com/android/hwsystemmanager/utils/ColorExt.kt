package com.android.hwsystemmanager.utils

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red

@ColorInt
fun Int.argb(alpha:Float): Int {
    val a = (alpha * 255.0f + 0.5f).toInt()
    return Color.argb(a, this.red, this.green, this.blue)
}