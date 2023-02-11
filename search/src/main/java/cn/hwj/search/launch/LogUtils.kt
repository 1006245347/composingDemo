package cn.hwj.search.launch

import android.util.Log

var openLog = true

const val TAG = "tag"


internal fun printI(key: String = TAG, message: String) {
    if (openLog) {
        Log.i(key, message)
    }
}

internal fun printV(key: String = TAG, message: String) {
    if (openLog)
        Log.v(key, message)
}

internal fun printD(key: String = TAG, message: String) {
    if (openLog)
        Log.d(key, message)
}

internal fun printW(key: String = TAG, message: String) {
    if (openLog) {
        Log.w(key, message)
    }
}

internal fun printE(key: String = TAG, message: String) {
    if (openLog) {
        Log.e(key, message)
    }
}