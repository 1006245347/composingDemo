package cn.hwj.search.process

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import cn.hwj.core.CoreUtils
import cn.hwj.route.RoutePath
import cn.hwj.search.R
import com.didi.drouter.annotation.Router
import com.didi.drouter.api.DRouter

/**
 * @author by jason-何伟杰，2024/2/19
 * des:Bundle只支持基础类型的多进程通信
 *
 * https://cloud.tencent.com/developer/article/1953493  WorkManager 在多进程应用中的高级用法
 */
@Router(path = RoutePath.SEARCH_ACTIVITY_BUNDLE)
class BundleActivity : AppCompatActivity() {

    lateinit var tvRev:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bundle)
        initView()
    }

    private fun initView() {
        tvRev=findViewById(R.id.tvRev)
        clickAction(R.id.btnBack) {
            finish()
        }
        clickAction(R.id.btnSend) {

        }
        tvRev.text=intent.getStringExtra("phone")+" ${CoreUtils.getCurProcessName(this)} ${Thread.currentThread().name}"
        printLog("${intent.extras?.getString("phone")}") //有值
    }

    fun clickAction(id: Int, block: () -> Unit) {
        findViewById<View>(id).setOnClickListener {
            block()
        }
    }
    fun printLog(txt:String) {
        Log.d("TAG",">$txt")
    }
}