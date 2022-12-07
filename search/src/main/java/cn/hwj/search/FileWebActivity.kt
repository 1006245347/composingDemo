package cn.hwj.search

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.view.KeyEvent
import android.widget.TextView
import cn.hwj.core.CoreUtils
import cn.hwj.core.global.printV
import cn.hwj.route.RoutePath
import com.didi.drouter.annotation.Router
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.ValueCallback
import org.json.JSONException
import org.json.JSONObject

/**
 * @author by jason-何伟杰，2022/12/7
 * des:接入本地 浏览文件，运行要替换demo的文件路径
 *
 *  该页面在 web进程运行
 */
@Router(path = RoutePath.SEARCH_ACTIVITY_FILE)
class FileWebActivity : WebActivity(), ValueCallback<String> {

    val mFilePath = Environment.getExternalStorageDirectory().toString()

    //在测试机中添加以下文件
    val pdfFile = "/storage/emulated/0/Documents/ThirdInterface.pdf"
    val xlsFile = "/storage/emulated/0/Documents/buglist.xls"
    val txtFile = "/storage/emulated/0/Documents/web_进入.txt"//带中文的文件名
    val docxFile = "/storage/emulated/0/Documents/Boxtech.docx"

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
        val tvInfo = findViewById<TextView>(R.id.tvInfo)
        tvInfo.setOnClickListener {

//            if (QbSdk.is)
            //要文件权限
//            openFile()
            openFileReader(this, pdfFile) // ok //试过下载插件失败
//            openFileReader(this, xlsFile)//ok
//            openFileReader(this, docxFile)// ok
//            openFileReader(this,txtFile)//ok
        }
    }

    private fun openFile() {
        val params = hashMapOf<String, String>()
        params.put("local", "true")
        params.put("entryId", "2")
        params.put("allowAutoDestroy", "true")
        val jo = JSONObject()
        try {
            jo.put("pkName", this.packageName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        params.put("menuData", jo.toString())
        QbSdk.openFileReader(this, pdfFile, params, this)
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        printV(CoreUtils.getCurProcessName(this))
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        printV(CoreUtils.getCurProcessName(this))
    }
}