//package com.android.hwsystemmanager
//
//import android.graphics.Rect
//import android.os.Bundle
//import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
//import androidx.customview.widget.ExploreByTouchHelper
//import com.android.hwsystemmanager.utils.Logcat
//import com.android.hwsystemmanager.widgets.BatteryBarChart
//
//class BarChartTouchHelper2(private val hostView: BatteryBarChart) : ExploreByTouchHelper(hostView) {
//    public override fun getVirtualViewAt(f10: Float, f11: Float): Int {
//        for (batteryStackBarData in hostView.mBarLists) {
//            val f12 = batteryStackBarData.f19385a
//            if (f12 <= f10 && f12 + batteryStackBarData.f19387c >= f10) {
//                return batteryStackBarData.f19390f
//            }
//        }
//        return -1
//    }
//
//    public override fun getVisibleVirtualViews(list: MutableList<Int>?) {
//        list?.add(0)
//        val batteryBarChart = this.hostView
//        val lastIndex = batteryBarChart.lastIndex
//        if (lastIndex == 0) {
//            return
//        }
//        for (i4 in 1..<lastIndex) {
//            if (batteryBarChart.mIsHalfHour == 1) {
//                if (i4 % 2 != 0 && list != null) {
//                    list.add(i4)
//                }
//            } else if (i4 % 2 == 0 && list != null) {
//                list.add(i4)
//            }
//        }
//        list?.add(lastIndex)
//        Logcat.d("BarChartTouchHelper", "virtualViewIds : $list")
//    }
//
//    public override fun onPerformActionForVirtualView(i4: Int, i8: Int, bundle: Bundle?): Boolean {
//        return false
//    }
//
//    public override fun onPopulateNodeForVirtualView(i4: Int, node: AccessibilityNodeInfoCompat) {
//        if (i4 >= 0) {
//            val batteryBarChart = this.hostView
//            if (i4 <= batteryBarChart.mBarLists.size) {
//                node.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK)
//                val batteryStackBarData = batteryBarChart.mBarLists[i4]
//                batteryBarChart.mSelectIndex = i4 / 2
//                if (batteryBarChart.startIndex < batteryBarChart.mBarLists.size && batteryBarChart.endIndex < batteryBarChart.mBarLists.size) {
//                    val f10 = batteryBarChart.mBarLists[batteryBarChart.startIndex].f19385a
//                    val f11 =
//                        batteryBarChart.mBarLists[batteryBarChart.endIndex].f19385a + batteryStackBarData.f19387c
//                    val f12 = batteryStackBarData.f19388d
//                    val f13 = batteryStackBarData.f19386b
//                    node.contentDescription = batteryBarChart.clickPointDescription
//                    node.setBoundsInParent(
//                        Rect(
//                            f10.toInt(),
//                            f13.toInt(),
//                            f11.toInt(),
//                            (f12 + f13).toInt()
//                        )
//                    )
//                    return
//                }
//                node.contentDescription = ""
//                node.setBoundsInParent(Rect(0, 0, 0, 0))
//                return
//            }
//        }
//        Logcat.d("BarChartTouchHelper", "On populate node for virtual view but out of index.")
//        node.contentDescription = ""
//        node.setBoundsInParent(Rect(0, 0, 0, 0))
//    }
//}
