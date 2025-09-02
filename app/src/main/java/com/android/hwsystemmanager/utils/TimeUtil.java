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

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
    public static final SimpleDateFormat DATE_FORMAT1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
    public static final SimpleDateFormat DATE_HOUR_FORMAT = new SimpleDateFormat("HH", Locale.ROOT);

    /**
     * 获取指定时间戳的凌晨0点
     *
     * @param timestamp 时间戳
     * @return 凌晨0点的时间戳
     */
    public static long getMidnightTimestamp(long timestamp) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(timestamp);
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);
        return instance.getTimeInMillis();
    }

    /**
     * 获取当前时间戳的凌晨0点
     *
     * @return 凌晨0点的时间戳
     */
    public static long getMidnightTimestamp() {
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);
        return instance.getTimeInMillis();
    }

    public static boolean isEarlyMorning() {
        return (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 1) % 24 < 8;
    }

    public static String formatBatteryChooseTime(int state, long time) {
        long adjustedTime = 3600000 + time;
        Calendar instance = Calendar.getInstance();
        if (state == -1) {
            adjustedTime -= 1800000;
        } else if (state == 1) {
            time += 1800000;
        }
        instance.setTimeInMillis(time);
        String formatDateTime = DateUtils.formatDateTime(MainApplication.getContext(), instance.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME);
        instance.setTimeInMillis(adjustedTime);
        String formatDateTime2 = DateUtils.formatDateTime(MainApplication.getContext(), instance.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME);
        String format = MainApplication.getContext().getString(R.string.power_battery_choose_time);
        String content = String.format(format, formatDateTime, formatDateTime2);
        return BidiFormatter.getInstance().unicodeWrap(content);
    }

    public static String formatTime(long time) {
        return DATE_FORMAT1.format(new Date(time));
    }

    public static String formatHourTime(long time) {
        return DATE_HOUR_FORMAT.format(new Date(time));
    }
}
