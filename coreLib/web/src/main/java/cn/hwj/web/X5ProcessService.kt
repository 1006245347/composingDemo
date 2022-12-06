package cn.hwj.web

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.tencent.smtt.sdk.QbSdk

/**
 * @author by jason-何伟杰，2022/12/6
 * des:新开进程进行 x5
 */
class X5ProcessService : Service() {

    private var mAppId: String? = null

    override fun onCreate() {
        super.onCreate()
        Log.v("TAG","X5Service-onCreate>>>")
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mAppId = intent?.getStringExtra("appId")
        WebUtils.initX5Core(this.applicationContext, mAppId+"", true, object : QbSdk.PreInitCallback {
            override fun onCoreInitFinished() {
                Log.v("TAG","onCoreInitFinished>>> " +
                        WebUtils.getCurProcessName(this@X5ProcessService)+
                        " $mAppId")
            }

            override fun onViewInitFinished(p0: Boolean) {
            }
        })
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}
