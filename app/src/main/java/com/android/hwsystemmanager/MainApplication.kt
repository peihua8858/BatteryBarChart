package com.android.hwsystemmanager

import android.app.Application
import android.content.Context

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        mContext = this
    }

    companion object {
        private var mContext: MainApplication = MainApplication()
        @JvmStatic
        val context: Context
            get() = mContext
        @JvmStatic
        val application: MainApplication
            get() = mContext
    }
}