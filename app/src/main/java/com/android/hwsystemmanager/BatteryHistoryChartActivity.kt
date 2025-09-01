package com.android.hwsystemmanager

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import com.android.hwsystemmanager.databinding.BatteryHistoryChartBinding
import com.android.hwsystemmanager.multicolor.MultiColorLineChart
import com.android.hwsystemmanager.utils.BatterHistoryUtils


class BatteryHistoryChartActivity : ComponentActivity() {
    val binding by lazy { BatteryHistoryChartBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val result = BatteryStateHelper.fakeData()
        binding.apply {
            batteryStackBar.setListData(result)
            batteryStackChart.setData(result)
            batteryStackBar.slideListener=(BatterHistoryUtils.OnSlideListener { isDoNotIntercept -> batteryHistoryScrollView.setDoNotIntercept(isDoNotIntercept) })
            batteryHistoryUpperLayout.visibility = View.VISIBLE
//            val data: MutableList<MultiColorLineChart.DataPoint> = ArrayList()
//            data.add(MultiColorLineChart.DataPoint(0, 30f, Color.BLUE))
//            data.add(MultiColorLineChart.DataPoint(1, 45f, Color.BLUE))
//            data.add(MultiColorLineChart.DataPoint(2, 60f, Color.GREEN))
//            data.add(MultiColorLineChart.DataPoint(3, 75f, Color.GREEN))
//            data.add(MultiColorLineChart.DataPoint(4, 55f, Color.RED))
//            data.add(MultiColorLineChart.DataPoint(5, 40f, Color.RED))
//            data.add(MultiColorLineChart.DataPoint(6, 65f, Color.MAGENTA))
//            data.add(MultiColorLineChart.DataPoint(7, 85f, Color.MAGENTA))
            batteryLineChart.setData(result)
            batteryLineChart.setOnSlideListener(object : MultiColorLineChart.OnSlideListener {
                override fun onSlide(isDoNotIntercept: Boolean) {
                   batteryHistoryScrollView.setDoNotIntercept(isDoNotIntercept)
                }
            })
        }
    }
}