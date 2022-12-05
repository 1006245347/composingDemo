package cn.hwj.search

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.hwj.core.CoreUtils
/**
 * @author by jason-何伟杰，2022/12/2
 * des: 附加功能-在推送通知栏直接打开本页面
 */
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