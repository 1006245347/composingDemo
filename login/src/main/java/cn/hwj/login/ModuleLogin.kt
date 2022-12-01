package cn.hwj.login

import android.app.Activity
import android.os.Bundle
import cn.hwj.core.global.BaseModuleInit

/**
 * @author by jason-何伟杰，2022/11/30
 * des:登录模块的全局环境初始化 所有业务
 */
class ModuleLogin : BaseModuleInit(){

    override fun onCreate() {
        super.onCreate()
    }

    override fun onActivityCreate(activity: Activity, p1: Bundle?) {
        super.onActivityCreate(activity, p1)
    }

    override fun onActivityDestroyed(activity: Activity) {
        super.onActivityDestroyed(activity)
    }

    override val priority: Int
        get() = 0 //越小越先初始化
}