package cn.hwj.core.global

import android.app.Activity
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
            it.onCreate()
            if (moduleList.indexOf(it) == 0) //限制只执行一次
            //让每个module都有处理Activity的监听,要过滤，不然集成模式下会重复实现多次
                CoreApplicationProvider.appContext.registerActivityLifecycleCallbacks(object :
                    ActivityLifecycleCallbacksImpl {
                    override fun onActivityCreated(activity: Activity, p1: Bundle?) {
                        super.onActivityCreated(activity, p1)
                        it.onActivityCreate(activity, p1)
                    }

                    override fun onActivityStarted(activity: Activity) {
                        super.onActivityStarted(activity)
                        it.onActivityStarted(activity)
                    }

                    override fun onActivityResumed(activity: Activity) {
                        super.onActivityResumed(activity)
                        it.onActivityResumed(activity)
                    }

                    override fun onActivityPaused(activity: Activity) {
                        super.onActivityPaused(activity)
                        it.onActivityPaused(activity)
                    }

                    override fun onActivityStopped(activity: Activity) {
                        super.onActivityStopped(activity)
                        it.onActivityStopped(activity)
                    }

                    override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {
                        super.onActivitySaveInstanceState(activity, p1)
                        it.onActivitySaveInstanceState(activity, p1)
                    }

                    override fun onActivityDestroyed(activity: Activity) {
                        super.onActivityDestroyed(activity)
                        it.onActivityDestroyed(activity)
                    }
                })
        }
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
    /**
     * 模块初始化优先级 越高初始化越快
     * Module中设置优先级越小越优先初始化
     */
    open val priority: Int = 0
}