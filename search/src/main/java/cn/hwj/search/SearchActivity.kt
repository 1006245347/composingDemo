package cn.hwj.search

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import cn.hwj.bridge.ModuleFactory
import cn.hwj.core.CoreUtils
import cn.hwj.core.global.CoreApplicationProvider
import cn.hwj.core.global.MMKVUtils
import cn.hwj.core.global.printD
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
        askPermission()
    }

    private fun askPermission() {
        val requestList = ArrayList<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestList.add(Manifest.permission.READ_MEDIA_IMAGES)
            requestList.add(Manifest.permission.READ_MEDIA_AUDIO)
            requestList.add(Manifest.permission.READ_MEDIA_VIDEO)
        } else { //Android 13 特有权限，由于编译器在sdk33无法自动补全xml代码，后续再升级As
            requestList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                initTbs()
                testMultiCache()
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                val startActivity =
                    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                        if (Environment.isExternalStorageManager()) {
                            initTbs()
                            testMultiCache()
                        } else {
                            Toast.makeText(this, "存储权限获取失败", Toast.LENGTH_SHORT).show()
                        }
                    }
                startActivity.launch(intent)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionX.init(this)
                .permissions(requestList)
                .onExplainRequestReason { scope, deniedList ->
                    val msg = "同意以下权限使用："
                    scope.showRequestReasonDialog(deniedList, msg, "Allow", "Deny")
                }.request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        Toast.makeText(this, "all granted!", Toast.LENGTH_SHORT).show()
                        initTbs()
                        testMultiCache()
                    } else {
                        Toast.makeText(this, "Deny $deniedList!", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun testMultiCache() {
        //测试组件化多进程数据获取
        MMKVUtils.setSavePath(CoreApplicationProvider.getGlobalDir(), "login")
        printD("user=${MMKVUtils.getStr("user")} ")
        MMKVUtils.setSavePath(CoreApplicationProvider.getGlobalDir(), "search")
        MMKVUtils.addStr("u", "uu")
        printD("search>${MMKVUtils.getStr("u")} ${MMKVUtils.getStr("user")}")
    }

    private lateinit var tvInfo: TextView
    private fun initView() {
        findViewById<TextView>(R.id.tvClick).setOnClickListener {
            QbSdk.reset(SearchUtils.getModuleContext()) //看注释只能在同一进程有效一次
        }
        findViewById<TextView>(R.id.tvProcess).setOnClickListener {
            DRouter.build(RoutePath.SEARCH_ACTIVITY_BUNDLE)
                .putExtra("phone","114")
                .start()
        }
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

        //测试对外模块通信能力
        if (ModuleFactory.instance.getLoginService()?.isLogin() == true) {
            Toast.makeText(this, "已登录", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "请登录", Toast.LENGTH_SHORT).show()
        }
        //在其他module调用，这里简单测试
        printV("searchModule=${ModuleFactory.instance.getSearchService()?.getResult()}")
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
//        DRouter.build(RoutePath.SEARCH_ACTIVITY_LIST)
//            .start()

//        DRouter.build(RoutePath.SEARCH_ACTIVITY_MENU)
//            .putExtra("type",1)
//            .putExtra("type", 0) //普通适配器列表
//            .start()

//        DRouter.build(RoutePath.SEARCH_ACTIVITY_WEB)
//            .putExtra("url","https://www.baidu.com")
//            .start()

//        //有个极端情景：进入本页面快速点击再跳转，还没来得及异步下载，这里就不触发了，需要加个延迟
//        if (TbsDownloader.isDownloading()) {
//            Toast.makeText(SearchUtils.getModuleContext(), "x5 is download !", Toast.LENGTH_SHORT)
//                .show()
//            return
//        }

//        DRouter.build(RoutePath.SEARCH_ACTIVITY_FILE)
////         //   .putExtra("url", "https://www.baidu.com") //百度的链接首次安装app原生必不行！！
//            .putExtra("url", "http://ark.gree.com/search/login/oauth2/authorize")
//            .start()
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

    private fun startDownloadX5() { //要在子线程跑 ???
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