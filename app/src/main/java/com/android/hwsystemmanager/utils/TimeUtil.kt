package com.android.hwsystemmanager.utils

import android.text.BidiFormatter
import android.text.format.DateUtils
import com.android.hwsystemmanager.MainApplication
import com.android.hwsystemmanager.MainApplication.Companion.context
import com.android.hwsystemmanager.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object TimeUtil {
    const val HALF_HOUR: Long = 1800000L
    const val HOUR_1: Long = 3600000L
    const val HOUR_24: Long = 24 * HOUR_1
    const val HOUR_23: Long = 23 * HOUR_1
    val DATE_FORMAT: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
    val DATE_FORMAT1: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT)
    val DATE_HOUR_FORMAT: SimpleDateFormat = SimpleDateFormat("HH", Locale.ROOT)

    fun formatTime(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        val formatDateTime =
            DateUtils.formatDateTime(
                context,
                timestamp,
                DateUtils.FORMAT_SHOW_TIME
            )
        Logcat.d("timestamp:$timestamp,startTimeString =$formatDateTime")
        return formatDateTime
    }

    /**
     * 获取指定时间戳的凌晨0点
     *
     * @param timestamp 时间戳
     * @return 凌晨0点的时间戳
     */
    fun getMidnightTimestamp(timestamp: Long): Long {
        val instance = Calendar.getInstance()
        instance.timeInMillis = timestamp
        instance[Calendar.HOUR_OF_DAY] = 0
        instance[Calendar.MINUTE] = 0
        instance[Calendar.SECOND] = 0
        instance[Calendar.MILLISECOND] = 0
        return instance.timeInMillis
    }

    val midnightTimestamp: Long
        /**
         * 获取当前时间戳的凌晨0点
         *
         * @return 凌晨0点的时间戳
         */
        get() {
            val instance = Calendar.getInstance()
            instance[Calendar.HOUR_OF_DAY] = 0
            instance[Calendar.MINUTE] = 0
            instance[Calendar.SECOND] = 0
            instance[Calendar.MILLISECOND] = 0
            return instance.timeInMillis
        }

    val isEarlyMorning: Boolean
        get() = (Calendar.getInstance()[Calendar.HOUR_OF_DAY] + 1) % 24 < 8

    fun formatBatteryChooseTime(state: Int, time: Long): String {
        var time = time
        var adjustedTime = 3600000 + time
        val instance = Calendar.getInstance()
        if (state == -1) {
            adjustedTime -= 1800000
        } else if (state == 1) {
            time += 1800000
        }
        instance.timeInMillis = time
        val formatDateTime = DateUtils.formatDateTime(context, instance.timeInMillis, DateUtils.FORMAT_SHOW_TIME)
        instance.timeInMillis = adjustedTime
        val formatDateTime2 = DateUtils.formatDateTime(context, instance.timeInMillis, DateUtils.FORMAT_SHOW_TIME)
        val format = context.getString(R.string.power_battery_choose_time)
        val content = String.format(format, formatDateTime, formatDateTime2)
        return BidiFormatter.getInstance().unicodeWrap(content)
    }

    fun formatDateTime(time: Long): String {
        return DATE_FORMAT1.format(Date(time))
    }

    fun formatHourTime(time: Long): String {
        return DATE_HOUR_FORMAT.format(Date(time))
    }

    fun getDateTimeMillis(time: Long, hourOfDay: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        calendar[Calendar.MILLISECOND] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.HOUR_OF_DAY] = hourOfDay
        return calendar.timeInMillis
    }
    fun getDateTimeMillis(time: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        calendar[Calendar.MILLISECOND] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MINUTE] = 0
        return calendar.timeInMillis
    }
    fun getDateMinuteTimeMillis(time: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        calendar[Calendar.MILLISECOND] = 0
        calendar[Calendar.SECOND] = 0
//        calendar[Calendar.MINUTE] = 0
        return calendar.timeInMillis
    }
}
