package cn.hwj.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.hwj.core.CoreUtils
import cn.hwj.core.global.printV
import cn.hwj.route.RoutePath
import cn.hwj.web.X5WebView
import com.didi.drouter.annotation.Router
import com.tencent.smtt.sdk.WebChromeClient

@Router(path = RoutePath.SEARCH_ACTIVITY_WEB)
open class WebActivity : AppCompatActivity() {

    lateinit var mWebView: X5WebView
    private var mUrl: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        initWeb()
    }

    open fun initWeb() {
        printV("curProcess=${CoreUtils.getCurProcessName(this)}")
        mWebView = findViewById(R.id.webView)
        mWebView.webChromeClient = WebChromeClient()
        mUrl = intent.getStringExtra("url")
        mWebView.requestFocus()
        mWebView.post { mWebView.loadUrl(mUrl) }
    }
}