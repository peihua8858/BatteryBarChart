package com.android.hwsystemmanager.multicolor

import android.graphics.Rect
import android.os.Bundle
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.customview.widget.ExploreByTouchHelper
import com.android.hwsystemmanager.utils.Logcat

class BarChartTouchHelper(private val hostView: MultiColorLineChart) : ExploreByTouchHelper(hostView) {
    public override fun getVirtualViewAt(f10: Float, f11: Float): Int {
        for (batteryStackBarData in hostView.stackBarDataList) {
            val f12 = batteryStackBarData.x
            if (f12 <= f10 && f12 + batteryStackBarData.width >= f10) {
                return batteryStackBarData.index
            }
        }
        return -1
    }

    public override fun getVisibleVirtualViews(list: MutableList<Int>?) {
        list?.add(0)
        val chartView = this.hostView
        val lastIndex = chartView.lastIndex
        if (lastIndex == 0) {
            return
        }
        for (i4 in 1..<lastIndex) {
            if (chartView.mIsHalfHour == 1) {
                if (i4 % 2 != 0 && list != null) {
                    list.add(i4)
                }
            } else if (i4 % 2 == 0 && list != null) {
                list.add(i4)
            }
        }
        list?.add(lastIndex)
        Logcat.d("BarChartTouchHelper", "virtualViewIds : $list")
    }

    public override fun onPerformActionForVirtualView(i4: Int, i8: Int, bundle: Bundle?): Boolean {
        return false
    }

    public override fun onPopulateNodeForVirtualView(i4: Int, node: AccessibilityNodeInfoCompat) {
        if (i4 >= 0) {
            val chartView = this.hostView
            if (i4 <= chartView.stackBarDataList.size) {
                node.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK)
                val batteryStackBarData = chartView.stackBarDataList[i4]
                chartView.mSelectIndex = i4 / 2
                if (chartView.startIndex < chartView.stackBarDataList.size && chartView.endIndex < chartView.stackBarDataList.size) {
                    val f10 = chartView.stackBarDataList[chartView.startIndex].x
                    val f11 =
                        chartView.stackBarDataList[chartView.endIndex].x + batteryStackBarData.width
                    val f12 = batteryStackBarData.height
                    val f13 = batteryStackBarData.y
                    node.contentDescription = chartView.clickPointDescription
                    node.setBoundsInParent(
                        Rect(
                            f10.toInt(),
                            f13.toInt(),
                            f11.toInt(),
                            (f12 + f13).toInt()
                        )
                    )
                    return
                }
                node.contentDescription = ""
                node.setBoundsInParent(Rect(0, 0, 0, 0))
                return
            }
        }
        Logcat.d("BarChartTouchHelper", "On populate node for virtual view but out of index.")
        node.contentDescription = ""
        node.setBoundsInParent(Rect(0, 0, 0, 0))
    }
}