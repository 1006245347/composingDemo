package cn.hwj.core.global

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import android.text.TextUtils
import cn.hwj.core.CoreUtils.getCurProcessName
import com.didi.drouter.api.DRouter
import java.io.File

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
        if (TextUtils.equals(getCurProcessName(this), packageName)) {
            appContext = this
            initApp()//确保只在主进程初始化
            ModuleInitDelegate.reorder()
            ModuleInitDelegate.onCreate()
        }
        printV("Application执行次数》》$packageName")
    }

    /*适合基础库的初始化，会回调到所有模块*/
    open fun initApp() {
//        ???多个module下，如何都实现监听Activity生命并操作却不重复，？？每个module用接口不会吧
        //暂时在ModuleInitDelegate处理，后续再看
//        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacksImpl {
//            override fun onActivityCreated(activity: Activity, p1: Bundle?) {
//                super.onActivityCreated(activity, p1)
//                printV("custom_create>>$activity")
//            }
//
//            override fun onActivityDestroyed(activity: Activity) {
//                super.onActivityDestroyed(activity)
//                printV("custom_destroy>>$activity")
//            }
//        })

        DRouter.init(this) //初始化路由表
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(base)
        printV("attachBaseContext执行次数》》$packageName")
        ModuleInitDelegate.attachBaseContext(base)
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