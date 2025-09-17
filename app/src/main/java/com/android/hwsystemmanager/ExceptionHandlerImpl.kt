package com.android.hwsystemmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.DeadSystemException
import android.os.Looper
import kotlin.system.exitProcess

/**
 * 终极解决方案
 */
class ExceptionHandlerImpl(private val mOldHandler: Thread.UncaughtExceptionHandler?) : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        if (isInterruptException(e)) {
            // 异常逻辑 1： 继续执行，进程不结束
            resumeMainThreadLoop()
            // restartApp（） 或者直接中断该进程，进行重启重启该app 的逻辑2
            return
        }
        mOldHandler?.uncaughtException(t, e)
    }

    private fun restartApp(context: Context) {
        // 重新启动应用程序或者系统服务
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, pendingIntent)
        // 退出应用程序或者停止服务
        exitProcess(0)
    }

    private fun resumeMainThreadLoop() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            return
        }
        try {
            Looper.loop()
        } catch (e: Exception) {
            uncaughtException(Thread.currentThread(), e)
        }
    }

    companion object {
        fun init() {
            //在bugly初始化或者自定义crash上报组件之后调用
            val mOldHandler = Thread.getDefaultUncaughtExceptionHandler()
            if (mOldHandler !is ExceptionHandlerImpl) {
                Thread.setDefaultUncaughtExceptionHandler(ExceptionHandlerImpl(mOldHandler))
            }
        }

        private fun isInterruptException(e: Throwable): Boolean {
            //拦截DeadSystemException
            return e is DeadSystemException || e.toString().contains("DeadSystemException")
        }
    }
}