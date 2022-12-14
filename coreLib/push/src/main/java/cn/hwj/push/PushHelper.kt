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
     * ????????????tag
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
                    //????????????check??????tag
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
        //?????????????????????6002 ??????,6014 ???????????????,?????????????????????
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
        //?????????????????????6002 ??????,6024 ?????????????????????,??????????????????
        if (errorCode == 6002 || errorCode == 6024) {
            Logger.d(TAG, "need retry")
            val message = Message()
            message.what = DELAY_SET_MOBILE_NUMBER_ACTION
            message.obj = mobileNumber
            delaySendHandler.sendMessageDelayed(message, (1000 * 60).toLong())
            var str = "Failed to set mobile number due to %s. Try again after 60s."
            str = String.format(
                Locale.ENGLISH, str,
                if (errorCode == 6002) "timeout" else "server internal error???"
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
        //??????sequence??????????????????????????????????????????
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
                //tag??????????????????,???????????????????????????add
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
        //??????sequence??????????????????????????????????????????
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
        //??????sequence??????????????????????????????????????????
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

    //????????????????????????
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
         * ??????
         */
        const val ACTION_ADD = 1

        /**
         * ??????
         */
        const val ACTION_SET = 2

        /**
         * ????????????
         */
        const val ACTION_DELETE = 3

        /**
         * ????????????
         */
        const val ACTION_CLEAN = 4

        /**
         * ??????
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
            //?????????????????????
            val builder = BasicPushNotificationBuilder(context)
            //        builder.statusBarDrawable = R.mipmap.ic_launcher;
//        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //??????????????????????????????
            builder.notificationDefaults =
                Notification.DEFAULT_VIBRATE //?????????????????? Notification.DEFAULT_SOUND?????????????????? Notification.DEFAULT_VIBRATE???
            JPushInterface.setPushNotificationBuilder(0, builder) //1-->0
            JPushInterface.setLatestNotificationNumber(context, 3) //????????????????????????3???
            //1.jpush??????????????????????????????
            //2.?????????????????????????????? ??????-????????????????????????????????????????????????????????????
            //  ??????-???????????????
            //3.?????????????????????????????? ???????????????????????????=0???????????????0

            //????????????-??????
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

            //2.??????-?????? a.add-?????? b.set-??????
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
                    //????????????
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