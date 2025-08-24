package com.android.hwsystemmanager


data class LevelAndCharge(
    val rowId: Int,
    val level: Int,
    val time: Long,
    val charge: String,
    val shouldDrawLogo: Boolean,
) {
    internal constructor(level: Int, charge: String, time: Long) : this(
        0,
        level,
        time,
        charge,
        false
    )
}
