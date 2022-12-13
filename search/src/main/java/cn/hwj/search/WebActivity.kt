package cn.hwj.search

//import android.webkit.WebChromeClient
//import android.webkit.WebView
//import android.webkit.WebViewClient
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import cn.hwj.core.global.printV
import cn.hwj.route.RoutePath
import cn.hwj.web.WebUtils
import cn.hwj.web.X5WebView
import com.didi.drouter.annotation.Router
import com.tencent.smtt.sdk.*

@Router(path = RoutePath.SEARCH_ACTIVITY_WEB)
open class WebActivity : AppCompatActivity() {

    lateinit var mWebView: X5WebView

    //        lateinit var mWebView: WebView
     var mUrl: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        isSuc()
        initWeb()
    }

    //这样模拟并解决、在SearchActivity给权限去下载、去初始化，下载时给个进度view,实际场景要100%好才让他跳转
    //到WebActivity 判断是否成功x5,因为第一次安装基本是在下载但同时也初始化了，2次初始化
    open fun initWeb() {
        mUrl = intent.getStringExtra("url")
        mWebView = X5WebView(this)
        if (mWebView.x5WebViewExtension != null) {
            printV("x5 isUsed>>>$mUrl")
        } else {
            printV("sys isUsed>>>$mUrl")
            setWebSettings()
        }
        val fl = findViewById<FrameLayout>(R.id.fl)
        mWebView.webChromeClient = WebChromeClient()
        mWebView.requestFocus()
        //去除qq浏览器
//        window.decorView.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
//            val outView = ArrayList<View>()
//            //可以把ActionBar的文本干掉了
////            window.decorView.findViewsWithText(outView, "composingDemo", View.FIND_VIEWS_WITH_TEXT)
//            window.decorView.findViewsWithText(outView, "QQ浏览器文件服务", View.FIND_VIEWS_WITH_TEXT)
//            val size = outView!!.size
//            printV(">>$size $outView")
//            if (outView != null && outView.size > 0) {
//                outView[0].visibility = View.GONE
//            }
//        }
        fl.addView(mWebView)
        mWebView.post { mWebView.loadUrl(mUrl!!) }
        //发现第一次没有去下载x5后，后面一直失败
        if (!WebUtils.isSucX5(mWebView)!!) {
            printV("x5--still not work!")
            TbsDownloader.needDownload(SearchUtils.getModuleContext(),true)  //会继续下载

            //这里的在现有流程不会执行，是个方案可了解
            //发现重置后就会不一定会自动下载x5,回退页面进入依然没有使用x5,还要再初始化(貌似每次初始化就会调用停止下载)
//            val r=object : BroadcastReceiver(){
//                override fun onReceive(p0: Context?, p1: Intent?) {
//                    printV("receive>>>")
//                    WebUtils.initX5Core(p0,"4c673d3784",true,null)
//                }
//            }
//            val filter = IntentFilter()
//            filter.addAction("x5_install")
//            registerReceiver(r,filter)
        }
    }

    open fun setWebSettings() {
        mWebView.let {
            val webSettings = it.settings
            webSettings.allowFileAccess = true
            webSettings.databaseEnabled = true
            webSettings.useWideViewPort = true
            webSettings.javaScriptCanOpenWindowsAutomatically = true
            webSettings.pluginsEnabled = true
            webSettings.javaScriptEnabled = true
            webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH)
//            webSettings.blockNetworkImage=true //网络图片无法加载
            webSettings.setAppCacheEnabled(true)
            webSettings.setSupportMultipleWindows(true)
            webSettings.setGeolocationEnabled(true)
            mWebView.webViewClient = object : WebViewClient() {} //原生要
        }
    }

    private fun isSuc(){ //这样重新初始化要保证 x5是100%下载完成
        val w=X5WebView(this)
        if (!WebUtils.isSucX5(w)!!) {
            WebUtils.initX5Core(this.applicationContext, "4c673d3784", true, null)
        }
    }
}