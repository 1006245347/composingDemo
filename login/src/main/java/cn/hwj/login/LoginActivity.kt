package cn.hwj.login

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.hwj.core.CoreUtils
import cn.hwj.route.RoutePath
import com.didi.drouter.annotation.Router
import com.permissionx.guolindev.PermissionX

/**
 * @author by jason-何伟杰，2022/12/2
 * des:模块登录的 账号输入页面
 */
@Router(path = RoutePath.LOGIN_ACTIVITY_LOGIN)
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
    }

    lateinit var tvInfo: TextView
    lateinit var tvPhone: TextView
    private fun initView() {
        tvInfo = findViewById(R.id.tvInfo)
        tvPhone = findViewById(R.id.tvPhone)
        appendTxt("packageName: $packageName \n")
        appendTxt("process: ${CoreUtils.getCurProcessName(this)} \n")
        appendTxt("thread: ${Thread.currentThread().name} \n")
        appendTxt("appName: ${getString(R.string.app_name)} \n")
        appendTxt("Activity: $this")
        intent?.getStringExtra("phone")?.let {
            tvPhone.text = it
        }
        tvPhone.setOnClickListener {
            askPermission()
        }
        tvInfo.setOnClickListener { askNotification() }
    }

    private fun appendTxt(news: String): String {
        val txt = tvInfo.text
        val stringBuilder = StringBuilder()
            .append(txt)
            .append(news)
        tvInfo.setText(stringBuilder.toString())
        return stringBuilder.toString()
    }

    private fun askPermission() {
        val requestList = ArrayList<String>()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            requestList.add(Manifest.permission.READ_MEDIA_IMAGES)
//            requestList.add(Manifest.permission.READ_MEDIA_AUDIO)
//            requestList.add(Manifest.permission.READ_MEDIA_VIDEO)
//        } //Android 13 特有权限，由于编译器在sdk33无法自动补全xml代码，后续再升级As
        PermissionX.init(this)
            .permissions(requestList)
            .onExplainRequestReason { scope, deniedList ->
                val msg = "同意以下权限使用："
                scope.showRequestReasonDialog(deniedList, msg, "Allow", "Deny")
            }.request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    Toast.makeText(this, "all granted!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Deny $deniedList!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    //因为POST_NOTIFICATIONS是Android 13的新增权限，以前的系统版本是没有的，因此如果使用
    // Manifest.permission.POST_NOTIFICATIONS在Android 13以下系统可能会导致编译不过。
    private fun askNotification() {
        PermissionX.init(this)
            .permissions(PermissionX.permission.POST_NOTIFICATIONS)//,Manifest.permission.NEARBY_WIFI_DEVICES
            .onExplainRequestReason { scope, deniedList ->
                val msg = "避免遗漏重要通知，同意权限："
                scope.showRequestReasonDialog(deniedList, msg, "yes", "no")
            }.request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    Toast.makeText(this, "all granted!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Deny $deniedList!", Toast.LENGTH_SHORT).show()
                }
            }
    }
}