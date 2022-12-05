package cn.hwj.push

import android.app.Notification
import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.SparseArray
import cn.jpush.android.api.BasicPushNotificationBuilder
import cn.jpush.android.api.JPushInterface
import cn.jpush.android.api.JPushMessage
import java.util.*

class PushHelper private constructor() {
    private var context: Context? = null
    fun init(context: Context?) {
        if (context != null) {
            this.context = context.applicationContext
        }
    }

    var pushListener: PushListener? = null
    fun callbackReceive(s: String) {
        pushListener?.handlePushMsg(s)
    }

    private val setActionCache = SparseArray<Any>()
    operator fun get(sequence: Int): Any {
        return setActionCache[sequence]
    }

    fun remove(sequence: Int): Any {
        return setActionCache[sequence]
    }

    fun put(sequence: Int, tagAliasBean: Any) {
        setActionCache.put(sequence, tagAliasBean)
    }

    private val delaySendHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                DELAY_SEND_ACTION -> if (msg.obj != null && msg.obj is TagAliasBean) {
                    sequence++
                    val tagAliasBean = msg.obj as TagAliasBean
                    setActionCache.put(sequence, tagAliasBean)
                    if (context != null) {
                        handleAction(context, sequence, tagAliasBean)
                    } else {
                        Logger.e(TAG, "#unexcepted - context was null")
                    }
                } else {
                    Logger.w(TAG, "#unexcepted - msg obj was incorrect")
                }
                DELAY_SET_MOBILE_NUMBER_ACTION -> if (msg.obj != null && msg.obj is String) {
                    Logger.i(TAG, "retry set mobile number")
                    sequence++
                    val mobileNumber = msg.obj as String
                    setActionCache.put(sequence, mobileNumber)
                    if (context != null) {
                        handleAction(context, sequence, mobileNumber)
                    } else {
                        Logger.e(TAG, "#unexcepted - context was null")
                    }
                } else {
                    Logger.w(TAG, "#unexcepted - msg obj was incorrect")
                }
            }
        }
    }

    fun handleAction(context: Context?, sequence: Int, mobileNumber: String) {
        put(sequence, mobileNumber)
        Logger.d(
            TAG,
            "sequence:$sequence,mobileNumber:$mobileNumber"
        )
        JPushInterface.setMobileNumber(context, sequence, mobileNumber)
    }

    /**
     * 处理设置tag
     */
    fun handleAction(context: Context?, sequence: Int, tagAliasBean: TagAliasBean?) {
        init(context)
        if (tagAliasBean == null) {
            Logger.w(TAG, "tagAliasBean was null")
            return
        }
        put(sequence, tagAliasBean)
        if (tagAliasBean.isAliasAction) {
            when (tagAliasBean.action) {
                ACTION_GET -> JPushInterface.getAlias(context, sequence)
                ACTION_DELETE -> JPushInterface.deleteAlias(context, sequence)
                ACTION_SET -> JPushInterface.setAlias(context, sequence, tagAliasBean.alias)
                else -> {
                    Logger.w(TAG, "unSupport alias action type")
                    return
                }
            }
        } else {
            when (tagAliasBean.action) {
                ACTION_ADD -> JPushInterface.addTags(context, sequence, tagAliasBean.tags)
                ACTION_SET -> JPushInterface.setTags(context, sequence, tagAliasBean.tags)
                ACTION_DELETE -> JPushInterface.deleteTags(context, sequence, tagAliasBean.tags)
                ACTION_CHECK -> {
                    //一次只能check一个tag
                    JPushInterface.checkTagBindState(
                        context, sequence,
                        tagAliasBean.tags!!.toTypedArray()[0]
                    )
                }
                ACTION_GET -> JPushInterface.getAllTags(context, sequence)
                ACTION_CLEAN -> JPushInterface.cleanTags(context, sequence)
                else -> {
                    Logger.w(TAG, "unSupport tag action type")
                    return
                }
            }
        }
    }

    private fun retryActionIfNeeded(errorCode: Int, tagAliasBean: TagAliasBean?): Boolean {
        //返回的错误码为6002 超时,6014 服务器繁忙,都建议延迟重试
        if (errorCode == 6002 || errorCode == 6014) {
            Logger.d(TAG, "need retry")
            if (tagAliasBean != null) {
                val message = Message()
                message.what = DELAY_SEND_ACTION
                message.obj = tagAliasBean
                delaySendHandler.sendMessageDelayed(message, (1000 * 60).toLong())
                val logs = getRetryStr(tagAliasBean.isAliasAction, tagAliasBean.action, errorCode)
                return true
            }
        }
        return false
    }

    private fun retrySetMobileNumberActionIfNeeded(errorCode: Int, mobileNumber: String): Boolean {
        //返回的错误码为6002 超时,6024 服务器内部错误,建议稍后重试
        if (errorCode == 6002 || errorCode == 6024) {
            Logger.d(TAG, "need retry")
            val message = Message()
            message.what = DELAY_SET_MOBILE_NUMBER_ACTION
            message.obj = mobileNumber
            delaySendHandler.sendMessageDelayed(message, (1000 * 60).toLong())
            var str = "Failed to set mobile number due to %s. Try again after 60s."
            str = String.format(
                Locale.ENGLISH, str,
                if (errorCode == 6002) "timeout" else "server internal error”"
            )
            return true
        }
        return false
    }

    private fun getRetryStr(isAliasAction: Boolean, actionType: Int, errorCode: Int): String {
        var str = "Failed to %s %s due to %s. Try again after 60s."
        str = String.format(
            Locale.ENGLISH,
            str,
            getActionStr(actionType),
            if (isAliasAction) "alias" else " tags",
            if (errorCode == 6002) "timeout" else "server too busy"
        )
        return str
    }

    private fun getActionStr(actionType: Int): String {
        when (actionType) {
            ACTION_ADD -> return "add"
            ACTION_SET -> return "set"
            ACTION_DELETE -> return "delete"
            ACTION_GET -> return "get"
            ACTION_CLEAN -> return "clean"
            ACTION_CHECK -> return "check"
        }
        return "unkonw operation"
    }

    fun onTagOperatorResult(context: Context?, jPushMessage: JPushMessage) {
        val sequence: Int = jPushMessage.getSequence()
        Logger.i(
            TAG,
            "action - onTagOperatorResult, sequence:" + sequence + ",tags:" + jPushMessage.getTags()
        )
        init(context)
        //根据sequence从之前操作缓存中获取缓存记录
        val tagAliasBean = setActionCache[sequence] as TagAliasBean
        if (tagAliasBean == null) {
            return
        }
        if (jPushMessage.getErrorCode() === 0) {
            Logger.i(
                TAG,
                "action - modify tag Success,sequence:$sequence"
            )
            setActionCache.remove(sequence)
            val logs = getActionStr(tagAliasBean.action) + " tags success"
            Logger.i(TAG, logs)
        } else {
            var logs = "Failed to " + getActionStr(tagAliasBean.action) + " tags"
            if (jPushMessage.getErrorCode() === 6018) {
                //tag数量超过限制,需要先清除一部分再add
                logs += ", tags is exceed limit need to clean"
            }
            logs += ", errorCode:" + jPushMessage.getErrorCode()
            Logger.e(TAG, logs)
            if (!retryActionIfNeeded(jPushMessage.errorCode, tagAliasBean)) {

            }
        }
    }

    fun onCheckTagOperatorResult(context: Context?, jPushMessage: JPushMessage) {
        val sequence: Int = jPushMessage.getSequence()
        Logger.i(
            TAG,
            "action - onCheckTagOperatorResult, sequence:" + sequence + ",checktag:" + jPushMessage.getCheckTag()
        )
        init(context)
        //根据sequence从之前操作缓存中获取缓存记录
        val tagAliasBean = setActionCache[sequence] as TagAliasBean
        if (tagAliasBean == null) {
            return
        }
        if (jPushMessage.getErrorCode() === 0) {
            Logger.i(TAG, "tagBean:$tagAliasBean")
            setActionCache.remove(sequence)
            val logs =
                getActionStr(tagAliasBean.action) + " tag " + jPushMessage.getCheckTag() + " bind state success,state:" + jPushMessage.getTagCheckStateResult()
            Logger.i(TAG, logs)
        } else {
            val logs =
                "Failed to " + getActionStr(tagAliasBean.action) + " tags, errorCode:" + jPushMessage.getErrorCode()
            Logger.e(TAG, logs)

        }
    }

    fun onAliasOperatorResult(context: Context?, jPushMessage: JPushMessage) {
        val sequence: Int = jPushMessage.getSequence()
        Logger.i(
            TAG,
            "action - onAliasOperatorResult, sequence:" + sequence + ",alias:" + jPushMessage.getAlias()
        )
        init(context)
        //根据sequence从之前操作缓存中获取缓存记录
        val tagAliasBean = setActionCache[sequence] as TagAliasBean
        if (tagAliasBean == null) {
            return
        }
        if (jPushMessage.getErrorCode() === 0) {
            Logger.i(
                TAG,
                "action - modify alias Success,sequence:$sequence"
            )
            setActionCache.remove(sequence)
            val logs = getActionStr(tagAliasBean.action) + " alias success"
            Logger.i(TAG, logs)
        } else {
            val logs =
                "Failed to " + getActionStr(tagAliasBean.action) + " alias, errorCode:" + jPushMessage.getErrorCode()
            Logger.e(TAG, logs)
        }
    }

    //设置手机号码回调
    fun onMobileNumberOperatorResult(context: Context?, jPushMessage: JPushMessage) {
        val sequence: Int = jPushMessage.getSequence()
        Logger.i(
            TAG,
            "action - onMobileNumberOperatorResult, sequence:" + sequence + ",mobileNumber:" + jPushMessage.getMobileNumber()
        )
        init(context)
        if (jPushMessage.getErrorCode() === 0) {
            Logger.i(
                TAG,
                "action - set mobile number Success,sequence:$sequence"
            )
            setActionCache.remove(sequence)
        } else {
            val logs = "Failed to set mobile number, errorCode:" + jPushMessage.getErrorCode()
            Logger.e(TAG, logs)
            if (!retrySetMobileNumberActionIfNeeded(
                    jPushMessage.errorCode,
                    jPushMessage.mobileNumber
                )
            ) {
            }
        }
    }

    class TagAliasBean {
        var action = 0
        var tags: Set<String>? = null
        var alias: String? = null
        var isAliasAction = false
        override fun toString(): String {
            return "TagAliasBean{" +
                    "action=" + action +
                    ", tags=" + tags +
                    ", alias='" + alias + '\'' +
                    ", isAliasAction=" + isAliasAction +
                    '}'
        }
    }

    companion object {
        private const val TAG = "JPush"
        var sequence = 1

        /**
         * 增加
         */
        const val ACTION_ADD = 1

        /**
         * 覆盖
         */
        const val ACTION_SET = 2

        /**
         * 删除部分
         */
        const val ACTION_DELETE = 3

        /**
         * 删除所有
         */
        const val ACTION_CLEAN = 4

        /**
         * 查询
         */
        const val ACTION_GET = 5
        const val ACTION_CHECK = 6
        const val DELAY_SEND_ACTION = 1
        const val DELAY_SET_MOBILE_NUMBER_ACTION = 2
        private var mInstance: PushHelper? = null
        val instance: PushHelper?
            get() {
                if (mInstance == null) {
                    synchronized(PushHelper::class.java) {
                        if (mInstance == null) {
                            mInstance = PushHelper()
                        }
                    }
                }
                return mInstance
            }

        fun setPushDebug(isDebug: Boolean = true) {
            JPushInterface.setDebugMode(isDebug)
        }

        fun initPushArg(context: Context?, uniqueId: String?) {
            JPushInterface.init(context)
            //通知栏基础属性
            val builder = BasicPushNotificationBuilder(context)
            //        builder.statusBarDrawable = R.mipmap.ic_launcher;
//        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为点击后自动消失
            builder.notificationDefaults =
                Notification.DEFAULT_VIBRATE //设置为铃声（ Notification.DEFAULT_SOUND）或者震动（ Notification.DEFAULT_VIBRATE）
            JPushInterface.setPushNotificationBuilder(0, builder) //1-->0
            JPushInterface.setLatestNotificationNumber(context, 3) //通知栏只保持最多3条
            //1.jpush必须登录和初始化成功
            //2.设置相应的别名或标签 别名-一个用户只有一个（不同用户可以相同别名）
            //  标签-标识一群人
            //3.设置自定义的通知样式 默认通知栏样式编号=0，这里覆盖0

            //设置别名-标签
            var action = -1
            if (uniqueId == null) return
            val tagAliasBean = TagAliasBean()
            action = ACTION_SET
            tagAliasBean.action = action
            sequence++
            tagAliasBean.alias = uniqueId
            tagAliasBean.isAliasAction = true
            instance!!.handleAction(
                context,
                sequence,
                tagAliasBean
            )

            //2.标签-一步 a.add-新增 b.set-覆盖
            val tag: String = uniqueId
            action = ACTION_SET
            val tagAliasBean_set = TagAliasBean()
            tagAliasBean_set.action = action
            sequence++
            val sArray = tag.split(",").toTypedArray()
            val tagSet: MutableSet<String> =
                LinkedHashSet()
            for (sTagItem in sArray) {
                if (!ExampleUtil.isValidTagAndAlias(sTagItem)) {
                    //格式出错
                    break
                }
                tagSet.add(sTagItem)
            }
            if (!tagSet.isEmpty()) {
                tagAliasBean_set.tags = tagSet
                tagAliasBean_set.isAliasAction = false
                instance!!.handleAction(
                    context,
                    sequence,
                    tagAliasBean_set
                )
            }
        }
    }
}