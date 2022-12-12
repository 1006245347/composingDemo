package cn.hwj.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import cn.hwj.core.CoreUtils
import cn.hwj.core.global.printV
import cn.hwj.core.global.setLogEnable
import cn.hwj.route.RoutePath
import com.didi.drouter.annotation.Router
import com.didi.drouter.api.DRouter
import com.didi.drouter.api.Extend
import com.didi.drouter.router.RouterCallback

/**
 * @author by jason-何伟杰，2022/12/2
 * des:首页广告
 */
@Router(path = RoutePath.LOGIN_ACTIVITY_WELCOME)
class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        initAty()
    }

    private fun initAty() {
        launcher = registerForActivityResult(MyResultContract()) {
            it?.let { printV("forResult>>$it") }
        }
        val tvInfo = findViewById<TextView>(R.id.tvInfo)
        tvInfo.setOnClickListener {
            setLogEnable(true)
            clickEvent()
        }
        setLogEnable(false)
        printV("welcomeAty>>>")
    }

    var index=0
    private fun clickEvent() {
        switchAty(index)
        index++
    }

    private fun switchAty(type: Int = 0) {
        val intent = Intent(this, LoginActivity::class.java)
        //1.路由表跳转
        if (type == 0) {
            DRouter.build(RoutePath.LOGIN_ACTIVITY_LOGIN)
                .putExtra("phone", "17089499993")
                .start()
        } else if (type == 1) {
            DRouter.build(RoutePath.LOGIN_ACTIVITY_LOGIN)
                .putExtra("phone", "110")
//                .putExtra("ctx",ModuleLogin().getModuleApplication())
                .start(this) { result ->
                    if (result.isActivityStarted) { //是否能成功打开
                        Toast.makeText(
                         CoreUtils.getContext(),//             ModuleLogin().getModuleContext(),
                            "call 110 !",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else if (type == 2) {
            DRouter.build(RoutePath.LOGIN_ACTIVITY_LOGIN)
                .putExtra("phone", "114")
                .start(this, object : RouterCallback.ActivityCallback() {
                    override fun onActivityResult(resultCode: Int, data: Intent?) {
                        if (resultCode == Activity.RESULT_OK) {
                            printV(data?.getStringExtra("data"))
                        }
                    }
                })
        } else if (type == 3) {
            //旧api ActivityForResult
            DRouter.build(null)
                .putExtra("phone", "000000")
                .putExtra(Extend.START_ACTIVITY_VIA_INTENT, intent)
                .start(this, object : RouterCallback.ActivityCallback() {
                    override fun onActivityResult(resultCode: Int, data: Intent?) {
                        if (resultCode == Activity.RESULT_OK) {
                            printV(data?.getStringExtra("data"))
                        }
                    }
                })
        } else if (type == 4) {   //新api ActivityForResult
            DRouter.build("null")
                .putExtra("phone", "0000")
                .putExtra(Extend.START_ACTIVITY_VIA_INTENT, intent)
                .setActivityResultLauncher(launcher)
        }else if (type == 5) { //这里只处理数据，不开界面
            DRouter.build(RoutePath.LOGIN_FRAGMENT_FIRST)
                .start(this)
        }
    }

    var launcher: ActivityResultLauncher<Intent>? = null

    inner class MyResultContract : ActivityResultContract<Intent, String?>() {
        override fun createIntent(context: Context, input: Intent): Intent {
            return input
        }

        override fun parseResult(resultCode: Int, intent: Intent?): String? {
            return intent?.getStringExtra("phone")
        }

    }
}