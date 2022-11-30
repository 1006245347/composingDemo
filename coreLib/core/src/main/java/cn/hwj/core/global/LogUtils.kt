package cn.hwj.core.global

import android.util.Log

/**
 * @author by jason-何伟杰，2022/11/25
 * des:日志打印
 */
var openLog = true

const val TAG = "TAG"

internal fun printI(message: String = "", key: String = TAG) {
    if (openLog) {
        Log.i(key, message)
    }
}

internal fun printV(message: String, key: String = TAG) {
    if (openLog)
        Log.v(key, message)
}

internal fun printD(message: String, key: String = TAG) {
    if (openLog)
        Log.d(key, message)
}

internal fun printW(message: String, key: String = TAG) {
    if (openLog) {
        Log.w(key, message)
    }
}

internal fun printE(message: String, key: String = TAG) {
    if (openLog) {
        Log.e(key, message)
    }
}