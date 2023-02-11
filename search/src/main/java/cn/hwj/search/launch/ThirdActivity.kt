package cn.hwj.search.launch

import android.os.Bundle
import android.widget.TextView
import cn.hwj.search.R

class ThirdActivity : BaseAty() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)
        initView()
    }

    private fun initView() {
        findViewById<TextView>(R.id.tvThird).setOnClickListener {
            //测试一
//            launchClear(cls = LoginActivity::class.java)

            //测试二  intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK  //其他app拉起，每次调用都是新实例
//            intent.flags=Intent.FLAG_ACTIVITY_SINGLE_TOP
            launchClear(cls = FirstActivity::class.java)

            //测试三  intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK  //其他app拉起，每次调用都是新实例
//            launchClear(cls = NewsActivity::class.java)

            //测试四 intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//            launchClear(cls = ThirdActivity::class.java)

            //测试五 intent.flags=Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
//            launchClear(cls= SecondActivity::class.java)
        }
    }
}