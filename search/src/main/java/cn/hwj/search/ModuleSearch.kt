package cn.hwj.search

import android.app.Activity
import android.os.Bundle
import cn.hwj.bridge.ModuleFactory
import cn.hwj.core.global.BaseModuleInit
import cn.hwj.core.global.printV
import com.tencent.smtt.sdk.QbSdk

/**
 * @author by jason-何伟杰，2022/11/30
 * des:搜索模块的全局环境初始化所有业务
 */
class ModuleSearch : BaseModuleInit() {

    override fun onCreate() {
        //提供对外模块通信数据能力  不创建SearchServiceApi()就不被对外通信
//        ModuleFactory.instance.setSearchService(SearchServiceApi())

        val isDebug = true  //测试调试
        val crashId = "4c673d3784"

        //极光推送初始化  这里会开启新的进程
//        PushHelper.setPushDebug(isDebug)
//        PushHelper.initPushArg(ModuleSearch().getModuleContext(), "search")

        //*****Tbs出现个bug,当第一次安装应用没有赋予权限就初始化了，无法使用文件阅读器，有些某文件夹无法访问

        //第一种方式x5浏览器初始化 新开进程 疑问：文档中bugly要上报x5的异常信息是在
        // 新进程，要 //两次日志框架初始化？测试下
//        WebUtils.startX5WebProcessPreInitService(getModuleContext(), crashId, debug = isDebug)

        //第二种,这里内部在主进程初始化了日志框架
//        WebUtils.let {
//            it.perStartX5()
//            it.setX5Config(true)
//            it.initX5Core(getModuleContext(), crashId, debug = isDebug, qbCall)
//        }

        //第二种要注释以下initCrashReport，每个进程是独立的，在主进程初始化一次
//        CoreUtils.initCrashReport(getModuleContext(), crashId, debug = isDebug)
    }

    override fun onActivityCreate(activity: Activity, p1: Bundle?) {
        super.onActivityCreate(activity, p1)
        printV("search_module_check_create>$activity")
    }

    override fun onActivityDestroyed(activity: Activity) {
        super.onActivityDestroyed(activity)
    }

    private val qbCall = object : QbSdk.PreInitCallback {
        override fun onCoreInitFinished() {
            printV("x5call_finish>>")
        }

        override fun onViewInitFinished(p0: Boolean) {
        }
    }
    override val priority: Int
        get() = 10
}

//这种写法每次都会创建新的对象 ModuleSearch！不合适
//fun ModuleSearch.getCacheDir(): File? {
//    printV("obj_module_search=$this")
//    return CoreApplicationProvider.getAppCacheDir()
//}