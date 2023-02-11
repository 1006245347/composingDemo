package cn.hwj.search.launch

import android.os.Bundle
import android.widget.TextView
import cn.hwj.search.R

/**
 * @author by jason-何伟杰，2023/2/10
 * des:模拟 常驻首页
 */
class FirstActivity : BaseAty() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)
        initView()
    }

    private fun initView() {
        findViewById<TextView>(R.id.tvFirst).setOnClickListener {
            switchAty(cls = SecondActivity::class.java)
//            finish()//首页是否在栈中，flag会不同效果
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun finish() {
        printD(message = "finish>>>")
        super.finish()
    }
}