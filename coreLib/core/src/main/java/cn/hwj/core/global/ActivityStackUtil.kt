package cn.hwj.core.global

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.system.exitProcess

/**
 * @author jason-何伟杰，2020-01-05
 * des:应用中所有Activity的管理器，可用于一键杀死所有Activity。
 */
object ActivityStackUtil {
    private val activityStack = Stack<Activity>()

    @JvmStatic
    fun add(weakRefActivity: Activity?) {
        weakRefActivity?.let {
            activityStack.push(it)
        }
    }

    @JvmStatic
    fun del(weakRefActivity: Activity?) {
        weakRefActivity?.let {
            //应该是先把Stack的内容清掉，再finish；假如先finish,那么存在Stack的Aty还能正常finish?
            activityStack.remove(it)
            it.finish() //这个需要吗,还有执行顺序有无泄漏
        }
    }

    @JvmStatic
    fun getCurAty(): AppCompatActivity {
        return activityStack.lastElement() as AppCompatActivity
    }

    //关闭其他Activity
    @JvmStatic
    fun <T> finishOther(cls: Class<T>) {
        for (aty in activityStack) {
            if (aty::class.java != cls) {
                del(aty)
                break
            }
        }
    }

    @JvmStatic
    fun <T> findAty(cls: Class<T>): Activity? {
        for (aty in activityStack) {
            if (aty::class.java == cls) {
                return aty
            }
        }
        return null
    }

    @JvmStatic
    fun <T> finishActivity(weakRefActivity: Activity) {
        if (activityStack.size > 0 && activityStack.search(weakRefActivity) != -1) {
            del(weakRefActivity)
        }
    }

    @JvmStatic
    fun clearAllActivity() {
        if (activityStack.isNotEmpty()) {
            while (!activityStack.empty()) {
                activityStack.pop()?.finish()
            }
        }
    }

    @JvmStatic
    fun exitApp() {
        clearAllActivity()
        exitProcess(0)
    }

    @JvmStatic
    fun getStackInfo(): String {
        return activityStack.toString()
    }
}