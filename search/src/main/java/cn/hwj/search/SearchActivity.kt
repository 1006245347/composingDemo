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
        DRouter.build(RoutePath.SEARCH_ACTIVITY_LIST)
            .start()
    }

    private fun appendTxt(news: String): String {
        val txt = tvInfo.text
        val stringBuilder = StringBuilder()
            .append(txt)
            .append(news)
        tvInfo.setText(stringBuilder.toString())
        return stringBuilder.toString()
    }
}