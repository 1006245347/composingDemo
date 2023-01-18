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
        //非集成模式下没有依赖各业务模块，会爆红，需要手动注释处理,不处理也可直接run module
//        ModuleInitDelegate.register(ModuleLogin(),ModuleSearch())
    }
}