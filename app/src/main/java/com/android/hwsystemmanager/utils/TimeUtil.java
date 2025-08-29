package com.android.hwsystemmanager.utils;

import android.text.BidiFormatter;
import android.text.format.DateUtils;


import com.android.hwsystemmanager.MainApplication;
import com.android.hwsystemmanager.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class TimeUtil {

    public static final SimpleDateFormat f7989a = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
    public static final SimpleDateFormat DATE_FORMAT1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
    public static final SimpleDateFormat DATE_HOUR_FORMAT = new SimpleDateFormat("HH", Locale.ROOT);
    public static long a(long j10) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(j10);
        instance.set(11, 0);
        instance.set(12, 0);
        instance.set(13, 0);
        instance.set(14, 0);
        return instance.getTimeInMillis();
    }

    public static long b() {
        Calendar instance = Calendar.getInstance();
        instance.set(11, 0);
        instance.set(12, 0);
        instance.set(13, 0);
        instance.set(14, 0);
        return instance.getTimeInMillis();
    }

    public static boolean c() {
        if ((Calendar.getInstance().get(11) + 1) % 24 < 8) {
            return true;
        }
        return false;
    }

    public static String m11216d(int i4, long j10) {
        long j11 = 3600000 + j10;
        Calendar instance = Calendar.getInstance();
        if (i4 == -1) {
            j11 -= 1800000;
        } else if (i4 == 1) {
            j10 += 1800000;
        }
        instance.setTimeInMillis(j10);
        String formatDateTime = DateUtils.formatDateTime(MainApplication.getContext(), instance.getTimeInMillis(), 1);
        instance.setTimeInMillis(j11);
        String formatDateTime2 = DateUtils.formatDateTime(MainApplication.getContext(), instance.getTimeInMillis(), 1);
        String i8 = MainApplication.getContext().getString(R.string.power_battery_choose_time);
        String format = String.format(i8, formatDateTime, formatDateTime2);
        return BidiFormatter.getInstance().unicodeWrap(format);
    }
    public static String formatTime(long time){
        return DATE_FORMAT1.format(new Date(time));
    }

    public static String formatHourTime(long time){
        return DATE_HOUR_FORMAT.format(new Date(time));
    }
}
