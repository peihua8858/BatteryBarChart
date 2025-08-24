package com.android.hwsystemmanager

data class SelectedItem(
    val startX: Float,
    val startY: Float,
    val time: Long,
    val screenWidth: Float,
) {
    var state: Int = 0
}
