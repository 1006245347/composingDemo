package cn.hwj.search

import android.app.Application
import android.content.Context
import cn.hwj.core.global.CoreApplicationProvider
import cn.hwj.core.global.printV
import java.io.File

object SearchUtils {

    fun getCacheDir(): File? {
        return CoreApplicationProvider.getAppCacheDir()
    }

    fun getModuleContext() : Context{
        return CoreApplicationProvider.appContext.applicationContext
    }

    fun getModuleApplication():Application{
        return CoreApplicationProvider.appContext
    }
}