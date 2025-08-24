package com.android.hwsystemmanager.utils


public inline fun <C : R, R : CharSequence> C.ifContains(
    str: String,
    black: () -> R,
): R {
    return if (this.contains(str)) black() else this
}

fun String.parseInt(default: Int): Int {
    return try {
        Integer.parseInt(this)
    } catch (e: Exception) {
        default
    }
}

fun String.parseLong(default: Long): Long {
    return try {
        java.lang.Long.parseLong(this)
    } catch (e: Exception) {
        default
    }
}