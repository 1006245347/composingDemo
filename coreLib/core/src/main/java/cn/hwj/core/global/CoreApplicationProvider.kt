package cn.hwj.core.global

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import android.text.TextUtils
import cn.hwj.core.CoreUtils.getCurProcessName
import com.didi.drouter.api.DRouter
import java.io.File

/**
 * @author by jason-何伟杰，2022/12/8
 * des:共享Application
 */
open class CoreApplicationProvider : Application() {
    companion object {
        // 全局共享的 Application
        lateinit var appContext: Application

        @JvmStatic
        fun getAppCacheDir(): File? {
            return appContext.getExternalFilesDir(appContext.packageName + "_cache")
        }

        @JvmStatic
        fun getImageCacheDir(): File? {
            return appContext.getExternalFilesDir(appContext.packageName + "_img")
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (isAppMainProcess(this)) {
            appContext = this
            initApp()//确保只在主进程初始化
            ModuleInitDelegate.reorder()
            ModuleInitDelegate.onCreate()
        }
        printV("Application执行次数》》 $this")
    }

    //模块的初始化保证在主进程中
    private fun isAppMainProcess(context: Context): Boolean {
        if (TextUtils.equals(getCurProcessName(context), packageName)) {
            return true
        }
        return false
    }

    /*适合基础库的初始化，会回调到所有模块*/
    open fun initApp() {
        DRouter.init(this) //初始化路由表
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(base)
        if (isAppMainProcess(this)) {
            ModuleInitDelegate.attachBaseContext(base)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        ModuleInitDelegate.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        ModuleInitDelegate.onTrimMemory(level)
    }

    override fun onTerminate() {
        super.onTerminate()
        ModuleInitDelegate.onTerminate()
    }
}