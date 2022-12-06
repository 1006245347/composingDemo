package cn.hwj.search

import android.app.Activity
import android.os.Bundle
import cn.hwj.core.CoreUtils
import cn.hwj.core.global.BaseModuleInit
import cn.hwj.core.global.printV
import cn.hwj.push.PushHelper
import cn.hwj.web.WebUtils
import com.tencent.smtt.sdk.QbSdk

/**
 * @author by jason-何伟杰，2022/11/30
 * des:搜索模块的全局环境初始化所有业务
 */
class ModuleSearch : BaseModuleInit() {

    override fun onCreate() {
        super.onCreate()
        //极光推送初始化  这里的上下文要封装下
        PushHelper.initPushArg(ModuleSearch().getModuleContext(), "search")

        //第一种方式x5浏览器初始化 新开进程 疑问：文档中bugly要上报x5的异常信息是在新进程，要
        //两次日志框架初始化？测试下
        WebUtils.startX5WebProcessPreInitService(getModuleContext(), "4c673d3784")
        //第二种
//        WebUtils.let {
//            it.perStartX5()
//            it.setX5Config(true)
//            it.initX5Core(getModuleContext(), "4c673d3784", true, qbCall)
//        }
        CoreUtils.initCrashReport(getModuleContext(), "4c673d3784", true)
    }

    override fun onActivityCreate(activity: Activity, p1: Bundle?) {
        super.onActivityCreate(activity, p1)
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