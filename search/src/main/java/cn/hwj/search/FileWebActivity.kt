package cn.hwj.search

import android.Manifest
import android.content.Context
import android.net.http.SslError
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.*
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import cn.hwj.core.CoreUtils
import cn.hwj.core.global.printV
import cn.hwj.route.RoutePath
import com.didi.drouter.annotation.Router
import com.permissionx.guolindev.PermissionX
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.ValueCallback
import org.json.JSONException
import org.json.JSONObject
import java.io.File

/**
 * @author by jason-何伟杰，2022/12/7
 * des:接入本地 浏览文件，运行要替换demo的文件路径
 *
 *  该页面在 web进程运行
 *
 *   */
@Router(path = RoutePath.SEARCH_ACTIVITY_FILE)
class FileWebActivity : WebActivity(), ValueCallback<String> {

    var mCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
    }

    override fun initWeb() {
        super.initWeb()
//        useSysWeb()//好奇怪的bug，每次第一次安装打开一定失败，不只是链接问题？
        val tvInfo = findViewById<TextView>(R.id.tvInfo)
        tvInfo.setOnClickListener {
            try {
                printV("${QbSdk.isSuportOpenFile("pdf", 2)}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            openFileReader(this, fileList[mCount])
            mCount++
            if (mCount == 7) mCount = 0
            tvInfo.text = "read$mCount"
        }
        val tvClick = findViewById<TextView>(R.id.tvClick)
        tvClick.setOnClickListener {
            printV("type=${QbSdk.canLoadX5(CoreUtils.getContext())}")
        }
        copyFile("testfiles", "tbsReadfile")
        PermissionX.init(this).permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    copyFile("testfiles", "tbsReadfile")
                } else {
                    Toast.makeText(this, "Deny $deniedList!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun copyFile(srcPath: String, dscPath: String) {
        FileUtils.instance.copyAssetsToSD(srcPath, dscPath)
            .setFileOperateCallback(object : FileUtils.FileOperateCallback {
                override fun onSuccess() {
                    getLocalFile()
                }

                override fun onFailed(error: String?) {
                    printV("err_copy=$error")
                }
            })
    }

    val fileList = mutableListOf<String>()
    private fun getLocalFile() {
        fileList.clear()
//        val dirName = File(Environment.getExternalStorageDirectory(), "tbsReadfile").absolutePath
        val dirName = File(SearchUtils.getCacheDir(), "tbsReadfile")
        for (s in assets.list("testfiles")!!) {
//            Log.v("TAG", "file=${dirName}/$s")
            fileList.add("$dirName/$s")
        }
    }

    private fun openFileReader(context: Context, pathName: String?) {
        val params = HashMap<String, String>()
        params["local"] = "true"
        params["style"] = "1" //1是微信样式
        params["topBarBgColor"] = "#2CFC47"  //想去掉个字符串  QQ浏览器文件服务
        val obj = JSONObject()
        try {
            obj.put("pkgName", context.applicationContext.packageName)
            obj.put("thirdCtx", "test_hwj>>>") //sdk只原样
            obj.put("className", "cn.hwj.search.ListActivity")
            obj.put(
                "menuItems",
                "[" + "{id:0,iconResId:" + R.mipmap.ic_launcher_round + ",text:\"menu0\"}," +
                        " {id:1,iconResId:" + R.mipmap.ic_launcher_round + ",text:\"menu1\"}," +
                        " {id:2,iconResId:" + R.mipmap.ic_launcher_round + ",text:\"menu2\"}" + "]"
            )
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        params["menuData"] = obj.toString()
        QbSdk.getMiniQBVersion(context)
        val ret: Int = QbSdk.openFileReader(context, pathName, params, this)

        //当x5没有下载好就初始化，就无法成功调取阅读器
        printV("openFile-error=$ret")
    }

    override fun onReceiveValue(msg: String?) {
        //单进程打开文件后 回调的msg 存在以下可关闭当前进程，减少内存
        //openFileReader open in QB                  用 QQ 浏览器打开
        //filepath error TbsReaderDialogClosed
        //default browser:
        //filepath error
        //fileReaderClosed

        printV("openFile-error2=$msg")
        if ("fileReaderClosed" == msg) {
            finish()
        }
    }

    fun useSysWeb() {
        mUrl = intent.getStringExtra("url")
        val mWebView = WebView(this)
        val fl = findViewById<FrameLayout>(R.id.fl)
        mWebView.webChromeClient = WebChromeClient()
        mWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                super.onReceivedSslError(view, handler, error)
                handler?.proceed()
            }
        }
        mWebView.let {
            val webSettings = it.settings
            webSettings.allowFileAccess = true
            webSettings.databaseEnabled = true
            webSettings.useWideViewPort = true
            webSettings.javaScriptCanOpenWindowsAutomatically = true
            webSettings.javaScriptEnabled = true
            webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH)
//            webSettings.blockNetworkImage=true //网络图片无法加载
//            webSettings.setAppCacheEnabled(true)
            webSettings.setSupportMultipleWindows(true)
            webSettings.setGeolocationEnabled(true)
            webSettings.blockNetworkImage = false
            webSettings.domStorageEnabled = true
        }
        fl.addView(mWebView)
        mWebView.loadUrl(mUrl!!)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        printV(CoreUtils.getCurProcessName(this))
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        printV(CoreUtils.getCurProcessName(this))
    }
}