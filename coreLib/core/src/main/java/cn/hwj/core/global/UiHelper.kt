package cn.hwj.core.global

import android.app.Activity
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

object UiHelper {

    @JvmStatic
    fun switch2Aty(activity: Activity, cls: Class<*>?, isClearAll: Boolean = false) {
        try {
            GlobalScope.launch(Dispatchers.Main) {
                val intent = Intent(activity, cls)
                if (isClearAll)
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                activity.startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun switch2Aty(activity: Context, cls: Class<*>?, isClearAll: Boolean = false) {
        try {
            GlobalScope.launch(Dispatchers.Main) {
                val intent = Intent(activity, cls)
                if (isClearAll)
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                activity.startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun getContext(): Context {
        return CoreApplicationProvider.appContext
    }

    @JvmStatic
    fun getCreateDate(parttern: String = "yyyyMMdd"): String {
        val format = SimpleDateFormat(parttern)
        return format.format(Date())
    }

    @JvmStatic
    fun getVersionName(): String? {
        val packageManager: PackageManager = getContext().packageManager
        try {
            val packageInfo: PackageInfo =
                packageManager.getPackageInfo(getContext().packageName, 0)
            return packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "1.0.0"
    }

    @JvmStatic
    fun getVersionCode(): String? {
        val packageManager: PackageManager = getContext().packageManager
        try {
            val packageInfo: PackageInfo =
                packageManager.getPackageInfo(getContext().packageName, 0)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                return packageInfo.versionCode.toString()
            } else {
                return packageInfo.longVersionCode.toString()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "1"
    }

    @JvmStatic
    fun getPackageName(): String {
        val packageManager = getContext().packageManager
        val packageInfo = packageManager.getPackageInfo(getContext().packageName, 0)
        return packageInfo.packageName
    }

    /** @return 跳转应用设置界面*/
    @JvmStatic
    fun switch2SettingInfo() {
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS")
            intent.setData(Uri.fromParts("package", getPackageName(), null))
        } else {
            intent.setAction(Intent.ACTION_VIEW)
            intent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails")
            intent.putExtra("com.android.settings.ApplicationPkgName", getPackageName())
        }
        getContext().startActivity(intent)
    }

    @JvmStatic
    fun getResource(): Resources {
        return getContext().resources
    }

    @JvmStatic
    fun getString(resId: Int): String {
        return getResource().getString(resId)
    }

    //保留一位小数
    @JvmStatic
    fun getFloatStr(data: Float): String {
        return String.format("%.2f", data)
    }

    private val APP_NAME_CACHE = mutableMapOf<String, String>()
    fun getAppName(pm: PackageManager): String {
        val pInfo = pm.getPackageInfo(getContext().packageName, 0)
        if (APP_NAME_CACHE.containsKey(pInfo.packageName)) {
            return APP_NAME_CACHE.get(pInfo.packageName) + ""
        }
        val label = pInfo.applicationInfo.loadLabel(pm).toString()
        APP_NAME_CACHE[pInfo.packageName] = label
        return label
    }

    /**
     * @return 得到string.xml中的字符串，带点位符
     */
    @JvmStatic
    fun getString(id: Int, vararg formatArgs: Any?): String? {
        return getResource().getString(id, *formatArgs)
    }

    /** @return 得到颜色值 */
    @JvmStatic
    fun getColor(colorId: Int): Int {
        return ContextCompat.getColor(getContext(), colorId)
    }

    @JvmStatic
    fun hideKeyboard(context: Context, etInput: EditText?) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive) {
            imm.hideSoftInputFromWindow(etInput?.windowToken, 0)
        }
    }

    /**  应用白名单 @return <uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" />*/
    @JvmStatic
    fun switch2WhiteList() {
        val intent = Intent()
        try {
            var componentName: ComponentName? = null
            val brand = Build.BRAND
            when (brand.toLowerCase()) {
                "samsung" -> {
                    componentName = ComponentName(
                        "com.samsung.android.sm",
                        "com.samsung.android.sm.app.dashboard.SmartManagerDashBoardActivity"
                    )
                }
                "huawei", "honor" -> {
                    componentName = ComponentName(
                        "com.huawei.systemmanager",
                        "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"
                        //com.huawei.systemmanager.optimize.bootstart.BootStartActivity
                    )
                }
                "xiaomi" -> {
                    componentName = ComponentName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity"
                    )
                }
                "vivo" -> {
                    componentName = ComponentName(
                        "com.iqoo.secure",
                        "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"
                    )
                }
                "oppo" -> {
                    componentName = ComponentName(
                        "com.coloros.oppoguardelf",
                        "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity"
                    )
                }
                "360" -> {
                    componentName = ComponentName(
                        "com.yulong.android.coolsafe",
                        "com.yulong.android.coolsafe.ui.activity.autorun.AutoRunListActivity"
                    )
                }
                "meizu" -> {
                    componentName =
                        ComponentName("com.meizu.safe", "com.meizu.safe.permission.SmartBGActivity")
                }
                "oneplus" -> {
                    componentName = ComponentName(
                        "com.oneplus.security",
                        "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity"
                    )
                }
                "ulong" -> {    //360未测
                    componentName = ComponentName(
                        "com.yulong.android.coolsafe",
                        ".ui.activity.autorun.AutoRunListActivity"
                    )
                }
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (null != componentName) {
                intent.component = componentName
            } else {
                intent.action = Settings.ACTION_SETTINGS
            }
        } catch (e: Exception) {
            intent.action = Settings.ACTION_SETTINGS
        } finally {
            getContext().startActivity(intent)
        }
    }

    /** @return 开启通知管理*/
    @JvmStatic
    fun checkNotifyPermission(context: Context) {
        val isOpen = NotificationManagerCompat.from(context).areNotificationsEnabled()
        if (!isOpen) {
            AlertDialog.Builder(context)
                .setNegativeButton("取消") { _, _ -> "" }
                .setPositiveButton("去开启") { _, _ ->
                    when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                            val intent = Intent()
                            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName()) //8.0
                            intent.putExtra(Settings.EXTRA_CHANNEL_ID, context.applicationInfo.uid)
                            context.startActivity(intent)
                        }
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                            val intent = Intent()
                            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                            intent.putExtra("app_package", getPackageName()) //7.0
                            intent.putExtra("app_uid", context.applicationInfo.uid)
                            context.startActivity(intent)
                        }
                        Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT -> {
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            intent.addCategory(Intent.CATEGORY_DEFAULT)
                            intent.data = Uri.parse("package:${getPackageName()}")
                            context.startActivity(intent)
                        }
                    }
                }
                .setCancelable(true)
                .setMessage("开启通知服务").show()
        }
    }

    @JvmStatic
    fun showTip(c: Context, txt: String, run: Runnable? = null, cancelRun: Runnable? = null) {
        AlertDialog.Builder(c)
            .setMessage(txt + "")
            .setNegativeButton(
                "取消"
            ) { dialog, which ->
                cancelRun?.run()
                dialog?.dismiss()
            }.setPositiveButton(
                "确定"
            ) { dialog, which ->
                run?.run()
                dialog?.dismiss()
            }.show()
    }
}