package cn.hwj.core

import android.app.ActivityManager
import android.content.Context
import android.os.Process
import android.util.Log
import cn.hwj.core.global.CoreApplicationProvider

object CoreUtils {
    fun testCore() {
        Log.v("TAG", "testCore()>>>>>")
    }

    fun getCurProcessName(context: Context): String {
        val pid = Process.myPid()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (appProcess in activityManager
            .runningAppProcesses) {
            if (appProcess.pid == pid) {
                return appProcess.processName
            }
        }
        return context.packageName
    }

    fun getContext(): Context {
        return  CoreApplicationProvider.appContext
    }
}