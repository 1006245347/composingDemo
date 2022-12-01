package cn.hwj.composing

import cn.hwj.core.global.CoreApplicationProvider
import cn.hwj.core.global.ModuleInitDelegate
//import cn.hwj.login.ModuleLogin
//import cn.hwj.search.ModuleSearch

/**
 * @author by jason-何伟杰，2022/11/30
 * des:壳 模块app 不应存在任何代码类，只提供个上下文管理
 */
class MainApplication : CoreApplicationProvider() {

    init {
        //非集成模式下没有依赖各业务模块，会爆红，需要手动注释处理。。。反射实现自动？
//        ModuleInitDelegate.register(ModuleLogin(),ModuleSearch())
    }

    override fun onCreate() {
        super.onCreate()

        //集成路由所有模块都写，这里不写也行
//        ARouter.openDebug()
//        ARouter.openLog()
//        ARouter.init(appContext)

    }

    override fun onTerminate() {
        super.onTerminate()
//        ARouter.getInstance().destroy()
    }
}