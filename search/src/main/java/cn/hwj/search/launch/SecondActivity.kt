package cn.hwj.search.launch

import android.os.Bundle
import android.widget.TextView
import cn.hwj.search.R

class SecondActivity : BaseAty() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        initView()
    }

    private fun initView() {
        findViewById<TextView>(R.id.tvSecond).setOnClickListener {
            switchAty(cls = ThirdActivity::class.java)
//            finish()
        }
    }
}