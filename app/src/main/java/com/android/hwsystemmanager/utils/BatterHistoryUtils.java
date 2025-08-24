package com.android.hwsystemmanager.utils;

import android.text.BidiFormatter;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.android.hwsystemmanager.MainApplication;
import com.android.hwsystemmanager.R;


public final class BatterHistoryUtils {

    public interface a {
        void mo7052a(boolean z10);
    }

    public interface b {
        void mo7053a(long j10, long j11, boolean z10);
    }

    public static String m7051a(long j10, long j11, boolean z10) {
        String unicodeWrap = BidiFormatter.getInstance().unicodeWrap(DateUtils.formatDateTime(MainApplication.getContext(), j10, 1));
        String unicodeWrap2 = BidiFormatter.getInstance().unicodeWrap(DateUtils.formatDateTime(MainApplication.getContext(), j11, 1));
        String formatDateTime = DateUtils.formatDateTime(MainApplication.getContext(), j10, 16);
        String formatDateTime2 = DateUtils.formatDateTime(MainApplication.getContext(), j11, 16);
        String m393c = (formatDateTime + " " + unicodeWrap);
        if (!TextUtils.equals(formatDateTime, formatDateTime2)) {
            unicodeWrap2 = (formatDateTime2 + " " + unicodeWrap2);
        }
        if (z10) {
            String string = MainApplication.getContext().getString(R.string.power_battery_choose_time_blind);
            return String.format(string, m393c, unicodeWrap2);
        }
        String string2 = MainApplication.getContext().getString(R.string.power_battery_choose_time);
        return String.format(string2, m393c, unicodeWrap2);
    }
}
