package cn.hwj.search.launch

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * @author by jason-何伟杰，2023/2/10
 * des:广播接受者
 */
class StateBroadReceived : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        p1?.let {
            if (it.action == "first") {
                printD(message = "11>$p0 ")
            } else if (it.action == "second") {
                printD(message = "22>$p0 ")
            } else if (it.action == "third") {
                printD(message = "33>$p0 ")
            } else if (it.action == "login") {
                printD(message = "44>$p0 ")
            } else if (it.action == "news") {
                printD(message = "55>$p0 ")
            } else {
                printD(message = "66>>$p0")
            }
            val cls = p1.getStringExtra("cls")
            printD(message = " ${ActivityStackUtil.findAty(LoginActivity::class.java)}")
        }
    }
}