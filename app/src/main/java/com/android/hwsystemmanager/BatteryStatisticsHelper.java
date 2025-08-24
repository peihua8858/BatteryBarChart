package com.android.hwsystemmanager;

import android.content.Context;
import android.text.format.DateUtils;

import com.android.hwsystemmanager.utils.Logcat;
import com.android.hwsystemmanager.utils.TextExtKt;

import java.util.Calendar;


public final class BatteryStatisticsHelper {

//    public static  void m931a(PowerUsageState powerUsageState, long j10, List list) {
//        if (powerUsageState.getBgPower() + powerUsageState.getFgPower() > 0.0d) {
//            ContentValues contentValues = new ContentValues();
//            contentValues.put("date", Long.valueOf(j10));
//            contentValues.put("name", powerUsageState.getName());
//            contentValues.put("backpower", Long.valueOf(m933c(powerUsageState.getBgPower())));
//            contentValues.put("forepower", Long.valueOf(m933c(powerUsageState.getFgPower())));
//            contentValues.put("foretime", Long.valueOf(powerUsageState.getFgTime()));
//            contentValues.put("backtime", Long.valueOf(powerUsageState.getBgTime()));
//            list.add(contentValues);
//        }
//    }
//
//
//    public static  void m932b() {
//        ContentResolver contentResolver = HwTextPinyinUtil.f14221d.getContentResolver();
//        long currentTimeMillis = System.currentTimeMillis() - 86400000;
//        long m935e = m935e(currentTimeMillis) + (currentTimeMillis - 1800000);
//        contentResolver.delete(SmartProvider.f9978l, "date < ?", new String[]{String.valueOf(m935e)});
//        C0135d.m369i(C0172a.m378d("deleteBatteryInfoOneDayAgo  nowTime= ", currentTimeMillis, ", whereTimes = "), m935e, "BatteryStatisticsHelper");
//    }


    public static long m933c(long j10) {
        return Math.round(((float) j10) * 100.0f) / 100;
    }


    public static long m934d() {
        long currentTimeMillis = System.currentTimeMillis();
        Logcat.d("BatteryStatisticsHelper", "currentTimeMillis:" + currentTimeMillis);
        if (m936f(currentTimeMillis) >= 30) {
            return m935e(currentTimeMillis) + currentTimeMillis;
        }
        return (currentTimeMillis - 1800000) + m935e(currentTimeMillis);
    }


    public static long m935e(long j10) {
        Logcat.d("BatteryStatisticsHelper", "getHalfTime:" + j10);
        int m936f = m936f(j10);
        if (m936f != 0 && m936f != 30) {
            long j11 = (30 - (m936f % 30)) * 60000;
            Logcat.d("BatteryStatisticsHelper:", "getHalfTime changed: " + j11);
            return j11;
        }
        Logcat.d("BatteryStatisticsHelper:", "time is not changed: " + j10);
        return 1800000L;
    }


    public static int m936f(long j10) {
        Context context = MainApplication.getContext();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(j10);
        String formatDateTime = DateUtils.formatDateTime(context, calendar.getTimeInMillis(), 1);
        int m11850E0 = formatDateTime.indexOf(':') + 1;
        String substring = formatDateTime.substring(m11850E0, m11850E0 + 2);
        return TextExtKt.parseInt(substring,0);
    }


//    public static  void m937g(List<? extends PowerUsageState> list, long j10, long j11) {
//        int size = list.size();
//        C0134c.m359l("counts =", size, "BatteryStatisticsHelper");
//        if (size <= 0) {
//            return;
//        }
//        ArrayList arrayList = new ArrayList();
//        for (PowerUsageState powerUsageState : list) {
//            C0008b.m11g("BatteryStatisticsHelper", "insert data " + powerUsageState.getName() + " -- " + (powerUsageState.getBgPower() + powerUsageState.getFgPower()));
//            if (!ConsumeDetailModel.f22400f.contains(powerUsageState.getName()) && powerUsageState.getBgPower() + powerUsageState.getFgPower() <= 0) {
//                C0134c.m360m("insert data continue ", powerUsageState.getName(), "BatteryStatisticsHelper");
//            } else {
//                arrayList.add(powerUsageState);
//            }
//        }
//        if (arrayList.size() <= 0) {
//            C0008b.m11g("BatteryStatisticsHelper", "no items!!!");
//            return;
//        }
//        MutableCollectionsJVM.m262q0(arrayList, new C5911a(10));
//        Context context = HwTextPinyinUtil.f14221d;
//        C4130i.m10303c(context);
//        ContentResolver contentResolver = context.getContentResolver();
//        ArrayList arrayList2 = new ArrayList(arrayList.size());
//        long j12 = j10 + 60000;
//        try {
//            HwTextPinyinUtil.f14221d.getContentResolver().delete(SmartProvider.f9978l, "date > ? and date <= ?", new String[]{String.valueOf(j12 - j11), String.valueOf(j12)});
//        } catch (SQLiteException unused) {
//            C0008b.m9e("BatteryStatisticsHelper", "deleteHalfHourBattery SQLiteException.");
//        }
//        C0008b.m11g("BatteryStatisticsHelper", "fun saveValues tempList size = " + arrayList.size());
//        Iterator it = arrayList.iterator();
//        while (it.hasNext()) {
//            PowerUsageState powerUsageState2 = (PowerUsageState) it.next();
//            Iterator it2 = it;
//            C0008b.m11g("BatteryStatisticsHelper", "uap.getName = " + powerUsageState2.getName() + ",uap.getBgPower() = " + powerUsageState2.getBgPower() + ", uap.getFgPower() = " + powerUsageState2.getFgPower() + ",uap.getBgTime " + powerUsageState2.getBgTime() + ", uap.getFgTime " + powerUsageState2.getFgTime());
//            if (powerUsageState2.getFgPower() + powerUsageState2.getBgPower() > 0 || ConsumeDetailModel.f22400f.contains(powerUsageState2.getName())) {
//                ContentValues contentValues = new ContentValues();
//                contentValues.put("date", Long.valueOf(j10));
//                contentValues.put("name", powerUsageState2.getName());
//                contentValues.put("backpower", Long.valueOf(m933c(powerUsageState2.getBgPower())));
//                contentValues.put("forepower", Long.valueOf(m933c(powerUsageState2.getFgPower())));
//                contentValues.put("foretime", Long.valueOf(powerUsageState2.getFgTime()));
//                contentValues.put("backtime", Long.valueOf(powerUsageState2.getBgTime()));
//                arrayList2.add(contentValues);
//            }
//            it = it2;
//        }
//        if (arrayList2.size() != 0) {
//            contentResolver.bulkInsert(SmartProvider.f9978l, (ContentValues[]) arrayList2.toArray(new ContentValues[0]));
//        }
//        SharedPreferenceWrapper.m10724c(context, "power_settings", "last_top_app_consume_update_time", Long.valueOf(j10));
//    }
//
//
//    public static  ArrayList m938h(Context context, long j10, long j11) {
//        ArrayList arrayList = new ArrayList();
//        if (context == null) {
//            return arrayList;
//        }
//        try {
//            Cursor query = context.getContentResolver().query(SmartProvider.f9978l, new String[]{"name", "sum(foretime) sumtime", "sum(backtime) sumbgtime", "sum(forepower) sumpower", "sum(backpower) sumbgpower"}, "date > ? and date <= ? group by name", new String[]{String.valueOf(j10), String.valueOf(j10 + j11)}, "sumpower desc,sumbgpower desc");
//            if (query != null) {
//                Cursor cursor = query;
//                try {
//                    Cursor cursor2 = cursor;
//                    while (query.moveToNext()) {
//                        String string = query.getString(0);
//                        if (query.getLong(3) + query.getLong(4) > 0 || ConsumeDetailModel.f22400f.contains(string)) {
//                            arrayList.add(new PowerUsageState(string, query.getLong(1), query.getLong(2), query.getLong(3), query.getLong(4)));
//                            if (arrayList.size() >= 110) {
//                                break;
//                            }
//                        }
//                    }
//                    C6316m c6316m = C6316m.f23698a;
//                    NotifyPreference.m14206q(cursor, null);
//                } finally {
//                }
//            }
//            return arrayList;
//        } catch (SQLiteException unused) {
//            C0008b.m9e("BatteryStatisticsHelper", "queryBatteryStatistics function exception.");
//            return arrayList;
//        }
//    }
//
//
//    public static  void m939i(long j10) {
//        Context context = HwTextPinyinUtil.f14221d;
//        Intent intent = new Intent("com.huawei.hwsystemmanager.power.SCHEDULE_RECORD_POWER_CONSUME");
//        intent.setClass(context, ScheduleRecordPowerConsumeReceiver.class);
//        C6313j c6313j = C5011a.f19118a;
//        UserHandle userHandle = UserHandleEx.getUserHandle(0);
//        C4130i.m10305e(userHandle, "getUserHandle(...)");
//        PendingIntent broadcastAsUser = PendingIntentEx.getBroadcastAsUser(context, 0, intent, 134217728, userHandle);
//        Object systemService = context.getSystemService(NotificationCompat.CATEGORY_ALARM);
//        C4130i.m10304d(systemService, "null cannot be cast to non-null type android.app.AlarmManager");
//        ((AlarmManager) systemService).setWindow(3, SystemClock.elapsedRealtime() + j10, 60000L, broadcastAsUser);
//        C0008b.m11g("BatteryStatisticsHelper", "scheduleRecordPowerConsume,start alarms.");
//    }
}
