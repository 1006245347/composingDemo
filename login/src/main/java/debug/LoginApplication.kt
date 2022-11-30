package debug

import android.content.Context
import cn.hwj.core.global.CoreApplicationProvider
import cn.hwj.core.global.ModuleInitDelegate
import cn.hwj.login.ModuleLogin

/**
 * @author by jason-何伟杰，2022/11/30
 * des:单独打包需要提供Application,将逻辑都套装 ModuleLogin 中
 */
class LoginApplication : CoreApplicationProvider() {
    init {
        ModuleInitDelegate.register(ModuleLogin())
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    override fun onTerminate() {
        super.onTerminate()
    }
}