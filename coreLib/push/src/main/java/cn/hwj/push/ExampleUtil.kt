package cn.hwj.push

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.TextUtils
import java.util.regex.Pattern

object ExampleUtil {
    const val KEY_APP_KEY = "JPUSH_APPKEY"
    fun isEmpty(s: String?): Boolean {
        if (null == s) return true
        if (s.length == 0) return true
        return if (s.trim { it <= ' ' }.length == 0) true else false
    }

    /**
     * 只能以 “+” 或者 数字开头；后面的内容只能包含 “-” 和 数字。
     */
    private const val MOBILE_NUMBER_CHARS = "^[+0-9][-0-9]{1,}$"
    fun isValidMobileNumber(s: String?): Boolean {
        if (TextUtils.isEmpty(s)) return true
        val p = Pattern.compile(MOBILE_NUMBER_CHARS)
        val m = p.matcher(s)
        return m.matches()
    }

    // 校验Tag Alias 只能是数字,英文字母和中文
    fun isValidTagAndAlias(s: String?): Boolean {
        val p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_!@#$&*+=.|]+$")
        val m = p.matcher(s)
        return m.matches()
    }

    // 取得AppKey
    fun getAppKey(context: Context): String? {
        var metaData: Bundle? = null
        var appKey: String? = null
        try {
            val ai = context.packageManager.getApplicationInfo(
                context.packageName, PackageManager.GET_META_DATA
            )
            if (null != ai) metaData = ai.metaData
            if (null != metaData) {
                appKey = metaData.getString(KEY_APP_KEY)
                if (null == appKey || appKey.length != 24) {
                    appKey = null
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return appKey
    }
}
