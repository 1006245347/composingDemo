package cn.hwj.core.global

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle

/**
 * @author by jason-何伟杰，2022/11/30
 * des:关联各模块的application
 */
object ModuleInitDelegate : IModuleInit {

    private val moduleList = mutableListOf<IModuleInit>()

    fun register(vararg modules: IModuleInit) {
        moduleList.addAll(modules)
    }

    fun reorder() {
        moduleList.sortBy { (it as BaseModuleInit).priority }
    }

    override fun onCreate() {
        moduleList.forEach {
            it.onCreate() //执行每个module的第三方sdk初始化
        }
        CoreApplicationProvider.appContext.registerActivityLifecycleCallbacks(object :
            ActivityLifecycleCallbacksImpl {
            override fun onActivityCreated(activity: Activity, p1: Bundle?) {
                super.onActivityCreated(activity, p1)
                getModule(activity)?.onActivityCreate(activity, p1)
            }

            override fun onActivityStarted(activity: Activity) {
                super.onActivityStarted(activity)
                getModule(activity)?.onActivityStarted(activity)
            }

            override fun onActivityResumed(activity: Activity) {
                super.onActivityResumed(activity)
                getModule(activity)?.onActivityResumed(activity)
            }

            override fun onActivityPaused(activity: Activity) {
                super.onActivityPaused(activity)
                getModule(activity)?.onActivityPaused(activity)
            }

            override fun onActivityStopped(activity: Activity) {
                super.onActivityStopped(activity)
                getModule(activity)?.onActivityStopped(activity)
            }

            override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {
                super.onActivitySaveInstanceState(activity, p1)
                getModule(activity)?.onActivitySaveInstanceState(activity, p1)
            }

            override fun onActivityDestroyed(activity: Activity) {
                super.onActivityDestroyed(activity)
                getModule(activity)?.onActivityDestroyed(activity)
            }
        })
    }

    override fun attachBaseContext(base: Context) {
        moduleList.forEach {
            it.attachBaseContext(base)
        }
    }

    override fun onLowMemory() {
        moduleList.forEach { it.onLowMemory() }
    }

    override fun onTrimMemory(level: Int) {
        moduleList.forEach { it.onTrimMemory(level) }
    }

    override fun onTerminate() {
        moduleList.forEach { it.onTerminate() }
    }

    /*需要确保每个module的包名都是3层目录*/
    private fun getLocalPkName(clsPath: String): String {
        var path: String
        var index = clsPath.indexOf(".") //获取第一个.的位置
        index = clsPath.indexOf(".", index + 1) //获取第二个
        index = clsPath.indexOf(".", index + 1) //获取第三个
        path = clsPath.substring(0, index)
        return path
    }

    /*追溯当前Activity所在的module的包名*/
    private fun getModule(activity: Activity): IModuleInit? {
        for (m in moduleList) {
            if (m.toString().contains(getLocalPkName(activity.javaClass.name))) {
                return m
            }
        }
        return null
    }
}

interface IModuleInit {
    fun onCreate() {}
    fun attachBaseContext(base: Context) {}
    fun onLowMemory() {}
    fun onTrimMemory(level: Int) {}
    fun onTerminate() {}
    fun onActivityCreate(activity: Activity, p1: Bundle?) {}
    fun onActivityStarted(activity: Activity) {}
    fun onActivityResumed(activity: Activity) {}
    fun onActivityPaused(activity: Activity) {}
    fun onActivityStopped(activity: Activity) {}
    fun onActivitySaveInstanceState(activity: Activity, p1: Bundle?) {}
    fun onActivityDestroyed(activity: Activity) {}
}

abstract class BaseModuleInit : IModuleInit {

    fun getModuleApplication(): CoreApplicationProvider {
        return CoreApplicationProvider.appContext as CoreApplicationProvider
    }

    fun getModuleContext(): Context {
        return CoreApplicationProvider.appContext.applicationContext
    }

    /**
     * 模块初始化优先级 越高初始化越快
     * Module中设置优先级越小越优先初始化
     */
    open val priority: Int = 0
}