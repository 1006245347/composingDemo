package cn.hwj.core.global

import android.util.Log

/**
 * @author by jason-何伟杰，2022/11/25
 * des:日志打印
 */
internal var openLog = true

const val TAG = "TAG"

fun setLogEnable(enable: Boolean = true) {
    openLog = enable
}

fun printI(message: String = "", key: String = TAG) {
    if (openLog && !isEmpty(message)) {
        Log.i(key, message)
    }
}

fun printV(message: String?, key: String = TAG) {
    if (openLog && !isEmpty(message))
        Log.v(key, message!!)
}

fun printD(message: String, key: String = TAG) {
    if (openLog && !isEmpty(message))
        Log.d(key, message)
}

fun printW(message: String, key: String = TAG) {
    if (openLog && !isEmpty(message)) {
        Log.w(key, message)
    }
}

fun printE(message: String, key: String = TAG) {
    if (openLog && !isEmpty(message)) {
        Log.e(key, message)
    }
}

//严格判空
fun isEmpty(s: String?): Boolean {
    if (null == s) {
        println("LogUtils_txt is null>>>")
        return true
    }
    if (s.isEmpty()) {
        println("LogUtils_txt is null>>>")
        return true
    }
    if (s.trim { it <= ' ' }.isEmpty()) {
        println("LogUtils_txt is ' '>>>")
        return true
    }
    return false
}