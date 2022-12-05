package cn.hwj.push

import android.content.Context
import android.content.Intent
import android.util.Log
import cn.jpush.android.api.CustomMessage
import cn.jpush.android.api.JPushInterface
import cn.jpush.android.api.JPushMessage
import cn.jpush.android.api.NotificationMessage
import cn.jpush.android.service.JPushMessageReceiver
import org.json.JSONObject

/**
 * @author by jason-何伟杰，2022/12/2
 * des:收到自定义消息的处理
 */
class PushMessageReceiver : JPushMessageReceiver() {

    /*统一处理解析消息体,然后通过广播发送出去*/
    private fun parsePushEvent(context: Context, customMessage: CustomMessage) {
        try {
            Log.v("TAG", "CTX=${context} $customMessage")
//            val jsonObject = JSONObject(customMessage.message)
            PushHelper.instance?.callbackReceive("$customMessage")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onMessage(context: Context, customMessage: CustomMessage) {
        parsePushEvent(context, customMessage)
    }

    override fun onNotifyMessageOpened(context: Context?, p1: NotificationMessage?) {
//        try {
//            //打开自定义的Activity
//            val i = Intent(context, LaunchAty::class.java)
//            val bundle = Bundle()
//            bundle.putString(JPushInterface.EXTRA_NOTIFICATION_TITLE, message.notificationTitle)
//            bundle.putString(JPushInterface.EXTRA_ALERT, message.notificationContent)
//            i.putExtras(bundle)
//            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//            context!!.startActivity(i)
//        } catch (throwable: Throwable) {
//        }
    }

    override fun onNotifyMessageArrived(context: Context, message: NotificationMessage) {
        Logger.v("TAG","$message")
        var json: JSONObject?
        try {
            json = JSONObject(message.notificationContent.toString())
            val it = json.keys()
            val sb = StringBuilder()
            while (it.hasNext()) {
                val myKey = it.next()
                sb.append(
                    "\nkey:" + ", value: [" +
                            myKey + " - " + json.optString(myKey) + "]"
                )
            }
            val customMessage = CustomMessage()
            customMessage.message = (JSONObject(message.notificationContent).getString("message"))
            parsePushEvent(context, customMessage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onTagOperatorResult(context: Context?, jPushMessage: JPushMessage) {
        PushHelper.instance?.onTagOperatorResult(context, jPushMessage)
        super.onTagOperatorResult(context, jPushMessage)
    }

    override fun onCheckTagOperatorResult(context: Context?, jPushMessage: JPushMessage) {
        PushHelper.instance?.onCheckTagOperatorResult(context, jPushMessage)
        super.onCheckTagOperatorResult(context, jPushMessage)
    }

    override fun onMobileNumberOperatorResult(context: Context?, jPushMessage: JPushMessage) {
        PushHelper.instance?.onMobileNumberOperatorResult(context, jPushMessage)
        super.onMobileNumberOperatorResult(context, jPushMessage)
    }

    override fun onAliasOperatorResult(context: Context?, jPushMessage: JPushMessage) {
        PushHelper.instance?.onAliasOperatorResult(context,jPushMessage)
        super.onAliasOperatorResult(context, jPushMessage)
    }

    override fun onMultiActionClicked(context: Context?, intent: Intent) {
        var nActionExtra: String? =
            intent.getExtras()?.getString(JPushInterface.EXTRA_NOTIFICATION_ACTION_EXTRA)

        //开发者根据不同 Action 携带的 extra 字段来分配不同的动作。
        if (nActionExtra == null) {
            Log.d(
                "TAG",
                "ACTION_NOTIFICATION_CLICK_ACTION nActionExtra is null"
            )
            return
        }
        if (nActionExtra == "my_extra1") {
            Log.e(
                "TAG",
                "[onMultiActionClicked] 用户点击通知栏按钮一"
            )
        } else if (nActionExtra == "my_extra2") {
            Log.e(
                "TAG",
                "[onMultiActionClicked] 用户点击通知栏按钮二"
            )
        } else if (nActionExtra == "my_extra3") {
            Log.e(
                "TAG",
                "[onMultiActionClicked] 用户点击通知栏按钮三"
            )
        } else {
            Log.e(
                "TAG",
                "[onMultiActionClicked] 用户点击通知栏按钮未定义"
            )
        }
    }

}