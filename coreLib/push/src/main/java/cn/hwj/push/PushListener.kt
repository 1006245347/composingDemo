package cn.hwj.push

interface PushListener {
    fun handlePushMsg(msg:String)
}