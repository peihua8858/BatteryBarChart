package com.android.hwsystemmanager.compose

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.android.hwsystemmanager.BatteryStateHelper
import com.android.hwsystemmanager.R
import com.android.hwsystemmanager.multicolor.MultiColorLineChart.Companion.HALF_HOUR
import com.android.hwsystemmanager.utils.Logcat
import com.android.hwsystemmanager.utils.ResultData
import com.android.hwsystemmanager.utils.TimeUtil
import com.android.hwsystemmanager.utils.request
import java.util.Calendar

class ChartViewModel(application: Application) : AndroidViewModel(application) {
    val timeState = mutableStateOf<ResultData<MutableList<StackBarPointData>>>(ResultData.Initialize())
    fun requestData() {
        request(timeState) {
            val result = mutableListOf<StackBarPointData>()
            val data = BatteryStateHelper.fakeData()
            for ((index, item) in data.withIndex()) {
                result.add(
                    StackBarPointData(
                        index, item.level, item.time, item.charge,
                        getTimeHour(item.time)
                    )
                )
            }
            result
        }
    }

    private fun getTimeHour(timeMillis: Long): String {
        Logcat.d(">>>timeMillis:${TimeUtil.formatDateTime(timeMillis)}")

        val todayNoonTimeMillis = TimeUtil.getDateTimeMillis(System.currentTimeMillis(), 12)
        Logcat.d("timeMillis>>>todayNoonTimeMillis:${TimeUtil.formatDateTime(todayNoonTimeMillis)}")

        val nowTimeMillis = TimeUtil.getDateTimeMillis(System.currentTimeMillis())
        Logcat.d("nowTimeMillis>>>nowTimeMillis:${TimeUtil.formatDateTime(nowTimeMillis)}")
        val nowAfterHalfHourTimeMillis = TimeUtil.getDateMinuteTimeMillis(System.currentTimeMillis() + HALF_HOUR)
        Logcat.d("nowAfterHalfHourTimeMillis>>>nowAfterHalfHourTimeMillis:${TimeUtil.formatDateTime(nowAfterHalfHourTimeMillis)}")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeMillis
        calendar[Calendar.MILLISECOND] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MINUTE] = 0
        val curTimeMillis = calendar.timeInMillis
        val application = getApplication<Application>()
        return when (curTimeMillis) {
            nowTimeMillis, nowAfterHalfHourTimeMillis -> application.getString(R.string.power_battery_now)
//                    earlyMorningTime -> "凌晨"
            todayNoonTimeMillis -> application.getString(R.string.power_battery_noon)
            else -> TimeUtil.formatHourTime(curTimeMillis)
        }
//            val earlyMorningTime = earlyMorningCalendar.timeInMillis

    }
}