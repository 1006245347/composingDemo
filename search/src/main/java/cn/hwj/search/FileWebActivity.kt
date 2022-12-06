package cn.hwj.search

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import cn.hwj.core.global.printV
import cn.hwj.route.RoutePath
import com.didi.drouter.annotation.Router
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient
import org.json.JSONException
import org.json.JSONObject

@Router(path = RoutePath.SEARCH_ACTIVITY_FILE)
class FileWebActivity : WebActivity(), ValueCallback<String> {

    val mFilePath = Environment.getExternalStorageDirectory().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initWeb() {
        super.initWeb()
//        mWebView.webChromeClient = object : WebChromeClient() {
//            override fun openFileChooser(p0: ValueCallback<Uri>?, p1: String?, p2: String?) {
//                super.openFileChooser(p0, p1, p2)
//            }
//        }
        printV("x5-state=${QbSdk.isTbsCoreInited()}")

    }

    fun openFileReader(context: Context, pathName: String?) {
        val params = HashMap<String, String>()
        params["local"] = "true"
        val Object = JSONObject()
        try {
            Object.put("pkgName", context.getApplicationContext().getPackageName())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        params["menuData"] = Object.toString()
        QbSdk.getMiniQBVersion(context)
        val ret: Int = QbSdk.openFileReader(context, pathName, params, this)

        //openFileReaderListWithQBDownload
    }

    override fun onReceiveValue(p0: String?) {

    }
}