package cn.hwj.search.launch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseAty : AppCompatActivity() {

    fun switchAty(context: Context = this, cls: Class<*>) {
        val intent = Intent(context, cls)
        context.startActivity(intent)
    }

    //首页、登录页 都有可能成为栈中唯一Activity
    //adb shell dumpsys activity activities   获取当前栈
    fun launchClear(context: Context = this, cls: Class<*>) {
        val intent = Intent(context, cls)
        //含有待启动Activity的Task在Activity被启动前清空,新实例;
        //栈中没启动过该Activity,会在本栈顶创建该新Activity
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

        //含有待启动Activity的Task在Activity被启动前清空,不含也清空,新实例
//        intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK  //其他app拉起，每次调用都是新实例

        //含有待启动Activity但不置顶,清空启动Activity上的活动，而且创建新的实例
        //含有待启动Activity且置顶,清掉顶的Activity，创建新的实例
        //栈中没启动过该Activity,会在本栈顶创建该新Activity
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP  //常用突发的消息页，新闻页

        //含有待启动Activity但不置顶,清空启动Activity上的，但不创建新实例，调用onNewIntent()、onRestart
        //含有待启动Activity且置顶，不变，走 onNewIntent()
        //待启动不在栈中，不会清栈，在原本上新实例
        //总体来看和LaunchMode中的SingleTask在未定义android:taskAffinity属性时一致
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP   //常驻页，但数据要频繁更新

        //和LaunchMode的SingleTop一样，启动的Activity已置顶有就不变，但是走 onNewIntent()
        //启动的Activity在栈中但未置顶，在栈后创建新的实例
        //栈中没启动过该Activity,会在本栈顶创建该新Activity
//        intent.flags=Intent.FLAG_ACTIVITY_SINGLE_TOP  //常用突发的消息页，新闻页

        //复用栈中的Activity,该Activity会移到栈顶，栈顺序改变，调onNewIntent()
//        intent.flags=Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        context.startActivity(intent)
    }

    fun getCurAty(): AppCompatActivity {
        return this
    }

    fun sendBroadcastLimit() {
        when (getCurAty()::class.java) {
            FirstActivity::class.java -> {
                sendBroadcast(ACTION_FIRST)
            }
            SecondActivity::class.java -> {
                sendBroadcast(ACTION_SECOND)
            }
            ThirdActivity::class.java -> {
                sendBroadcast(ACTION_THIRD)
            }
            LoginActivity::class.java -> {
                sendBroadcast(ACTION_LOGIN)
            }
            NewsActivity::class.java -> {
                sendBroadcast(ACTION_NEW)
            }
        }
    }

    //发送广播
    fun sendBroadcast(action: String,cls: Class<*> = getCurAty()::class.java) {
        val intent = Intent()
        intent.putExtra("cls", "$cls") //字符串没法转class
        intent.action = action
        sendBroadcast(intent)
    }

    fun isCloseAty(): Boolean {
        if (getCurAty() == null || getCurAty().isDestroyed || getCurAty().isFinishing) {
            return true
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityStackUtil.add(this)
        printD(message = "onCreate>>>${getCurAty()}")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        printD(message = "onNewIntent()>>>${getCurAty()}")
    }

    override fun onDestroy() {
        printD(message = "onDestroy()>>>${getCurAty()}")
        super.onDestroy()
        ActivityStackUtil.del(this)
        sendBroadcastLimit()
    }

    val ACTION_FIRST = "first"
    val ACTION_SECOND = "second"
    val ACTION_THIRD = "third"
    val ACTION_LOGIN = "login"
    val ACTION_NEW = "news"
}