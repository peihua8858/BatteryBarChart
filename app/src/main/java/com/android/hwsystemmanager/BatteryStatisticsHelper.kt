//package com.android.settings.battery.utils
//
//import android.content.Context
//import android.text.format.DateUtils
//import com.android.settings.SettingsApp
//import com.android.settings.base.helper.Logcat
//import com.android.settings.util.parseInt
//import java.util.Calendar
//
//object BatteryStatisticsHelper {
////    fun a(powerUsageState: PowerUsageState, j10: Long, list: MutableList<*>) {
////        if ((powerUsageState.getBgPower() as Double) + (powerUsageState.getFgPower() as Double) > 0.0) {
////            val contentValues = ContentValues()
////            contentValues.put("date", j10)
////            contentValues.put("name", powerUsageState.getName())
////            contentValues.put("backpower", c(powerUsageState.getBgPower()))
////            contentValues.put("forepower", c(powerUsageState.getFgPower()))
////            contentValues.put("foretime", java.lang.Long.valueOf(powerUsageState.getFgTime()))
////            contentValues.put("backtime", java.lang.Long.valueOf(powerUsageState.getBgTime()))
////            list.add(contentValues)
////        }
////    }
////
////    fun b() {
////        val contentResolver: ContentResolver = SettingsApp.getContext().getContentResolver()
////        val currentTimeMillis = System.currentTimeMillis() - 86400000
////        val e10 = e(currentTimeMillis) + (currentTimeMillis - 1800000)
////        contentResolver.delete(SmartProvider.f2219l, "date < ?", arrayOf<String>(e10.toString()))
////        androidx.activity.result.d.i(
////            a.d(
////                "deleteBatteryInfoOneDayAgo  nowTime= ",
////                currentTimeMillis,
////                ", whereTimes = "
////            ), e10, "BatteryStatisticsHelper"
////        )
////    }
//
//    fun c(j10: Long): Long {
//        return (Math.round((j10.toFloat()) * 100.0f).toLong()) / (100L)
//    }
//
//    fun d(): Long {
//        val currentTimeMillis = System.currentTimeMillis()
//        if (f(currentTimeMillis) >= 30) {
//            return e(currentTimeMillis) + currentTimeMillis
//        }
//        return (currentTimeMillis - 1800000) + e(currentTimeMillis)
//    }
//
//    fun e(j10: Long): Long {
//        Logcat.d("BatteryStatisticsHelper", "getHalfTime:$j10")
//        val f10 = f(j10)
//        if (f10 != 0 && f10 != 30) {
//            val j11 = ((30 - (f10 % 30)).toLong()) * 60000
//            Logcat.d("BatteryStatisticsHelper:", "getHalfTime changed: $j11")
//            return j11
//        } else {
//            Logcat.d("BatteryStatisticsHelper:", "time is not changed: $j10")
//            return 1800000
//        }
//    }
//
//    fun f(j10: Long): Int {
//        val context: Context = SettingsApp.getContext()
//        val instance = Calendar.getInstance()
//        instance.timeInMillis = j10
//        val formatDateTime = DateUtils.formatDateTime(context, instance.timeInMillis, 1)
//        val E0 = formatDateTime.indexOf(':', 0) + 1
//        val substring = formatDateTime.substring(E0, E0 + 2)
//        return substring.parseInt(0)
//    }
//
////    //
////    fun g(list: List<PowerUsageState?>, j10: Long, j11: Long) {
////        val size = list.size
////        c.l("counts =", size, "BatteryStatisticsHelper")
////        if (size > 0) {
////            val arrayList: ArrayList<*> = ArrayList<Any?>()
////            for (powerUsageState in list) {
////                val name: String = powerUsageState.getName()
////                val bgPower: Long = powerUsageState.getBgPower() + powerUsageState.getFgPower()
////                a1.b.g("BatteryStatisticsHelper", "insert data $name -- $bgPower")
////                if (!uf.a.f10962f.contains(powerUsageState.getName())) {
////                    if (powerUsageState.getBgPower() + powerUsageState.getFgPower() <= 0) {
////                        c.m(
////                            "insert data continue ",
////                            powerUsageState.getName(),
////                            "BatteryStatisticsHelper"
////                        )
////                    }
////                }
////                arrayList.add(powerUsageState)
////            }
////            if (arrayList.size <= 0) {
////                a1.b.g("BatteryStatisticsHelper", "no items!!!")
////                return
////            }
////            j.q0(arrayList, a(10))
////            val context: Context = HwTextPinyinUtil.f5775d
////            i.c(context)
////            val contentResolver = context.contentResolver
////            val arrayList2: ArrayList<*> = ArrayList<Any?>(arrayList.size)
////            val j12 = j10 + 60000
////            val contentResolver2: ContentResolver = HwTextPinyinUtil.f5775d.getContentResolver()
////            val j13 = j12 - j11
////            try {
////                contentResolver2.delete(
////                    SmartProvider.f2219l,
////                    "date > ? and date <= ?",
////                    arrayOf<String>(j13.toString(), j12.toString())
////                )
////            } catch (unused: SQLiteException) {
////                a1.b.e("BatteryStatisticsHelper", "deleteHalfHourBattery SQLiteException.")
////            }
////            val size2 = arrayList.size
////            a1.b.g("BatteryStatisticsHelper", "fun saveValues tempList size = $size2")
////            var it: Iterator<*> = arrayList.iterator()
////            while (it.hasNext()) {
////                val powerUsageState2: PowerUsageState = it.next() as PowerUsageState
////                val name2: String = powerUsageState2.getName()
////                val bgPower2: Long = powerUsageState2.getBgPower()
////                val fgPower: Long = powerUsageState2.getFgPower()
////                val bgTime: Long = powerUsageState2.getBgTime()
////                val fgTime: Long = powerUsageState2.getFgTime()
////                val it2 = it
////                a1.b.g(
////                    "BatteryStatisticsHelper",
////                    "uap.getName = $name2,uap.getBgPower() = $bgPower2, uap.getFgPower() = $fgPower,uap.getBgTime $bgTime, uap.getFgTime $fgTime"
////                )
////                if (powerUsageState2.getFgPower() + powerUsageState2.getBgPower() > 0 || uf.a.f10962f.contains(
////                        powerUsageState2.getName()
////                    )
////                ) {
////                    val contentValues = ContentValues()
////                    contentValues.put("date", j10)
////                    contentValues.put("name", powerUsageState2.getName())
////                    contentValues.put("backpower", c(powerUsageState2.getBgPower()))
////                    contentValues.put("forepower", c(powerUsageState2.getFgPower()))
////                    contentValues.put(
////                        "foretime",
////                        java.lang.Long.valueOf(powerUsageState2.getFgTime())
////                    )
////                    contentValues.put(
////                        "backtime",
////                        java.lang.Long.valueOf(powerUsageState2.getBgTime())
////                    )
////                    arrayList2.add(contentValues)
////                }
////                it = it2
////            }
////            if (arrayList2.size != 0) {
////                contentResolver.bulkInsert(
////                    SmartProvider.f2219l,
////                    arrayList2.toTypedArray() as Array<ContentValues?>
////                )
////            }
////            mk.a.c(context, "power_settings", "last_top_app_consume_update_time", j10)
////        }
////    }
////
////    fun h(context: Context?, j10: Long, j11: Long): ArrayList<*> {
////        var th2: Throwable
////        val arrayList: ArrayList<*> = ArrayList<Any?>()
////        if (context == null) {
////            return arrayList
////        }
////        try {
////            val query = context.contentResolver.query(
////                SmartProvider.f2219l,
////                arrayOf<String>(
////                    "name",
////                    "sum(foretime) sumtime",
////                    "sum(backtime) sumbgtime",
////                    "sum(forepower) sumpower",
////                    "sum(backpower) sumbgpower"
////                ),
////                "date > ? and date <= ? group by name",
////                arrayOf<String>(j10.toString(), (j10 + j11).toString()),
////                "sumpower desc,sumbgpower desc"
////            )
////            if (query != null) {
////                val closeable: Closeable = query
////                try {
////                    val cursor = closeable as Cursor
////                    while (query.moveToNext()) {
////                        val string = query.getString(0)
////                        if (query.getLong(3) + query.getLong(4) > 0 || uf.a.f10962f.contains(string)) {
////                            arrayList.add(
////                                PowerUsageState(
////                                    string,
////                                    query.getLong(1),
////                                    query.getLong(2),
////                                    query.getLong(3),
////                                    query.getLong(4)
////                                )
////                            )
////                            if (arrayList.size >= 110) {
////                                break
////                            }
////                        }
////                    }
////                    val mVar: m = m.f11657a
////                    ve.d.q(closeable, null as Throwable?)
////                } catch (th3: Throwable) {
////                    val th4 = th3
////                    ve.d.q(closeable, th2)
////                    throw th4
////                }
////            }
////            return arrayList
////        } catch (unused: SQLiteException) {
////            a1.b.e("BatteryStatisticsHelper", "queryBatteryStatistics function exception.")
////            return arrayList
////        }
////    }
////
////    fun i(j10: Long) {
////        val context: Context = HwTextPinyinUtil.f5775d
////        val intent = Intent("com.huawei.hwsystemmanager.power.SCHEDULE_RECORD_POWER_CONSUME")
////        intent.setClass(context, ScheduleRecordPowerConsumeReceiver::class.java)
////        val jVar: j = rk.a.f8327a
////        val userHandle: UserHandle = UserHandleEx.getUserHandle(0)
////        i.e(userHandle, "getUserHandle(...)")
////        val broadcastAsUser: PendingIntent =
////            PendingIntentEx.getBroadcastAsUser(context, 0, intent, 134217728, userHandle)
////        val systemService = context.getSystemService(NotificationCompat.CATEGORY_ALARM)
////        i.d(systemService, "null cannot be cast to non-null type android.app.AlarmManager")
////        (systemService as AlarmManager).setWindow(
////            3,
////            SystemClock.elapsedRealtime() + j10,
////            60000,
////            broadcastAsUser
////        )
////        a1.b.g("BatteryStatisticsHelper", "scheduleRecordPowerConsume,start alarms.")
////    }
//}
