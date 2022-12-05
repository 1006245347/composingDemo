package cn.hwj.push

import android.util.Log

internal object Logger {
    //设为false关闭日志
    private const val LOG_ENABLE = false
    fun i(tag: String?, msg: String?) {
        if (LOG_ENABLE) {
            Log.i(tag, msg!!)
        }
    }

    fun v(tag: String?, msg: String?) {
        if (LOG_ENABLE) {
            Log.v(tag, msg!!)
        }
    }

    fun d(tag: String?, msg: String?) {
        if (LOG_ENABLE) {
            Log.d(tag, msg!!)
        }
    }

    fun w(tag: String?, msg: String?) {
        if (LOG_ENABLE) {
            Log.w(tag, msg!!)
        }
    }

    fun e(tag: String?, msg: String?) {
        if (LOG_ENABLE) {
            Log.e(tag, msg!!)
        }
    }
}