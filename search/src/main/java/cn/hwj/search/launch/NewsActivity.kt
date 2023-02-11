package cn.hwj.search.launch

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import cn.hwj.search.R

/**
 * @author by jason-何伟杰，2023/2/11
 * des:突发的消息页、新闻文章页
 */
class NewsActivity : BaseAty() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        initView()
    }

    private fun initView() {
        findViewById<TextView>(R.id.tvNews).setOnClickListener {
            route2First()
        }
    }

    /*退出时应该回到上一页，或者回到常驻首页*/
    private fun route2First() {
        val intent = Intent(getCurAty(), FirstActivity::class.java)
        if (ActivityStackUtil.findAty(FirstActivity::class.java) != null) {
            //多个场景1.栈中存在FirstActivity,可以清掉它上面的并onNewIntent
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        } else {
            //2.栈中无常驻首页，它只会在上面建新实例 栈全清
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }
}