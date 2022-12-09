package cn.hwj.login

import android.app.Activity
import android.app.Application
import android.os.Bundle
import cn.hwj.core.global.BaseModuleInit
import cn.hwj.core.global.CoreApplicationProvider
import cn.hwj.core.global.printV

/**
 * @author by jason-何伟杰，2022/11/30
 * des:登录模块的全局环境初始化 所有业务
 */
class ModuleLogin : BaseModuleInit() {

    override fun onCreate() {
        super.onCreate()
    }

    //监听回调所有的Activity
    override fun onActivityCreate(activity: Activity, p1: Bundle?) {
        super.onActivityCreate(activity, p1)
        printV("login_module_check_create>$activity")
    }

    override fun onActivityDestroyed(activity: Activity) {
        super.onActivityDestroyed(activity)
    }

    override val priority: Int
        get() = 0 //越小越先初始化
}

/*扩展函数*/
fun ModuleLogin.getApplication(): Application {
    return CoreApplicationProvider.appContext
}