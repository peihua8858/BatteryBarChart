package com.android.hwsystemmanager.utils

import android.os.Build

inline val isPie: Boolean
    get() = Build.VERSION.SDK_INT >=  Build.VERSION_CODES.P

inline val isOreoR1: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1
inline val isOreo: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O