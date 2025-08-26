package com.android.hwsystemmanager.widgets

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import com.android.hwsystemmanager.SelectedItem
import com.android.hwsystemmanager.utils.Logcat
import com.android.hwsystemmanager.utils.ScreenReaderUtils
import com.android.hwsystemmanager.utils.dp2px

object DrawBubbleView1 {
    fun onDraw(barChart: BatteryBarChart1, canvas: Canvas) {
        barChart.m7041j()
        val path = Path()
        val mBarLists = barChart.mBarLists
        val iterator = mBarLists.iterator()
        var i = 0

        while (iterator.hasNext()) {
            val next = iterator.next()
            val nextIndex = i + 1
            if (i < 0) {
                throw Exception("list index out of bounds")
            }
            if (i >= barChart.f9902h) {
                break
            }

            val isTrue = next.f18279k == "true"
            var cornerType = 4

            when {
                i == 0 -> {
                    if (barChart.m7039f()) {
                        if (barChart.f9902h > 1) {
                            val current = mBarLists[i]
                            val nextItem = mBarLists[nextIndex]
                            if (current.f18273e.charge == nextItem.f18273e.charge) {
                                cornerType = 1
                            } else {
                                cornerType = 4
                            }
                        } else {
                            cornerType = 1
                        }
                    } else {
                        if (barChart.f9902h > 1 && barChart.mIsHalfHour != 1) {
                            val current = mBarLists[i]
                            val nextItem = mBarLists[nextIndex]
                            if (current.f18273e.charge == nextItem.f18273e.charge) {
                                cornerType = 1
                            } else {
                                cornerType = 4
                            }
                        } else {
                            cornerType = 4
                        }
                    }
                }

                i == barChart.f9902h - 1 -> {
                    if (barChart.m7039f()) {
                        val current = mBarLists[i]
                        val prevItem = mBarLists[i - 1]
                        if (current.f18273e.charge == prevItem.f18273e.charge) {
                            cornerType = 3
                        } else {
                            cornerType = 1
                        }
                    } else {
                        val isStartIndex =
                            barChart.startIndex == i && barChart.selectedTimeSpand == 1800000L
                        val isEndIndex =
                            barChart.endIndex == i && barChart.selectedTimeSpand == 3600000L
                        val isLastItem = barChart.f9902h - barChart.endIndex == 2

                        cornerType =
                            if (isStartIndex || (isEndIndex && !BatteryBarChart1.m7032g(
                                    i,
                                    mBarLists
                                )) || (barChart.endIndex == i - 1 && isLastItem)
                            ) {
                                4
                            } else {
                                1
                            }
                    }
                }

                i < mBarLists.size && i >= 1 -> {
                    if (barChart.m7039f()) {
                        if (i != mBarLists.size - 1) {
                            if (BatteryBarChart1.m7032g(i, mBarLists) && BatteryBarChart1.m7033h(i, mBarLists)) {
                                cornerType = 4
                            } else if (BatteryBarChart1.m7032g(i, mBarLists)) {
                                cornerType = 1
                            } else if (BatteryBarChart1.m7033h(i, mBarLists)) {
                                cornerType = 3
                            }
                        }
                    } else {
                        val startIndex = barChart.startIndex
                        val endIndex = barChart.endIndex

                        cornerType = when {
                            i == startIndex && BatteryBarChart1.m7033h(i, mBarLists) -> 4
                            i == endIndex && BatteryBarChart1.m7032g(i, mBarLists) -> 4
                            i == endIndex + 1 || i == startIndex -> 1
                            i == barChart.f9902h - 1 || i == startIndex - 1 -> 1
                            i == endIndex -> 3
                            else -> 4
                        }
                    }
                }

                else -> cornerType = 1
            }

            if (cornerType == 1 || cornerType == 4) {
                path.reset()
            }

            if (i == 0 && barChart.f9902h > 0) {
                next.a(
                    canvas,
                    isTrue,
                    next.f18273e.level,
                    path,
                    cornerType
                )
            }

            if (1 <= i && i < barChart.f9902h) {
                val prevItem = mBarLists[i - 1]
                next.a(canvas, isTrue, prevItem.f18273e.level, path, cornerType)
            }

            i = nextIndex
        }

        val iterator2 = mBarLists.iterator()
        var j = 0
        var canvasVar = canvas
        while (iterator2.hasNext()) {
            val next = iterator2.next()
            val nextJ = j + 1
            if (j < 0) {
//                NotifyPreference.k0()
//                throw null
                return
            }

            if (!next.f18277i || barChart.m7039f()) {
                j = nextJ
                continue
            }

            val state = next.f18278j
            val lastIndex = mBarLists.size - 2
            var aVar = next.f18273e

            if (j < lastIndex) {
                val nextItem = mBarLists[nextJ]
                if (aVar.level < nextItem.f18273e.level) {
                    aVar = nextItem.f18273e
                }
            }

            val y2 = next.f18270b
            val level = next.f18275g
            val startY = (barChart.f9909o - barChart.f9908n) * (100 - aVar.level) / 100f + y2
            val height = barChart.height

            val bubbleStartX = when (state) {
                -1 -> next.f18269a + barChart.f9893A / 2f
                1 -> next.f18269a + barChart.f9893A + barChart.f9894B / 2f
                2 -> next.f18269a - barChart.f9894B / 2f
                else -> next.f18269a
            }

            val bubblePaintSize = barChart.dp2px(3.0f)
            val bubbleStartY = startY - bubblePaintSize

            val point = SelectedItem(
                bubbleStartX,
                bubbleStartY,
                barChart.selectedTime,
                barChart.f9910p.toFloat()
            ).apply {
                this.state = barChart.m7037d(barChart.startIndex, barChart.endIndex)
            }
            val bubbleView = BubbleView1(barChart.context, point)
            if (!ScreenReaderUtils.m10472c()) {
                val bubblePath = Path().apply {
                    moveTo(bubbleView.startX, bubbleView.startY)

                    val rightX = bubbleView.startX + bubbleView.f22165m.toFloat()
                    val topY = bubbleView.startY - bubbleView.f22166n.toFloat() - 1f

                    when {
                        rightX > bubbleView.f22157e - bubbleView.f22170r -> {
                            val bubbleTop = bubbleView.startY - bubbleView.f22168p / 2f - bubbleView.f22163k / 2f
                            lineTo(rightX, bubbleTop)
                        }
                        rightX < bubbleView.f22169q + bubbleView.f22170r + bubbleView.f22155c -> {
                            lineTo(rightX, topY)
                        }
                        else -> {
                            Logcat.d("BubbleView", "normal")
                            lineTo(rightX, rightX)
                        }
                    }

                    if (bubbleView.f22158f) {
                        val leftX = bubbleView.startX  - bubbleView.f22165m.toFloat()
                        if (leftX < bubbleView.f22169q + bubbleView.f22170r + bubbleView.f22155c) {
                            val bubbleTop = bubbleView.startY - bubbleView.f22168p / 2f - bubbleView.f22163k / 2f
                            lineTo(leftX, bubbleTop)
                        } else if (leftX > bubbleView.f22156d - bubbleView.f22155c - bubbleView.f22167o.toFloat()) {
                            lineTo(leftX, topY)
                        } else {
                            Logcat.d("BubbleView", "normal")
                            lineTo(leftX, leftX)
                        }
                    }

                    close()
                }
                Logcat.d("BubbleView", "bubblePath：${bubblePath.toString()}")
                canvasVar.drawPath(bubblePath, bubbleView.f22153a)

                val rectF = RectF().apply {
                    val centerX = bubbleView.startX  + bubbleView.f22164l / 2f
                    when {
                        centerX > bubbleView.f22157e -> {
                           Logcat.d("BubbleView", "bubble right")
                            val left = bubbleView.f22157e - bubbleView.f22162j - bubbleView.f22167o * 2f
                            val top = bubbleView.startY - bubbleView.f22173u.toFloat()
                            set(left, top, bubbleView.f22157e, point.startY)
                        }
                        centerX - bubbleView.f22164l / 2f > bubbleView.f22155c -> {
                            Logcat.d("BubbleView", "bubble middle")
                            val halfWidth = bubbleView.f22162j / 2f
                            val left = bubbleView.startX  - halfWidth - bubbleView.f22167o.toFloat()
                            val top = bubbleView.startY - bubbleView.f22173u.toFloat()
                            val right = bubbleView.startX  + halfWidth + bubbleView.f22167o.toFloat()
                            set(left, top, right, bubbleView.startY)
                        }
                        else -> {
                            Logcat.d("BubbleView", "bubble left")
                            val left = bubbleView.f22155c + bubbleView.f22169q
                            val top = bubbleView.startY - bubbleView.f22173u.toFloat()
                            val right = bubbleView.f22162j + left + bubbleView.f22167o * 2f
                            set(left, top, right, bubbleView.startY)
                        }
                    }
                }
                Logcat.d("BubbleView", "rectF：${rectF.toString()}")
                canvasVar.drawRoundRect(rectF, 1f, bubbleView.f22171s, bubbleView.f22153a)

                val textPaint = bubbleView.f22154b
                val fm = textPaint.fontMetrics
                val textY = rectF.centerY() - fm.top / 2 - fm.bottom / 2

                if (bubbleView.f22158f) {
                    val scaleX = bubbleView.f22162j / 2 + rectF.left + bubbleView.f22167o.toFloat()
                    val scaleY = bubbleView.f22163k / 2f
                    canvasVar.scale(-1f, 1f, scaleX, scaleY)
                }
                Logcat.d("BubbleView", "text：${bubbleView.f22161i}")
                canvasVar.drawText(bubbleView.f22161i, rectF.left + bubbleView.f22167o.toFloat(), textY, textPaint)
            }

            j = nextJ
            canvasVar = canvas
        }

    }
//    fun onDraw(barChart: BatteryBarChart, canvas: Canvas) {
//        barChart.m7041j()
//        val path = Path()
//        val mBarLists = barChart.mBarLists
//        val iterator = mBarLists.iterator()
//        var i = 0
//
//        while (iterator.hasNext()) {
//            val next = iterator.next()
//            val nextIndex = i + 1
//            if (i < 0) {
//                throw Exception("list index out of bounds")
//            }
//            if (i >= barChart.f9902h) {
//                break
//            }
//
//            val isTrue = next.f18279k == "true"
//            var cornerType = 4
//
//            when {
//                i == 0 -> {
//                    if (barChart.m7039f()) {
//                        if (barChart.f9902h > 1) {
//                            val current = mBarLists[i]
//                            val nextItem = mBarLists[nextIndex]
//                            if (current.f18273e.charge == nextItem.f18273e.charge) {
//                                cornerType = 1
//                            } else {
//                                cornerType = 4
//                            }
//                        } else {
//                            cornerType = 1
//                        }
//                    } else {
//                        if (barChart.f9902h > 1 && barChart.mIsHalfHour != 1) {
//                            val current = mBarLists[i]
//                            val nextItem = mBarLists[nextIndex]
//                            if (current.f18273e.charge == nextItem.f18273e.charge) {
//                                cornerType = 1
//                            } else {
//                                cornerType = 4
//                            }
//                        } else {
//                            cornerType = 4
//                        }
//                    }
//                }
//
//                i == barChart.f9902h - 1 -> {
//                    if (barChart.m7039f()) {
//                        val current = mBarLists[i]
//                        val prevItem = mBarLists[i - 1]
//                        if (current.f18273e.charge == prevItem.f18273e.charge) {
//                            cornerType = 3
//                        } else {
//                            cornerType = 1
//                        }
//                    } else {
//                        val isStartIndex =
//                            barChart.startIndex == i && barChart.selectedTimeSpand == 1800000L
//                        val isEndIndex =
//                            barChart.endIndex == i && barChart.selectedTimeSpand == 3600000L
//                        val isLastItem = barChart.f9902h - barChart.endIndex == 2
//
//                        cornerType =
//                            if (isStartIndex || (isEndIndex && !BatteryBarChart.m7032g(
//                                    i,
//                                    mBarLists
//                                )) || (barChart.endIndex == i - 1 && isLastItem)
//                            ) {
//                                4
//                            } else {
//                                1
//                            }
//                    }
//                }
//
//                i < mBarLists.size && i >= 1 -> {
//                    if (barChart.m7039f()) {
//                        if (i != mBarLists.size - 1) {
//                            if (BatteryBarChart.m7032g(i, mBarLists) && BatteryBarChart.m7033h(i, mBarLists)) {
//                                cornerType = 4
//                            } else if (BatteryBarChart.m7032g(i, mBarLists)) {
//                                cornerType = 1
//                            } else if (BatteryBarChart.m7033h(i, mBarLists)) {
//                                cornerType = 3
//                            }
//                        }
//                    } else {
//                        val startIndex = barChart.startIndex
//                        val endIndex = barChart.endIndex
//
//                        cornerType = when {
//                            i == startIndex && BatteryBarChart.m7033h(i, mBarLists) -> 4
//                            i == endIndex && BatteryBarChart.m7032g(i, mBarLists) -> 4
//                            i == endIndex + 1 || i == startIndex -> 1
//                            i == barChart.f9902h - 1 || i == startIndex - 1 -> 1
//                            i == endIndex -> 3
//                            else -> 4
//                        }
//                    }
//                }
//
//                else -> cornerType = 1
//            }
//
//            if (cornerType == 1 || cornerType == 4) {
//                path.reset()
//            }
//
//            if (i == 0 && barChart.f9902h > 0) {
//                next.a(
//                    canvas,
//                    isTrue,
//                    next.f18273e.level,
//                    path,
//                    cornerType
//                )
//            }
//
//            if (1 <= i && i < barChart.f9902h) {
//                val prevItem = mBarLists[i - 1]
//                next.a(canvas, isTrue, prevItem.f18273e.level, path, cornerType)
//            }
//
//            i = nextIndex
//        }
//
//        val iterator2 = mBarLists.iterator()
//        var j = 0
//        var canvasVar = canvas
//        while (iterator2.hasNext()) {
//            val next = iterator2.next()
//            val nextJ = j + 1
//            if (j < 0) {
////                NotifyPreference.k0()
////                throw null
//                return
//            }
//
//            if (!next.f18277i || barChart.m7039f()) {
//                j = nextJ
//                continue
//            }
//
//            val state = next.f18278j
//            val lastIndex = mBarLists.size - 2
//            var aVar = next.f18273e
//
//            if (j < lastIndex) {
//                val nextItem = mBarLists[nextJ]
//                if (aVar.level < nextItem.f18273e.level) {
//                    aVar = nextItem.f18273e
//                }
//            }
//
//            val y2 = next.f18270b
//            val level = next.f18275g
//            val startY = (barChart.f9909o - barChart.f9908n) * (100 - aVar.level) / 100f + y2
//            val height = barChart.height
//
//            val bubbleStartX = when (state) {
//                -1 -> next.f18269a + barChart.f9893A / 2f
//                1 -> next.f18269a + barChart.f9893A + barChart.f9894B / 2f
//                2 -> next.f18269a - barChart.f9894B / 2f
//                else -> next.f18269a
//            }
//
//            val bubblePaintSize = barChart.dp2px(3.0f)
//            val bubbleStartY = startY - bubblePaintSize
//
//            val point = SelectedItem(
//                bubbleStartX,
//                bubbleStartY,
//                barChart.selectedTime,
//                barChart.f9910p.toFloat()
//            ).apply {
//                this.state = barChart.m7037d(barChart.startIndex, barChart.endIndex)
//            }
//            val bubbleView = BubbleView(barChart.context, point)
//            if (!ScreenReaderUtils.m10472c()) {
//                val bubblePath = Path().apply {
//                    moveTo(bubbleView.startX, bubbleView.startY)
//
//                    val rightX = bubbleView.startX + bubbleView.f22165m.toFloat()
//                    val topY = bubbleView.startY - bubbleView.f22166n.toFloat() - 1f
//
//                    when {
//                        rightX > bubbleView.f22157e - bubbleView.f22170r -> {
//                            val bubbleTop = bubbleView.startY - bubbleView.f22168p / 2f - bubbleView.f22163k / 2f
//                            lineTo(rightX, bubbleTop)
//                        }
//                        rightX < bubbleView.f22169q + bubbleView.f22170r + bubbleView.f22155c -> {
//                            lineTo(rightX, topY)
//                        }
//                        else -> {
//                            Logcat.d("BubbleView", "normal")
//                            lineTo(rightX, rightX)
//                        }
//                    }
//
//                    if (bubbleView.f22158f) {
//                        val leftX = bubbleView.startX  - bubbleView.f22165m.toFloat()
//                        if (leftX < bubbleView.f22169q + bubbleView.f22170r + bubbleView.f22155c) {
//                            val bubbleTop = bubbleView.startY - bubbleView.f22168p / 2f - bubbleView.f22163k / 2f
//                            lineTo(leftX, bubbleTop)
//                        } else if (leftX > bubbleView.f22156d - bubbleView.f22155c - bubbleView.f22167o.toFloat()) {
//                            lineTo(leftX, topY)
//                        } else {
//                            Logcat.d("BubbleView", "normal")
//                            lineTo(leftX, leftX)
//                        }
//                    }
//
//                    close()
//                }
//                Logcat.d("BubbleView", "bubblePath：${bubblePath.toString()}")
//                canvasVar.drawPath(bubblePath, bubbleView.f22153a)
//
//                val rectF = RectF().apply {
//                    val centerX = bubbleView.startX  + bubbleView.f22164l / 2f
//                    when {
//                        centerX > bubbleView.f22157e -> {
//                            Logcat.d("BubbleView", "bubble right")
//                            val left = bubbleView.f22157e - bubbleView.f22162j - bubbleView.f22167o * 2f
//                            val top = bubbleView.startY - bubbleView.f22173u.toFloat()
//                            set(left, top, bubbleView.f22157e, point.startY)
//                        }
//                        centerX - bubbleView.f22164l / 2f > bubbleView.f22155c -> {
//                            Logcat.d("BubbleView", "bubble middle")
//                            val halfWidth = bubbleView.f22162j / 2f
//                            val left = bubbleView.startX  - halfWidth - bubbleView.f22167o.toFloat()
//                            val top = bubbleView.startY - bubbleView.f22173u.toFloat()
//                            val right = bubbleView.startX  + halfWidth + bubbleView.f22167o.toFloat()
//                            set(left, top, right, bubbleView.startY)
//                        }
//                        else -> {
//                            Logcat.d("BubbleView", "bubble left")
//                            val left = bubbleView.f22155c + bubbleView.f22169q
//                            val top = bubbleView.startY - bubbleView.f22173u.toFloat()
//                            val right = bubbleView.f22162j + left + bubbleView.f22167o * 2f
//                            set(left, top, right, bubbleView.startY)
//                        }
//                    }
//                }
//                Logcat.d("BubbleView", "rectF：${rectF.toString()}")
//                canvasVar.drawRoundRect(rectF, 1f, bubbleView.f22171s, bubbleView.f22153a)
//
//                val textPaint = bubbleView.f22154b
//                val fm = textPaint.fontMetrics
//                val textY = rectF.centerY() - fm.top / 2 - fm.bottom / 2
//
//                if (bubbleView.f22158f) {
//                    val scaleX = bubbleView.f22162j / 2 + rectF.left + bubbleView.f22167o.toFloat()
//                    val scaleY = bubbleView.f22163k / 2f
//                    canvasVar.scale(-1f, 1f, scaleX, scaleY)
//                }
//                Logcat.d("BubbleView", "text：${bubbleView.f22161i}")
//                canvasVar.drawText(bubbleView.f22161i, rectF.left + bubbleView.f22167o.toFloat(), textY, textPaint)
//            }
//
//            j = nextJ
//            canvasVar = canvas
//        }
//
//    }


//    fun drawBubbleView(barChart: BatteryBarChart, canvas: Canvas, i8: Int, i4: Int) {
//        var i8 = i8
//        var i4 = i4
//        val mBarLists: List<BatteryStackBarData> = barChart.mBarLists
//        val it3 = mBarLists.iterator()
//        var bubbleView: BubbleView
//
//        var i15 = 0
//        var bubbleStartX = 0f
//        var m10476a = 0f
//        var i9: Int
//        var f11: Float
//        var f12: Float
//        var f13: Float
//        var f14: Float
//        var f15: Float
//        var z10: Boolean
//        var f16: Float
//        var f17: Float
//        var f18: Float
//        var f19 = 0f
//        var f20: Float
//        var f21: Float
//        while (it3.hasNext()) {
//            val next2 = it3.next()
//            val i16 = i15 + 1
//            if (i15 >= 0) {
//                if (next2.f19393i && !barChart.m7039f()) {
//                    val barDataState = next2.f19392h
//                    val size = mBarLists.size - i8
//                    var levelAndCharge = next2.f19389e
//                    if (i15 < size) {
//                        val levelAndCharge2 = mBarLists[i16].f19389e
//                        if (levelAndCharge.level < levelAndCharge2.level) {
//                            levelAndCharge = levelAndCharge2
//                        }
//                    }
//                    val startY = next2.f19386b
//                    Logcat.d("BatteryBarChart", "it.y2 is $startY")
//                    Logcat.d("BatteryBarChart", "it.level2 is " + next2.f19391g)
//                    val f23 =
//                        (((barChart.f9909o - barChart.f9908n) * ((100 - levelAndCharge.level).toFloat())) / (100f)) + startY
//                    Logcat.d(
//                        "BatteryBarChart",
//                        "drawBubbleView chart startY--" + startY + " , height--" + barChart.height + " , level--" + levelAndCharge.level
//                    )
//                    Logcat.d("BatteryBarChart", "barDataState is $barDataState")
//                    Logcat.d("BatteryBarChart", "startY is $startY")
//                    val startX = next2.f19385a
//                    Logcat.d("BatteryBarChart", "startX is $startX")
//                    if (barDataState != -1) {
//                        if (barDataState != i4) {
//                            if (barDataState == 2) {
//                                bubbleStartX = startX - (barChart.f9894B / 2)
//                                m10476a = barChart.dp2px(3.0f)
//                            }
//                        } else {
//                            bubbleStartX = startX + barChart.f9893A + (barChart.f9894B / 2)
//                            m10476a = barChart.dp2px(3.0f)
//                        }
//                    } else {
//                        bubbleStartX = startX + (barChart.f9893A / 2)
//                        m10476a = barChart.dp2px(3.0f)
//                    }
//                    val bubbleStartY = f23 - m10476a
//                    Logcat.d(
//                        "BatteryBarChart",
//                        "bubbleStartX is $bubbleStartX    bubbleStartY is $bubbleStartY "
//                    )
//                    val selectedItem = SelectedItem(
//                        bubbleStartX,
//                        bubbleStartY,
//                        barChart.selectedTime,
//                        barChart.f9910p.toFloat()
//                    )
//                    selectedItem.state = barChart.m7037d(barChart.startIndex, barChart.endIndex)
//                    val context = barChart.context
//                    bubbleView = BubbleView(context, selectedItem)
//                    if (!ScreenReaderUtils.m10472c()) {
//                        val path3 = Path()
//                        val pointY = bubbleView.startY
//                        Logcat.d("BubbleView", "pointY is $pointY")
//                        val f27 = bubbleView.startX
//                        path3.moveTo(f27, pointY)
//                        val f28 = bubbleView.f22165m.toFloat()
//                        val f29 = f28 + f27
//                        val f30 = pointY - bubbleView.f22166n
//                        val f31 = f30 - 1.0f
//                        val f32 = bubbleView.f22157e
//                        val f33 = bubbleView.f22170r
//                        val f34 = f32 - f33
//                        val f35 = bubbleView.f22163k
//                        val f36 = bubbleView.f22155c
//                        val f37 = bubbleView.f22169q
//                        i9 = i16
//                        val i18 = bubbleView.f22168p
//                        f11 = f31
//                        f12 = f32
//                        if (f29 > f34) {
//                            f14 = (f30 - (i18 / 2f)) - (f35 / 2f)
//                            f13 = f12
//                        } else {
//                            f13 = f37 + f33 + f36
//                            if (f29 >= f13) {
//                                Logcat.d("BubbleView", "normal")
//                                f13 = f29
//                            }
//                            f14 = f11
//                        }
//                        z10 = bubbleView.f22158f
//                        val f38 = f13
//                        val i19 = bubbleView.f22167o
//                        var f39 = f14
//                        val f40 = bubbleView.f22156d
//                        if (z10) {
//                            val f41 = f27 - f28
//                            f15 = f29
//                            if (f41 < f33 + f36) {
//                                f39 = (f30 - (i18 / 2f)) - (f35 / 2f)
//                                f16 = f36
//                            } else {
//                                f16 = (f40 - f36) - i19
//                                if (f41 <= f16) {
//                                    Logcat.d("BubbleView", "normal")
//                                    f16 = f41
//                                }
//                                f39 = f11
//                            }
//                        } else {
//                            f15 = f29
//                            f16 = f38
//                        }
//                        path3.lineTo(f16, f39)
//                        var f42 = f27 - f28
//                        if (f42 < f37 + f33 + f36) {
//                            f42 = f37 + f36
//                            f17 = (f30 - (i18 / 2f)) - (f35 / 2)
//                        } else {
//                            val f43 = (f12 - f37) - f28
//                            if (f42 > f43) {
//                                f42 = f43
//                            } else {
//                                Logcat.d("BubbleView", "normal")
//                            }
//                            f17 = f11
//                        }
//                        if (z10) {
//                            if (f15 > ((f40 - f36) - f37) - f33) {
//                                f42 = f12 - f37
//                                f18 = (f30 - (i18 / 2f)) - (f35 / 2)
//                            } else {
//                                val f44 = i19 + f36 + f37
//                                if (f15 < f44) {
//                                    f21 = f44
//                                } else {
//                                    Logcat.d("BubbleView", "normal")
//                                    f21 = f15
//                                }
//                                f42 = f21
//                                f18 = f11
//                            }
//                        } else {
//                            f18 = f17
//                        }
//                        path3.lineTo(f42, f18)
//                        path3.close()
//                        val paint = bubbleView.f22153a
//                        canvas.drawPath(path3, paint)
//                        val f45 = 2f
//                        val f46 = bubbleView.f22164l / f45
//                        val f47 = f46 + f27
//                        val rectF = bubbleView.f22172t
//                        val f48 = bubbleView.f22162j
//                        val i20 = bubbleView.f22173u
//                        if (f47 > f12) {
//                            Logcat.d("BubbleView", "bubble right")
//                            f19 = (f12 - f48) - (i19 * 2)
//                            rectF[f19, f30 - i20, f12] = f30
//                        } else if (f27 - f46 > f36) {
//                            Logcat.d("BubbleView", "bubble middle")
//                            val f49 = f48 / f45
//                            f20 = f27 - f49
//                            rectF[f20 - i19.toFloat(), f30 - i20, f49 + f27 + i19.toFloat()] = f30
//                            canvas.drawRoundRect(rectF, f33, bubbleView.f22171s, paint)
//                            val paint2 = bubbleView.f22154b
//                            val fontMetrics = paint2.fontMetrics
//                            val centerY =
//                                (rectF.centerY() - (fontMetrics.top / f45)) - (fontMetrics.bottom / f45)
//                            if (z10) {
//                                canvas.scale(-1.0f, 1.0f, (f48 / f45) + f20, f35 / f45)
//                            }
//                            canvas.drawText(bubbleView.f22161i, f20, centerY, paint2)
//                            i15 = i9
//                            i8 = 2
//                            i4 = 1
//                        } else {
//                            Logcat.d("BubbleView", "bubble left")
//                            f19 = f36 + f37
//                            rectF[f19, f30 - i20, f48 + f19 + (i19 * 2)] = f30
//                        }
//                        f20 = i19 + f19
//                        canvas.drawRoundRect(rectF, f33, bubbleView.f22171s, paint)
//                        val paint22 = bubbleView.f22154b
//                        val fontMetrics2 = paint22.fontMetrics
//                        val centerY2 =
//                            (rectF.centerY() - (fontMetrics2.top / f45)) - (fontMetrics2.bottom / f45)
//                        if (z10) {
//                        }
//                        canvas.drawText(bubbleView.f22161i, f20, centerY2, paint22)
//                        i15 = i9
//                        i8 = 2
//                        i4 = 1
//                    }
//                }
//                i9 = i16
//                i15 = i9
//                i8 = 2
//                i4 = 1
//            }
//        }
//    }
}
