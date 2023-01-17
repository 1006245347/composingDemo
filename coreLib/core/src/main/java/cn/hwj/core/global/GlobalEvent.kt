package cn.hwj.core.global

/**
 * @author by jason-何伟杰，2023/1/16
 * des:组件间通用的事件，适用Eventbus
 */
class GlobalEvent {
    lateinit var key: String
    var value: String? = null

    constructor(key: String, value: String?) : this(key) {
        this.key = key
        this.value = value
    }

    constructor(key: String) {
        this.key = key
        this.value = null
    }
}