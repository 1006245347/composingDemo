package cn.hwj.core

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import android.util.Log
import cn.hwj.core.global.CoreApplicationProvider
import cn.hwj.core.global.MMKVUtils
import cn.hwj.core.global.printD
import com.tencent.bugly.crashreport.CrashReport
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*
import kotlin.NoSuchElementException

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

    fun getContext(): Context {
        return CoreApplicationProvider.appContext.applicationContext
    }

    fun getApplication(): Application {
        return CoreApplicationProvider.appContext
    }

    fun <T> printObj(obj: T?): String? {
        if (obj == null) return null
        val map: MutableMap<String, String> = HashMap()
        val fieldArray: Array<Field> = getT<T>(obj)!!::class.java.declaredFields
        printD("obj=$fieldArray")
//        for (i in fieldArray.indices) {
////            val o = getFieldValueByName(fieldArray[i].getName(), getT<T>(obj)!!)
//            printD(fieldArray[i].getName() + ">>>>>$o");
//            map[fieldArray[i].getName()] = o?.toString() ?: ""
//        }
        return map.toString()
    }

    fun <T> getT(t: T): T {
        return t
    }

//    fun getFieldValue(fieldName: String, o: Any): Any {
//        try {
//            val t = o::class.members
//            t.forEach {
//                printD("t>>>$it")
//            }
//        } catch (e: NoSuchElementException) {
//            e.printStackTrace()
//            throw NoSuchFieldException(fieldName)
//        }
//    }

    private fun getFieldValueByName(fieldName: String, o: Any): Any? {
        return try {
            val firstLetter =
                fieldName.substring(0, 1).uppercase(Locale.getDefault())
            val getter = "get" + firstLetter + fieldName.substring(1)
            val method: Method = o.javaClass.getMethod(getter, *arrayOf())
            method.invoke(o, arrayOf<Any>())
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

fun <T> MMKVUtils.addObj(obj: T) {
    CoreUtils.printObj(obj)
}