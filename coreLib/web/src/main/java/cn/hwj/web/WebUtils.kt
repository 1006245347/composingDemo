package cn.hwj.web

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Process
import android.util.Log
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.bugly.crashreport.CrashReport.CrashHandleCallback
import com.tencent.bugly.crashreport.CrashReport.UserStrategy
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.TbsListener
import com.tencent.smtt.sdk.WebView


object WebUtils {
    fun testWeb() {
        println("testWeb()>>>")
    }

    /*无wifi 也可以下载内核50mb*/
    fun setX5Config(forceStart: Boolean = false) {
        QbSdk.setNeedInitX5FirstTime(forceStart)
        QbSdk.setDownloadWithoutWifi(forceStart)
    }

    /*冷启动优化，配置和DexClassLoaderProviderService的使用*/
    fun perStartX5() {
        // 在调用TBS初始化、创建WebView之前进行如下配置
        val map = mutableMapOf<String, Any>()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
        QbSdk.initTbsSettings(map)
    }

    /*初始化 x5*/
    fun initX5Core(
        context: Context?,
        appId: String,debug:Boolean,
        callback: QbSdk.PreInitCallback
    ) {

        if (!QbSdk.isTbsCoreInited()) {
            //初始化
            QbSdk.initX5Environment(context, callback)
            QbSdk.setTbsListener(object : TbsListener {
                override fun onDownloadFinish(i: Int) {
                    println("x5-download-finish>>>$i")
                }

                override fun onInstallFinish(i: Int) {
                    println("x5-download-install>>>$i")
                }

                override fun onDownloadProgress(i: Int) {
                }
            })
            //联合上报崩溃日志
            val strategy = UserStrategy(context)
            strategy.setCrashHandleCallback(object : CrashHandleCallback() {
                @Synchronized
                override fun onCrashHandleStart(
                    crashType: Int,
                    errorType: String,
                    errorMessage: String,
                    errorStack: String
                ): Map<String, String> {
                    val map = LinkedHashMap<String, String>()
                    val x5CrashInfo =
                        WebView.getCrashExtraMessage(context)
                    map["x5crashInfo"] = x5CrashInfo
                    return map
                }

                @Synchronized
                override fun onCrashHandleStart2GetExtraDatas(
                    crashType: Int,
                    errorType: String,
                    errorMessage: String,
                    errorStack: String
                ): ByteArray? {
                    return try {
                        "Extra data.".toByteArray(charset("UTF-8"))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        return null
                    }
                }
            })
            //统一初始化 调试时第三个参数=true 在其他进程又初始化日志框架有效？
            CrashReport.initCrashReport(context, appId, debug, strategy)
        }
    }




    /**
     * 启动X5 独立Web进程的预加载服务。优点：
     * 1、后台启动，用户无感进程切换
     * 2、启动进程服务后，有X5内核时，X5预加载内核
     * 3、Web进程Crash时，不会使得整个应用进程crash掉
     * 4、隔离主进程的内存，降低网页导致的App OOM概率。
     *
     * 缺点：
     * 进程的创建占用手机整体的内存，demo 约为 150 MB
     */
    fun startX5WebProcessPreInitService(context: Context?, appId: String): Boolean {
        val currentProcessName = QbSdk.getCurrentProcessName(context)
        // 设置多进程数据目录隔离，不设置的话系统内核多个进程使用WebView会crash，X5下可能ANR
        WebView.setDataDirectorySuffix(QbSdk.getCurrentProcessName(context))
//        println("TAG-$currentProcessName")
        Log.v("TAG","x5-process=$currentProcessName")
        if (currentProcessName == context?.packageName) {
//            context?.startService(Intent(context, X5ProcessService::class.java))
            val i = Intent(context, X5ProcessService::class.java)
            i.putExtra("appId", appId)
            context?.startService(i)
            return true
        }
        return false
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