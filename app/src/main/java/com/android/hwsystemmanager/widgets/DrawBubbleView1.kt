package com.android.hwsystemmanager.widgets

import android.graphics.Canvas
import android.graphics.Path
import com.android.hwsystemmanager.BatteryStackBarData1
import com.android.hwsystemmanager.widgets.BatteryBarChart1.Companion.isDifferentFromNextCharge
import com.android.hwsystemmanager.widgets.BatteryBarChart1.Companion.isDifferentFromPreviousCharge
import com.android.hwsystemmanager.SelectedItem
import com.android.hwsystemmanager.utils.Logcat
import com.android.hwsystemmanager.utils.ScreenReaderUtils
import com.android.hwsystemmanager.utils.dp2px
import kotlin.math.max

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

            val isTrue = next.charge == "true"
            var cornerType = 4

            when {
                i == 0 -> {
                    if (barChart.notSelected()) {
                        if (barChart.f9902h <= 1) {
                            cornerType = 4
                        } else {
                            val current = mBarLists[i]
                            val nextItem = mBarLists[nextIndex]
                            cornerType = if (current.levelAndCharge.charge == nextItem.levelAndCharge.charge) {
                                1
                            } else {
                                4
                            }
                        }
                    } else {
                        if (barChart.f9902h <= 1 || barChart.mIsHalfHour == 1) {
                            cornerType = 4
                        } else {
                            val current = mBarLists[i]
                            val nextItem = mBarLists[nextIndex]
                            cornerType = if (current.levelAndCharge.charge == nextItem.levelAndCharge.charge) {
                                1
                            } else {
                                4
                            }
                        }
                    }
                }

                i == barChart.f9902h - 1 -> {
                    if (barChart.notSelected()) {
                        val current = mBarLists[i]
                        val prevItem = mBarLists[i - 1]
                        cornerType = if (current.levelAndCharge.charge == prevItem.levelAndCharge.charge) {
                            3
                        } else {
                            4
                        }
                    } else {
                        //>>Lb8
                        cornerType = if (barChart.startIndex == i && barChart.selectedTimeSpand == 1800000L) {
                            4
                        } else if (barChart.endIndex == i && barChart.selectedTimeSpand == 3600000L
                            && isDifferentFromPreviousCharge(i, mBarLists)
                        ) {
                            4
                        }else if(barChart.endIndex == i - 1 && barChart.f9902h - barChart.endIndex == 2){
                            4
                        }else{
                            3
                        }
                    }
                }

                i < mBarLists.size -> {
                    //L103>L10d
                    if (barChart.notSelected()) {
                        //未选中时
                        cornerType = if (i != mBarLists.size - 1) {
                            if (isDifferentFromPreviousCharge(i, mBarLists)
                                && isDifferentFromNextCharge(i, mBarLists)
                            ) {
                                4
                            } else if (isDifferentFromPreviousCharge(i, mBarLists)) {
                                1
                            } else if (isDifferentFromNextCharge(i, mBarLists)) {
                                3
                            } else {
                                2
                            }
                        } else {
                            3
                        }
                    } else {
                        //L13b
                        val startIndex = barChart.startIndex
                        val endIndex = barChart.endIndex
                        cornerType =
                            if (i == startIndex && isDifferentFromNextCharge(i, mBarLists)) {
                                4
                            } else if (endIndex == i && isDifferentFromPreviousCharge(
                                    i,
                                    mBarLists
                                )
                            ) {
                                4
                            } else if (endIndex + 1 == i || startIndex == i) {
                                1
                            } else if (barChart.f9902h - 1 == i || startIndex - 1 == i || endIndex == i) {
                                3
                            } else {
                                2
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
                    next.levelAndCharge.level,
                    path,
                    cornerType
                )
            }

            if (1 <= i && i < barChart.f9902h) {
                val prevItem = mBarLists[i - 1]
                next.a(canvas, isTrue, prevItem.levelAndCharge.level, path, cornerType)
            }

            i = nextIndex
        }

        // 修复：确保只绘制一个气泡，避免重复绘制
        var bubbleDrawn = false
        val iterator2 = mBarLists.iterator()
        var j = 0
        while (iterator2.hasNext()) {
            val next = iterator2.next()
            val nextJ = j + 1
            if (j < 0) {
                break
            }

            // 修复：添加条件判断，确保只在需要时绘制气泡
            if (!next.f18277i || barChart.notSelected() || bubbleDrawn) {
                j = nextJ
                continue
            }
            val barDataState = next.f18276h
//            val state = next.f18278j
            val lastIndex = mBarLists.size - 2
            var levelAndCharge = next.levelAndCharge

            if (j < lastIndex) {
                val nextItem = mBarLists[nextJ]
                if (levelAndCharge.level < nextItem.levelAndCharge.level) {
                    levelAndCharge = nextItem.levelAndCharge
                }
            }
            val y2 = next.startY
            val startX = next.startX
            val startY =
                (((barChart.chartHeight - barChart.f9908n) * ((100 - levelAndCharge.level).toFloat())) / (100f)) + y2
            Logcat.d("BubbleView", "BubbleView>>startX:$startX,startY:$startY,y2:$y2,barChart.chartHeight:${barChart.chartHeight},barChart.f9908n:${barChart.f9908n},levelAndCharge.level:${levelAndCharge.level}")
            val height = barChart.height
            var m10476a = 0f
            val bubbleStartX = when (barDataState) {
                -1 -> {
                    m10476a = barChart.dp2px(3.0f)
                    startX + barChart.f9893A / 2f
                }

                1 -> {
                    m10476a = barChart.dp2px(3.0f)
                    startX + barChart.f9893A + barChart.f9894B / 2f
                }

                2 -> {
                    m10476a = barChart.dp2px(3.0f)

                    startX - barChart.f9894B / 2f
                }

                else -> startX
            }
            Logcat.d("BubbleView", "barDataState:$barDataState,startX:$startX,barChart.f9893A:${barChart.f9893A},barChart.f9894B:${barChart.f9894B},startY:$startY,m10476a:$m10476a")
            // 向上移动气泡，避免箭头覆盖曲线
            val bubbleStartY = startY - m10476a
            Logcat.d("BubbleView", "bubbleStartX:$bubbleStartX,bubbleStartY:$bubbleStartY,startY:$startY,m10476a:$m10476a")
            val point = SelectedItem(
                bubbleStartX,
                bubbleStartY,
                barChart.selectedTime,
                barChart.f9910p.toFloat()
            ).apply {
                this.state = barChart.m7037d(barChart.startIndex, barChart.endIndex)
            }
            val bubbleView = BubbleView1(barChart.context, point)
            if (!ScreenReaderUtils.checkScreenReaderStatus()) {
                val pointY = bubbleView.startY
                val f27 = bubbleView.startX
                val f28 = bubbleView.f22165m.toFloat()//固定值7dp
                val f29 = f28 + f27
                val f30 = pointY - bubbleView.f22166n
                val f31 = f30 - 1.0f
                val f32 = bubbleView.f22157e//right
                val f33 = bubbleView.f22170r
                val f34 = f32 - f33
                val f35 = bubbleView.f22163k
                val f36 = bubbleView.f22155c
                val f37 = bubbleView.f22169q
                val i18 = bubbleView.f22168p
                val i19 = bubbleView.f22167o//边距固定值
                val f45 = 2f
                val f48 = bubbleView.f22162j//text width
                val i20 = bubbleView.f22173u
                val z10 = bubbleView.f22158f
                val f40 = bubbleView.f22156d
                var f14 = 0f
                val rectF = bubbleView.f22172t
                // 参考drawBubbleView方法中的边界处理逻辑，确保气泡不会超出视图边界
                 rectF .apply {
                    // 参考drawBubbleView方法中的边界处理逻辑
                     Logcat.d("BubbleView", "f32:$f32,f33:$f33,f29:$f29,f27:$f27,f34:$f34,i19:$i19,rectF：$this")
                    if (f29 > f34) {
                        // 气泡右侧超出边界
                        f14 = (f30 - (i18 / f45)) - (f35 / f45)
                        val left = f32 - f48 - (i19 * 2)
                        val top = f30 - i20
                        set(left, top, f32, f30)
                        Logcat.d("BubbleView", "f32:$f32,f48:$f48,i19:$i19,rectF：$this")
                        Logcat.d("BubbleView", "rectF：$this")
                    } else if (f27 - (f48 / f45) > f36) {
                        // 气泡在中间
                        val f49 = f48 / f45
                        val f20 = f27 - f49
                        val left = f20 - i19.toFloat()
                        val top = f30 - i20
                        val right = f49 + f27 + i19.toFloat()
                        set(max(0f,left), top, right, f30)
                        Logcat.d("BubbleView", "rectF：$this")
                    } else {
                        // 气泡左侧可能超出边界，确保不会超出
                        val left = f36 // 直接靠到左侧边界
                        val top = f30 - i20
                        val right = left + f48 + (i19 * 2)
                        set(left, top, right, f30)
                        Logcat.d("BubbleView", "rectF：$this")
                    }
                }
                Logcat.d("BubbleView", "rectF：$rectF")
                canvas.drawRoundRect(
                    rectF,
                    bubbleView.f22170r,
                    bubbleView.f22171s,
                    bubbleView.f22153a
                )

                // 绘制气泡下方的三角形箭头，调整方向向下
                // 参考drawBubbleView方法中的处理方式，保持三角形形状不变

                var f13 = 0f

                if (f29 > f34) {
                    f14 = (f30 - (i18 / 2f)) - (f35 / 2f)
                    f13 = f32
                } else {
                    f13 = f37 + f33 + f36
                    if (f29 >= f13) {
                        Logcat.d("BubbleView", "normal")
                        f13 = f29
                    }
                    f14 = f31
                }

                val f38 = f13
                var f39 = f14
                var f15 = 0f
                var f16 = 0f

                if (z10) {
                    val f41 = f27 - f28
                    f15 = f29
                    if (f41 < f33 + f36) {
                        f39 = (f30 - (i18 / 2f)) - (f35 / 2f)
                        f16 = f36
                    } else {
                        f16 = (f40 - f36) - i19
                        if (f41 <= f16) {
                            Logcat.d("BubbleView", "normal")
                            f16 = f41
                        }
                        f39 = f31
                    }
                } else {
                    f15 = f29
                    f16 = f38
                }

                var f42 = f27 - f28
                var f17 = 0f
                Logcat.d("BubbleView", "trianglePath>>moveTo：[$f27, $pointY],lineTo：[$f16, $f39],f28:$f28,f37:$f37,f33:$f33,f36:$f36,f42:$f42")
                if (f42 < f37 + f33 + f36) {
                    f42 = f37 + f33 + f36
                    f17 = (f30 - (i18 / 2f)) - (f35 / 2)
                } else {
                    val f43 = (f32 - f37) - f28
                    if (f42 > f43) {
                        f42 = f43
                    } else {
                        Logcat.d("BubbleView", "normal")
                    }
                    f17 = f31
                }

                var f18 = 0f
                var f21 = 0f

                if (z10) {
                    if (f15 > ((f40 - f36) - f37) - f33) {
                        f42 = f32 - f37
                        f18 = (f30 - (i18 / 2f)) - (f35 / 2)
                    } else {
                        val f44 = i19 + f36 + f37
                        if (f15 < f44) {
                            f21 = f44
                        } else {
                            Logcat.d("BubbleView", "normal")
                            f21 = f15
                        }
                        f42 = f21
                        f18 = f31
                    }
                } else {
                    f18 = f17
                }

                // 使用Path绘制气泡和三角形箭头，参考drawBubbleView方法
                val aa = f27 - (f16 - f27)
                val trianglePath = Path().apply {
                    moveTo(f27, pointY) // 顶点
                    lineTo(f16, f39) // 第一个点
                    lineTo(aa, f39) // 第二个点
                    close() // 闭合路径
                }
                Logcat.d("BubbleView", "trianglePath>>moveTo：[$f27, $pointY],lineTo：[$f16, $f39],lineTo：[$aa, $f18],bubbleView.f22171s:${bubbleView.f22171s},bubbleHeiht:${rectF.height()}")
                canvas.drawPath(trianglePath, bubbleView.f22153a)

                // 绘制文本
                val textPaint = bubbleView.f22154b
                val fm = textPaint.fontMetrics
                val textY = rectF.centerY() - fm.top / 2 - fm.bottom / 2

                // 文字随气泡一起移动
                val textX = rectF.left + bubbleView.f22167o.toFloat()
                if (bubbleView.f22158f) {
                    val scaleX = bubbleView.f22162j / 2 + rectF.left + bubbleView.f22167o.toFloat()
                    val scaleY = bubbleView.f22163k / 2f
                    canvas.scale(-1f, 1f, scaleX, scaleY)
                }
                Logcat.d("BubbleView", "text：${bubbleView.f22161i}")
                canvas.drawText(
                    bubbleView.f22161i,
                    textX,
                    textY,
                    textPaint
                )

                // 标记已绘制气泡
                bubbleDrawn = true
            }

            j = nextJ
        }
    }

    fun drawBubbleView(barChart: BatteryBarChart1, canvas: Canvas, i8: Int, i4: Int) {
        var i8 = i8
        var i4 = i4
        val mBarLists: List<BatteryStackBarData1> = barChart.mBarLists
        val it3 = mBarLists.iterator()
        var bubbleView: BubbleView

        var i15 = 0
        var bubbleStartX = 0f
        var m10476a = 0f
        var i9: Int
        var f11: Float
        var f12: Float
        var f13: Float
        var f14: Float
        var f15: Float
        var z10: Boolean
        var f16: Float
        var f17: Float
        var f18: Float
        var f19 = 0f
        var f20: Float
        var f21: Float
        while (it3.hasNext()) {
            val next2 = it3.next()
            val i16 = i15 + 1
            if (i15 >= 0) {
                if (next2.f18277i && !barChart.notSelected()) {
                    val barDataState = next2.f18276h
                    val size = mBarLists.size - i8
                    var levelAndCharge = next2.levelAndCharge
                    if (i15 < size) {
                        val levelAndCharge2 = mBarLists[i16].levelAndCharge
                        if (levelAndCharge.level < levelAndCharge2.level) {
                            levelAndCharge = levelAndCharge2
                        }
                    }
                    val startY = next2.startY
                    Logcat.d("BatteryBarChart", "it.y2 is $startY")
                    Logcat.d("BatteryBarChart", "it.level2 is " + next2.f18275g)
                    val f23 =
                        (((barChart.chartHeight - barChart.f9908n) * ((100 - levelAndCharge.level).toFloat())) / (100f)) + startY
                    Logcat.d(
                        "BatteryBarChart",
                        "drawBubbleView chart startY--" + startY + " , height--" + barChart.height + " , level--" + levelAndCharge.level
                    )
                    Logcat.d("BatteryBarChart", "barDataState is $barDataState")
                    Logcat.d("BatteryBarChart", "startY is $startY")
                    val startX = next2.startX
                    Logcat.d("BatteryBarChart", "startX is $startX")
                    if (barDataState != -1) {
                        if (barDataState != i4) {
                            if (barDataState == 2) {
                                bubbleStartX = startX - (barChart.f9894B / 2)
                                m10476a = barChart.dp2px(3.0f)
                            }
                        } else {
                            bubbleStartX = startX + barChart.f9893A + (barChart.f9894B / 2)
                            m10476a = barChart.dp2px(3.0f)
                        }
                    } else {
                        bubbleStartX = startX + (barChart.f9893A / 2)
                        m10476a = barChart.dp2px(3.0f)
                    }
                    val bubbleStartY = f23 - m10476a
                    Logcat.d(
                        "BatteryBarChart",
                        "bubbleStartX is $bubbleStartX    bubbleStartY is $bubbleStartY "
                    )
                    val selectedItem = SelectedItem(
                        bubbleStartX,
                        bubbleStartY,
                        barChart.selectedTime,
                        barChart.f9910p.toFloat()
                    )
                    selectedItem.state = barChart.m7037d(barChart.startIndex, barChart.endIndex)
                    val context = barChart.context
                    bubbleView = BubbleView(context, selectedItem)
                    if (!ScreenReaderUtils.checkScreenReaderStatus()) {
                        val path3 = Path()
                        val pointY = bubbleView.mStartY
                        Logcat.d("BubbleView", "pointY is $pointY")
                        val f27 = bubbleView.mStartX
                        path3.moveTo(f27, pointY)
                        val f28 = bubbleView.f22165m.toFloat()
                        val f29 = f28 + f27
                        val f30 = pointY - bubbleView.f22166n
                        val f31 = f30 - 1.0f
                        val f32 = bubbleView.f22157e
                        val f33 = bubbleView.f22170r
                        val f34 = f32 - f33
                        val f35 = bubbleView.f22163k
                        val f36 = bubbleView.f22155c
                        val f37 = bubbleView.f22169q
                        i9 = i16
                        val i18 = bubbleView.mRadius
                        f11 = f31
                        f12 = f32
                        if (f29 > f34) {
                            f14 = (f30 - (i18 / 2f)) - (f35 / 2f)
                            f13 = f12
                        } else {
                            f13 = f37 + f33 + f36
                            if (f29 >= f13) {
                                Logcat.d("BubbleView", "normal")
                                f13 = f29
                            }
                            f14 = f11
                        }
                        z10 = bubbleView.f22158f
                        val f38 = f13
                        val i19 = bubbleView.f22167o
                        var f39 = f14
                        val f40 = bubbleView.width
                        if (z10) {
                            val f41 = f27 - f28
                            f15 = f29
                            if (f41 < f33 + f36) {
                                f39 = (f30 - (i18 / 2f)) - (f35 / 2f)
                                f16 = f36
                            } else {
                                f16 = (f40 - f36) - i19
                                if (f41 <= f16) {
                                    Logcat.d("BubbleView", "normal")
                                    f16 = f41
                                }
                                f39 = f11
                            }
                        } else {
                            f15 = f29
                            f16 = f38
                        }
                        path3.lineTo(f16, f39)
                        var f42 = f27 - f28
                        if (f42 < f37 + f33 + f36) {
                            f42 = f37 + f33 + f36
                            f17 = (f30 - (i18 / 2f)) - (f35 / 2)
                        } else {
                            val f43 = (f12 - f37) - f28
                            if (f42 > f43) {
                                f42 = f43
                            } else {
                                Logcat.d("BubbleView", "normal")
                            }
                            f17 = f11
                        }
                        if (z10) {
                            if (f15 > ((f40 - f36) - f37) - f33) {
                                f42 = f12 - f37
                                f18 = (f30 - (i18 / 2f)) - (f35 / 2)
                            } else {
                                val f44 = i19 + f36 + f37
                                if (f15 < f44) {
                                    f21 = f44
                                } else {
                                    Logcat.d("BubbleView", "normal")
                                    f21 = f15
                                }
                                f42 = f21
                                f18 = f11
                            }
                        } else {
                            f18 = f17
                        }
                        path3.lineTo(f42, f18)
                        path3.close()
                        val paint = bubbleView.f22153a
                        canvas.drawPath(path3, paint)
                        val f45 = 2f
                        val f46 = bubbleView.f22164l / f45
                        val f47 = f46 + f27
                        val rectF = bubbleView.f22172t
                        val f48 = bubbleView.f22162j
                        val i20 = bubbleView.f22173u
                        if (f47 > f12) {
                            Logcat.d("BubbleView", "bubble right")
                            f19 = (f12 - f48) - (i19 * 2)
                            rectF[f19, f30 - i20, f12] = f30
                        } else if (f27 - f46 > f36) {
                            Logcat.d("BubbleView", "bubble middle")
                            val f49 = f48 / f45
                            f20 = f27 - f49
                            rectF[f20 - i19.toFloat(), f30 - i20, f49 + f27 + i19.toFloat()] =
                                f30
                            canvas.drawRoundRect(rectF, f33, bubbleView.f22171s, paint)
                            val paint2 = bubbleView.f22154b
                            val fontMetrics = paint2.fontMetrics
                            val centerY =
                                (rectF.centerY() - (fontMetrics.top / f45)) - (fontMetrics.bottom / f45)
                            if (z10) {
                                canvas.scale(-1.0f, 1.0f, (f48 / f45) + f20, f35 / f45)
                            }
                            canvas.drawText(bubbleView.mText, f20, centerY, paint2)
                            i15 = i9
                            i8 = 2
                            i4 = 1
                        } else {
                            Logcat.d("BubbleView", "bubble left")
                            f19 = f36 + f37
                            rectF[f19, f30 - i20, f48 + f19 + (i19 * 2)] = f30
                        }
                        f20 = i19 + f19
                        canvas.drawRoundRect(rectF, f33, bubbleView.f22171s, paint)
                        val paint22 = bubbleView.f22154b
                        val fontMetrics2 = paint22.fontMetrics
                        val centerY2 =
                            (rectF.centerY() - (fontMetrics2.top / f45)) - (fontMetrics2.bottom / f45)
                        if (z10) {
                        }
                        canvas.drawText(bubbleView.mText, f20, centerY2, paint22)
                        i15 = i9
                        i8 = 2
                        i4 = 1
                    }
                }
                i9 = i16
                i15 = i9
                i8 = 2
                i4 = 1
            }
        }
    }
}