package com.android.hwsystemmanager.multicolor

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import com.android.hwsystemmanager.BatteryStatisticsHelper
import com.android.hwsystemmanager.utils.Logcat
import com.android.hwsystemmanager.utils.dLog
import kotlin.math.abs

class TouchListenerImpl(private val hostView: MultiColorLineChart) : View.OnTouchListener {
    private var state: Int = 0
    var isEnable: Boolean = true

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (!isEnable) {
            return false
        }
        val action = event.action
        var result = false
        val pointF = hostView.mSelectPointF
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                state = 0
                pointF.set(event.x, event.y)
            }

            MotionEvent.ACTION_MOVE -> {
                if (!isWithinBarRange(event.x)) {
                    return false
                }
                if (state == 1 || state == 2) {
                    handleTouchEvent(event.x)
                } else {
                    val yDiff = abs(event.y - pointF.y)
                    val xDiff = abs(event.x - pointF.x)
                    if (yDiff > xDiff) {
                        state = 2
                    } else if (abs(event.x - pointF.x) > 60f) {
                        state = 1
                        handleTouchEvent(event.x)
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                if (event.y < hostView.mVerticalGap) {
                    return false
                }
                if (isWithinBarRange(event.x)) {
                    val index = (getSelectedBarIndex(event.x) + hostView.mIsHalfHour) / 2
                    if (hostView.mSelectIndex == index) {
                        Logcat.d(MultiColorLineChart.TAG, "reset bar status.")
                        resetSelectionAndUpdateBars()
                        result = true
                    }
                }
                if (result) {
                    return true
                }
                if (isWithinBarRange(event.x)) {
                    val index = (getSelectedBarIndex(event.x) + hostView.mIsHalfHour) / 2
                    if (index != hostView.mSelectIndex) {
                        if (isWithinBarRange(event.x)) {
                            val selectedIndex =
                                (getSelectedBarIndex(event.x) + hostView.mIsHalfHour) / 2
                            hostView.setSelectIndex(selectedIndex)
                            updateBarStates()
                            hostView.invalidate()
                        }
                    }
                }
                if (!hostView.notSelected()) {
                    val timeSpan = hostView.selectedTimeSpand
                    hostView.onCallBack?.onCallback(hostView.selectedTime, timeSpan, false)
                }
                hostView.onSlideListener?.onSlide(true)
            }

            else -> {
                if (event.y < hostView.mVerticalGap) {
                    return false
                }
                if (!hostView.notSelected()) {
                    val timeSpan = hostView.selectedTimeSpand
                    hostView.onCallBack?.onCallback(hostView.selectedTime, timeSpan, false)
                }
                hostView.onSlideListener?.onSlide(true)
            }
        }
        return true
    }

    fun isWithinBarRange(x: Float): Boolean {
        val size = hostView.dataLength
        val adjustment = if (size > 1 && size % 2 != hostView.mIsHalfHour) 1 else 0
        return !(x >= (hostView.mBarWidth * (size + adjustment)) + hostView.mLeftVerticalLineWidth || x <= hostView.mLeftVerticalLineWidth)
    }

    fun handleTouchEvent(x: Float) {
        if ((getSelectedBarIndex(x) + hostView.mIsHalfHour) / 2 != hostView.mSelectIndex && isWithinBarRange(x)) {
            hostView.setSelectIndex((getSelectedBarIndex(x) + hostView.mIsHalfHour) / 2)
            updateBarStates()
            hostView.invalidate()
        }
        val listener = hostView.onSlideListener
        if (listener != null) {
            hostView.parent.requestDisallowInterceptTouchEvent(true)
            listener.onSlide(state == 2)
        }
    }

    private val mNumLists = hostView.batteryDataList
    private val mBarLists = hostView.stackBarDataList
    private fun getSelectedBarIndex(x: Float): Int {
        var index = ((x - hostView.mLeftVerticalLineWidth) / hostView.mBarWidth).toInt()
        Logcat.d(
            MultiColorLineChart.TAG, "getSelectedBarIndex $index,\n" +
                    "x:$x,hostView.mLeftVerticalLineWidth:${hostView.mLeftVerticalLineWidth}\n" +
                    ",hostView.mBarWidth:${hostView.mBarWidth},\n" +
                    "mNumLists.size:${mNumLists.size},mBarLists.size:${mBarLists.size},\n" +
                    "hostView.dataLength:${hostView.dataLength}"
        )
        if (index < 0 || ((mNumLists.isNotEmpty()) && index > mNumLists.size)) {
            index = 0
        }
        Logcat.d(MultiColorLineChart.TAG, "getSelectedBarIndex $index")
        return index
    }

    fun resetSelectionAndUpdateBars() {
        hostView.mSelectIndex = -1
        if (mBarLists.size <= 48) {
            for (i4 in 0..<mBarLists.size) {
                mBarLists[i4].selectState = 1
            }
        }
        hostView.onCallBack?.onCallback(BatteryStatisticsHelper.m934d(), 3600000L, true)
        hostView.invalidate()
    }

    fun updateBarStates() {
        for ((index,item) in mBarLists.withIndex() ) {
            dLog { "drawBarBubble>>>index = $index ,hostView.mIsHalfHour:${hostView.mIsHalfHour},hostView.mSelectIndex:${hostView.mSelectIndex}" }
            if (hostView.mSelectIndex == (hostView.mIsHalfHour + index) / 2) {
                item.selectState = 1
                dLog { "drawBarBubble>>>index = $index , hostView.startIndex:${ hostView.startIndex},hostView.endIndex:${hostView.endIndex}" }
                if (index == hostView.endIndex) {
                    if (hostView.mIsHalfHour == 1 && (index == 0 || index == 47)) {
                        item.state = -1
                    } else {
                        val prevIndex = index - 1
                        val prevBarData = mBarLists[prevIndex]
                        val prevLevel = prevBarData.levelAndCharge.level
                        val curLevel = item.levelAndCharge.level
                        Logcat.d(MultiColorLineChart.TAG, "$prevLevel $curLevel")
                        if (curLevel != 0 && prevLevel != 0) {
                            if (curLevel >= prevLevel) {
                                item.state = 2
                                prevBarData.state = 0
                            } else {
                                prevBarData.state = 1
                                item.state = 0
                            }
                        } else if (curLevel == 0) {
                            prevBarData.state = -1
                        } else {
                            item.state = -1
                        }
                    }
                }
                item.showBubble = true
                dLog { "drawBarBubble>>>index = $index , item.showBubble:${ item.showBubble},item.state:${item.state}" }
            } else {
                item.selectState = 0
                item.showBubble = false
            }
        }
    }

}