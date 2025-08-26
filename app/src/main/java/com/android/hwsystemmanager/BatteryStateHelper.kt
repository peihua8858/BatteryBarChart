package com.android.hwsystemmanager

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.text.format.DateUtils
import com.android.hwsystemmanager.utils.Logcat
import com.android.hwsystemmanager.utils.TimeUtil
import com.android.hwsystemmanager.utils.dLog
import com.google.gson.Gson
import java.util.Calendar
import kotlin.math.round
import kotlin.math.roundToInt

object BatteryStateHelper {
    val f9979m: Uri =
        Uri.parse("content://com.huawei.android.smartpowerprovider/batterystatisticslevel")


    fun m928a() {
        val contentResolver = MainApplication.context.contentResolver
        val strArr = arrayOf((System.currentTimeMillis() - 86400000).toString())
        contentResolver.delete(f9979m, "date < ?", strArr)
        Logcat.d("BatteryStateHelper", "deleteBatteryInfoOneDayAgo whereTimes = $strArr")
    }


    fun m929b(levelAndCharge: LevelAndCharge) {
        val j10 = levelAndCharge.time
        val m935e = BatteryStatisticsHelper.m935e(j10) + (j10 - 1800000)
        val sb2 = StringBuilder("batteryLevel = ")
        val i4 = levelAndCharge.level
        sb2.append(i4)
        Logcat.d("BatteryStateHelper", sb2.toString())
        if (i4 < 0) {
            return
        }
        val context = MainApplication.context
        val contentResolver = context.contentResolver
        val sb3 = StringBuilder("uap.getUid() = ")
        val str = levelAndCharge.charge
        sb3.append(str)
        sb3.append(", uap.getPower() = ")
        sb3.append(i4)
        Logcat.d("BatteryStateHelper", sb3.toString())
        val contentValues = ContentValues()
        contentValues.put("date", m935e)
        contentValues.put("charge", str)
        contentValues.put("level", i4)
        contentResolver.bulkInsert(f9979m, arrayOf(contentValues))
        //        SharedPreferenceWrapper.m10724c(context, "power_settings", "last_top_app_consume_update_time", j10);
    }

    fun fakeData(startTime: Long, j11: Long): ArrayList<LevelAndCharge> {
        val arrayList = ArrayList<LevelAndCharge>()
        var time =startTime
        for (i in 0..23) {
            val sTime = time  + 1800000
            val nextTime = sTime + 1800000L
            time=nextTime
            dLog { ">>>sTime:${TimeUtil.formatTime(sTime)}," +
                    "nextTime:${TimeUtil.formatTime(nextTime)}" }
            if (i <= 5) {
                arrayList.add(LevelAndCharge((80 + i / 2f).roundToInt(), "true", sTime))
                arrayList.add(LevelAndCharge((80 + (i+1) / 2f).roundToInt(), "true", nextTime))
            } else if (i <= 10) {
                arrayList.add(LevelAndCharge((90 - i / 2f - 30).roundToInt(), "false", sTime))
                arrayList.add(LevelAndCharge((90 - (i+1) / 2f - 30).roundToInt(), "false", nextTime))
            } else if (i <= 15) {
                arrayList.add(LevelAndCharge((70 - i / 2f - 30).roundToInt(), "false", sTime))
                arrayList.add(LevelAndCharge((70 - (i+1) / 2f - 30).roundToInt(), "false", nextTime))
            } else {
                arrayList.add(LevelAndCharge((20 + i / 2f).roundToInt(), "true", sTime))
                arrayList.add(LevelAndCharge((20 + (i+1) / 2f).roundToInt(), "true", nextTime))
            }
        }
        dLog { ">>>>result:"+ Gson().toJson(arrayList) }
        return arrayList
    }
    fun formatTime(time: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        val formatDateTime =
            DateUtils.formatDateTime(MainApplication.context, calendar.timeInMillis,  DateUtils.FORMAT_SHOW_TIME)
        dLog { ">>>time:$time,startTimeString =$formatDateTime" }
        return formatDateTime
    }

    @SuppressLint("SimpleDateFormat")
    fun m930c(context: Context?, j10: Long, j11: Long): ArrayList<LevelAndCharge> {
        var j12: Long
        var i4: Int
        var j13: Long
        var i8: Int
        var str: String
        val arrayList = ArrayList<LevelAndCharge>()
        if (context == null) {
            return arrayList
        }
        var j14 = j10 + j11
        var i9 = 0
        var i10 = 1
        val cursor = context.contentResolver.query(
            f9979m,
            arrayOf("date", "level", "charge"),
            "date > ? and date <= ?",
            arrayOf(j10.toString(), j14.toString()),
            "date asc"
        ) ?: return arrayList
        cursor.use {
            var i11 = -1
            var i12 = -1
            var i13 = -1
            while (cursor.moveToNext()) {
                val j15 = cursor.getLong(i9)
                val i14 = ((j14 - j15) / 1800000).toInt()
                val i15 = cursor.getInt(i10)
                if (i15 >= 0 && i12 != i14) {
                    i8 = i12 - i14
                    if (i12 != i11 && i8 > i10) {
                        j13 = j15
                        val d10 = ((i15 - i13) / i8).toDouble()
                        val i16 = i8 - 1
                        var i17 = i16
                        while (i17 > 0) {
                            val j16 = j13 - (i17 * 1800000)
                            str = if (i16 == 1 && d10 > 0.0) {
                                "true"
                            } else {
                                "false"
                            }
                            Logcat.d(
                                "BatteryStateHelper",
                                "The battery history bar $j16 is empty, isCharging: $str."
                            )
                            arrayList.add(
                                LevelAndCharge(
                                    ((i15.toDouble()) - ((i17.toDouble()) * d10)).toInt(),
                                    str,
                                    j16
                                )
                            )
                            i17 += -1
                        }
                        j12 = j14
                    } else {
                        j12 = j14
                        j13 = j15
                    }
                    i4 = 1
                    val i19 = cursor.getInt(1)
                    val string = cursor.getString(2)
                    arrayList.add(LevelAndCharge(i19, string, j13))
                    if (arrayList.size >= 48) {
                        break
                    }
                    i13 = i15
                    i12 = i14
                } else {
                    j12 = j14
                    i4 = i10
                }
                i10 = i4
                j14 = j12
                i11 = -1
                i9 = 0
            }
        }
        return arrayList
    }
}
