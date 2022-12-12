package cn.hwj.search

import android.Manifest
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.hwj.core.CoreUtils
import cn.hwj.core.global.printV
import cn.hwj.push.PushHelper
import cn.hwj.push.PushListener
import cn.hwj.route.RoutePath
import cn.hwj.web.WebUtils
import com.didi.drouter.annotation.Router
import com.didi.drouter.api.DRouter
import com.permissionx.guolindev.PermissionX
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.TbsDownloader
import org.json.JSONObject

/**
 * @author by jason-何伟杰，2022/12/2
 * des: 附加功能-在推送通知栏直接打开本页面
 */
@Router(path = RoutePath.SEARCH_ACTIVITY_INPUT)
class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        try {
            initView()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        PermissionX.init(this)
            .permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    initTbs()
                } else {
                    Toast.makeText(this, "Deny permission!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private lateinit var tvInfo: TextView
    private fun initView() {
        tvInfo = findViewById(R.id.tvInfo)
        appendTxt("packageName: $packageName \n")
        appendTxt("process: ${CoreUtils.getCurProcessName(this)} \n")
        appendTxt("thread: ${Thread.currentThread().name} \n")
        appendTxt("appName: ${getString(R.string.app_name)} \n")
        appendTxt("Activity: $this")
        tvInfo.setOnClickListener { clickEvent() }

        //接收推送消息的处理
        PushHelper.instance?.pushListener = object : PushListener {
            override fun handlePushMsg(msg: String) {
                printV("handle1>>$msg")
            }
        }
    }

    private fun initTbs() {
        printV("initTbs>>")
        WebUtils.let {
//            it.perStartX5()
//            it.setX5Config(true)
            it.initX5Core(this.applicationContext, "4c673d3784", true, qbCall)
        }
    }

    private fun clickEvent() {
//        printV("curProcess=${CoreUtils.getCurProcessName(this)}")
//        CoreUtils.testCrashUpload()
//        DRouter.build(RoutePath.SEARCH_ACTIVITY_LIST)
//            .start()

//        DRouter.build(RoutePath.SEARCH_ACTIVITY_WEB)
//            .putExtra("url","https://www.baidu.com")
//            .start()

        DRouter.build(RoutePath.SEARCH_ACTIVITY_FILE)
//            .putExtra("url", "https://www.baidu.com") //百度首次安装原生必不行！！
            .putExtra("url", "http://ark.gree.com/search/login/oauth2/authorize")
            .start()
    }

    private fun appendTxt(news: String): String {
        val txt = tvInfo.text
        val stringBuilder = StringBuilder()
            .append(txt)
            .append(news)
        tvInfo.text = stringBuilder.toString()
        return stringBuilder.toString()
    }

    private fun test() {
        val s = getString(R.string.app_name_pad)
        printV("s=$s") //竟然自动把双引号干掉了
        val js = JSONObject(s)
        printV("s=${js.getJSONObject("_default")}")
        val jv = js.getJSONObject("_default")
        printV("id=${jv.optString("clubId")}")
    }

    private fun startDownloadX5() { //要在子线程跑
        TbsDownloader.startDownload(CoreUtils.getContext())
    }

    private var mRetry = 1
    private val qbCall = object : QbSdk.PreInitCallback {
        override fun onCoreInitFinished() {
//            printV("x5call_finish>>")
//            val map = mutableMapOf<String, Any>()
//            map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER]=true
//            QbSdk.initTbsSettings(map)
        }

        override fun onViewInitFinished(flag: Boolean) {
            if (flag) {
                printV("tbs_finish= x5 运行")
            } else {  //首次安装本地没有X5内核可用，如果在X5初始化结束之前调用webview，那么默认会使用系统内核
                //要观察这里，运行到这x5 、文件预览就失败了
                printV("tbs_finish= sys web运行")
//                QbSdk.reset(CoreUtils.getContext())
                if (mRetry > 0) {
//                    initTbs()
                    mRetry--
                }
            }
        }
    }
}