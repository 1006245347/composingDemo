package cn.hwj.search

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.hwj.core.CoreUtils
import cn.hwj.core.global.printV
import cn.hwj.push.PushHelper
import cn.hwj.push.PushListener
import cn.hwj.route.RoutePath
import com.didi.drouter.annotation.Router
import com.didi.drouter.api.DRouter
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
    }

    lateinit var tvInfo: TextView
    private fun initView() {
        tvInfo = findViewById(R.id.tvInfo)
        appendTxt("packageName: $packageName \n")
        appendTxt("process: ${CoreUtils.getCurProcessName(this)} \n")
        appendTxt("thread: ${Thread.currentThread().name} \n")
        appendTxt("appName: ${getString(R.string.app_name)} \n")
        appendTxt("Activity: $this")

        tvInfo.setOnClickListener {
            clickEvent()
        }

        //接收推送消息的处理
        PushHelper.instance?.pushListener=object : PushListener{
            override fun handlePushMsg(msg: String) {
             printV("handle1>>$msg")
            }
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
            .putExtra("url","https://www.baidu.com")
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

    private fun test(){
        val s=getString(R.string.app_name_pad)
        printV("s=$s") //竟然自动把双引号干掉了
        val js=JSONObject(s)
        printV("s=${js.getJSONObject("_default")}")
        val jv = js.getJSONObject("_default")
        jv?.let {
            printV("id=${ it.optString("clubId")}")

        }
    }
}