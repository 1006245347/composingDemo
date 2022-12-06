package cn.hwj.core

import android.app.ActivityManager
import android.content.Context
import android.os.Process
import android.util.Log
import com.tencent.bugly.crashreport.CrashReport

object CoreUtils {

    fun testCore() {
        Log.v("TAG", "testCore()>>>>>")
    }

    fun testCrashUpload() {
        CrashReport.testJavaCrash()
    }

    fun initCrashReport(context: Context, appId: String, debug: Boolean) {
        CrashReport.initCrashReport(context, appId, debug)
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

}