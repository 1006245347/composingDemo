package cn.hwj.search.launch

import android.os.Bundle
import android.widget.TextView
import cn.hwj.search.R

/**
 * @author by jason-何伟杰，2023/2/11
 * des:此处的为登录页 、 闪屏页
 */
class LoginActivity : BaseAty() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
    }

    private fun initView() {
        findViewById<TextView>(R.id.tvLogin).setOnClickListener {
            //使用以下flag可以做到启动该Activity且可以不创建新实例，同时清空其他的Activity
            // intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            launchClear(cls = FirstActivity::class.java)
            finish()
        }
    }
}