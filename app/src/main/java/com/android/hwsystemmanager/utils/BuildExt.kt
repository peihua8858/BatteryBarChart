package com.android.hwsystemmanager.utils

import android.os.Build

inline val isPie: Boolean
    get() = Build.VERSION.SDK_INT >= 28