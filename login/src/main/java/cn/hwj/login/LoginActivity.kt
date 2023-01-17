package cn.hwj.login

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import cn.hwj.core.CoreUtils
import cn.hwj.core.global.*
import cn.hwj.route.RoutePath
import com.didi.drouter.annotation.Router
import com.didi.drouter.api.DRouter
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
            launchAty()
        }
        tvInfo.setOnClickListener {
            askNotification()
            MMKVUtils.addBool("c", true)
            printV(
                "${MMKVUtils.getBool("c")} " +
                        "${MMKVUtils.getStr("S", "gg")} " +
                        "${MMKVUtils.hasKey("h")}"
            )
        }
        askPermission()

    }

    private fun mockLogin() {
        val user = "user info"
        MMKVUtils.setSavePath(CoreApplicationProvider.getGlobalDir(), "login")
//        val user = UserBean("jason", "13232508893")
        MMKVUtils.addStr("user", user)
        printD("local=${MMKVUtils.getStr("user")}")
    }

    private fun appendTxt(news: String): String {
        val txt = tvInfo.text
        val stringBuilder = StringBuilder()
            .append(txt)
            .append(news)
        tvInfo.text = stringBuilder.toString()
        return stringBuilder.toString()
    }

    private fun launchAty() {
        DRouter.build(RoutePath.SEARCH_ACTIVITY_INPUT)
            .start()
    }

    private fun askPermission() {
        val requestList = ArrayList<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestList.add(Manifest.permission.READ_MEDIA_IMAGES)
            requestList.add(Manifest.permission.READ_MEDIA_AUDIO)
            requestList.add(Manifest.permission.READ_MEDIA_VIDEO)
        } else { //Android 13 特有权限，由于编译器在sdk33无法自动补全xml代码，后续再升级As
            requestList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                mockLogin()
            }else {
                val intent = Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                val startActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                    if (Environment.isExternalStorageManager()) {
                        mockLogin()
                    } else {
                        Toast.makeText(this, "存储权限获取失败", Toast.LENGTH_SHORT).show()
                    }
                }
                startActivity.launch(intent)
            }
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionX.init(this)
                .permissions(requestList)
                .onExplainRequestReason { scope, deniedList ->
                    val msg = "同意以下权限使用："
                    scope.showRequestReasonDialog(deniedList, msg, "Allow", "Deny")
                }.request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        Toast.makeText(this, "all granted!", Toast.LENGTH_SHORT).show()
                        mockLogin()
                    } else {
                        Toast.makeText(this, "Deny $deniedList!", Toast.LENGTH_SHORT).show()
                    }
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