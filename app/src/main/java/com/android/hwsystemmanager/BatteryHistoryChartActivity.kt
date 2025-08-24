package com.android.hwsystemmanager

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import com.android.hwsystemmanager.databinding.BatteryHistoryChartBinding
import com.android.hwsystemmanager.utils.dLog

class BatteryHistoryChartActivity : ComponentActivity() {
    val binding by lazy { BatteryHistoryChartBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val time = BatteryStatisticsHelper.m934d()
        dLog { "time:$time" }
        val result = BatteryStateHelper.fakeData((time + 60000) - 86400000, 86400000L)
        binding.apply {
            batteryStackBar.setListData(result)
            batteryStackChart.setData(result)
            batteryHistoryUpperLayout.visibility = View.VISIBLE
        }
    }
}