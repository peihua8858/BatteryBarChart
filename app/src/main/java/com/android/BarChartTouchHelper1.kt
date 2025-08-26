package com.android

import android.graphics.Rect
import android.os.Bundle
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.customview.widget.ExploreByTouchHelper
import com.android.settings.base.helper.Logcat
import com.android.settings.battery.view.BatteryBarChart1

class BarChartTouchHelper1(private val hostView: BatteryBarChart1) : ExploreByTouchHelper(hostView) {
    public override fun getVirtualViewAt(f10: Float, f11: Float): Int {
        for (batteryStackBarData in hostView.mBarLists) {
            val f12 = batteryStackBarData.f18269a
            if (f12 <= f10 && f12 + batteryStackBarData.f18271c >= f10) {
                return batteryStackBarData.f18274f
            }
        }
        return -1
    }

    public override fun getVisibleVirtualViews(list: MutableList<Int>?) {
        list?.add(0)
        val batteryBarChart = this.hostView
        val lastIndex = batteryBarChart.lastIndex
        if (lastIndex == 0) {
            return
        }
        for (i4 in 1..<lastIndex) {
            if (batteryBarChart.mIsHalfHour == 1) {
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
            val batteryBarChart = this.hostView
            if (i4 <= batteryBarChart.mBarLists.size) {
                node.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK)
                val batteryStackBarData = batteryBarChart.mBarLists[i4]
                batteryBarChart.mSelectIndex = i4 / 2
                if (batteryBarChart.startIndex < batteryBarChart.mBarLists.size && batteryBarChart.endIndex < batteryBarChart.mBarLists.size) {
                    val f10 = batteryBarChart.mBarLists[batteryBarChart.startIndex].f18269a
                    val f11 =
                        batteryBarChart.mBarLists[batteryBarChart.endIndex].f18269a + batteryStackBarData.f18271c
                    val f12 = batteryStackBarData.f18272d
                    val f13 = batteryStackBarData.f18270b
                    node.contentDescription = batteryBarChart.clickPointDescription
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
