package cn.hwj.search

import android.app.Activity
import android.os.Bundle
import cn.hwj.core.global.BaseModuleInit
import cn.hwj.core.global.CoreApplicationProvider
import cn.hwj.push.TagAliasOperatorHelper
import cn.hwj.route.RoutePath
import com.didi.drouter.annotation.Router

/**
 * @author by jason-何伟杰，2022/11/30
 * des:搜索模块的全局环境初始化所有业务
 */
@Router(path = RoutePath.SEARCH_ACTIVITY_INPUT)
class ModuleSearch : BaseModuleInit() {

    override fun onCreate() {
        super.onCreate()
        //这里的上下文要封装下
        TagAliasOperatorHelper.initJpushArg(CoreApplicationProvider.appContext, "search")
    }

    override fun onActivityCreate(activity: Activity, p1: Bundle?) {
        super.onActivityCreate(activity, p1)
    }

    override fun onActivityDestroyed(activity: Activity) {
        super.onActivityDestroyed(activity)
    }

    override val priority: Int
        get() = 10
}