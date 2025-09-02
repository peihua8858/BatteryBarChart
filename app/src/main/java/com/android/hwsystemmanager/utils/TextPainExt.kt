package com.android.hwsystemmanager.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import com.android.hwsystemmanager.R

fun TextPaint.measureTextSize(str: String): Rect {
    val rect = Rect()
    getTextBounds(str, 0, str.length, rect)
    Logcat.d("TextPainExt", "width is ${rect.width()}, height is ${rect.height()}")
    return rect
}

fun Paint.measureTextSize(str: String): Rect {
    val rect = Rect()
    getTextBounds(str, 0, str.length, rect)
    Logcat.d("TextPainExt", "width is ${rect.width()}, height is ${rect.height()}")
    return rect
}


fun Context.createPaint(textSize: Float, @ColorRes colorId: Int): TextPaint {
    val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    textPaint.color = parseColorAttribute(colorId, false)
    textPaint.isAntiAlias = true
    textPaint.textSize = textSize
    return textPaint
}

fun View.createPaint(textSize: Float, @ColorRes colorId: Int): TextPaint {
    return context.createPaint(textSize, colorId)
}

fun Context.createDashedPaint(
    strokeWidth: Float,
    @ColorRes colorId: Int,
    dashPathEffect: DashPathEffect = DashPathEffect(
        floatArrayOf(
            3.0f,
            3.0f
        ), 0.0f
    ),
): Paint {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.color = parseColorAttribute(colorId, false)
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = strokeWidth
    paint.setPathEffect(dashPathEffect)
    return paint
}

fun View.createDashedPaint(
    @ColorInt color: Int,
    strokeWidth: Float,
    dashPathEffect: DashPathEffect = DashPathEffect(floatArrayOf(3.0f, 3.0f), 0.0f),
): Paint {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.color = color
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = strokeWidth
    paint.setPathEffect(dashPathEffect)
    return paint
}

fun View.createDashedPaint(
    strokeWidth: Float, @ColorRes colorId: Int,
    dashPathEffect: DashPathEffect = DashPathEffect(floatArrayOf(3.0f, 3.0f), 0.0f),
): Paint {
    return context.createDashedPaint(strokeWidth, colorId, dashPathEffect)
}

fun View.createDashedPaint(strokeWidth: Float): Paint {
    return context.createDashedPaint(strokeWidth, R.color.stroke_y_line_color_card)
}

@SuppressLint("ResourceType")
fun Context.createTextPaint(textSize: Float, @ColorRes colorId: Int): TextPaint {
    val textPaint = TextPaint(TextPaint.ANTI_ALIAS_FLAG)
    textPaint.color = parseColorAttribute(colorId, false)
    textPaint.isAntiAlias = true
    textPaint.textSize = textSize
    return textPaint
}

fun Context.createTextPaint(@ColorInt color: Int, textSize: Float): TextPaint {
    val textPaint = TextPaint(TextPaint.ANTI_ALIAS_FLAG)
    textPaint.color = color
    textPaint.isAntiAlias = true
    textPaint.textSize = textSize
    return textPaint
}

fun View.createTextPaint(@ColorInt color: Int, textSize: Float): TextPaint {
    return context.createTextPaint(color, textSize)
}

fun View.createTextPaint(textSize: Float, @ColorRes colorId: Int): TextPaint {
    return context.createTextPaint(textSize, colorId)
}

fun Context.createTextPaint(textSize: Float): TextPaint {
    return createTextPaint(
        parseColorAttribute(android.R.attr.textColorSecondary, false)
        , textSize)
}

fun View.createTextPaint(textSize: Float): TextPaint {
    return context.createTextPaint(textSize)
}

