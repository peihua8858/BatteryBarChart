package com.android.hwsystemmanager.utils

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import com.android.settings.util.getColor

fun Paint.measureTextSize(str: String): Pair<Int, Int> {
    val rect = Rect()
    getTextBounds(str, 0, str.length, rect)
    Logcat.d("TextPainExt", "width is ${rect.width()}, height is ${rect.height()}")
    return Pair(rect.width(), rect.height())
}

fun Context.createPaint(textSize: Int): TextPaint {
    val textPaint = TextPaint(1)
    textPaint.color = getColor(android.R.attr.textColorSecondary, false)
    textPaint.isAntiAlias = true
    textPaint.textSize = textSize.toFloat()
    return textPaint
}