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
/**
 * @author by jason-何伟杰，2022/12/13
 * des: 后期试试  https://blog.csdn.net/u014620028/article/details/109215501
 * https://github.com/yangchong211/YCWebView/blob/master/read/Question1.md
 */
object WebUtils {
    fun testWeb() {
        println("testWeb()>>>")
    }

    /*判断是否成功使用x5*/
    fun isSucX5(webView: X5WebView?): Boolean? {
        webView?.let {
//            Log.v("TAG","IS--X5--${it.x5WebViewExtension!=null}")
            return it.x5WebViewExtension != null
        }
        return false
    }

    /*冷启动优化，配置和DexClassLoaderProviderService的使用*/
    fun perStartX5() {
        // 在调用TBS初始化、创建WebView之前进行如下配置
        val map = mutableMapOf<String, Any>()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
        QbSdk.initTbsSettings(map)
    }

    /*无wifi 也可以下载内核50mb*/
    fun setX5Config(forceStart: Boolean = false) {
        QbSdk.setNeedInitX5FirstTime(forceStart)
        QbSdk.setDownloadWithoutWifi(forceStart)
    }

    /*初始化 x5*/
    fun initX5Core(
        context: Context?,
        appId: String, debug: Boolean,
        callback: QbSdk.PreInitCallback?
    ) {
        //初始化
        QbSdk.initX5Environment(context, callback)
        QbSdk.setTbsListener(object : TbsListener {
            override fun onDownloadFinish(i: Int) { //非100都是失败
                if (i != 100) {
                    QbSdk.reset(context)
                }
                Log.v("TAG", "x5-download-finish>>>$i ${Thread.currentThread().name}")
            }

            override fun onInstallFinish(i: Int) { //非200都不成功
                Log.v("TAG", "x5-download-install>>>$i")
                if (i == 200) {
                    notifyInstallX5(context)
                }
            }

            override fun onDownloadProgress(i: Int) {
                Log.v("TAG", "X5-progress-$i")
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
        //统一初始化 调试时第三个参数=true 在其他进程又初始化日志框架有效?
        CrashReport.initCrashReport(context, appId, debug, strategy)
    }

    fun notifyInstallX5(context: Context?) {
        val intent = Intent()
        intent.action = "x5_install"
        context?.sendBroadcast(intent)
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
    fun startX5WebProcessPreInitService(
        context: Context?,
        appId: String,
        debug: Boolean = true
    ): Boolean {
        val currentProcessName = QbSdk.getCurrentProcessName(context)
        // 设置多进程数据目录隔离，不设置的话系统内核多个进程使用WebView会crash，X5下可能ANR
        WebView.setDataDirectorySuffix(QbSdk.getCurrentProcessName(context))
//        println("TAG-$currentProcessName")
        if (currentProcessName == context?.packageName) {
            val i = Intent(context, X5ProcessService::class.java)
            i.putExtra("appId", appId)
            i.putExtra("debug", debug)
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