package cn.hwj.core.global

import cn.hwj.core.CoreUtils
import com.tencent.mmkv.MMKV

/**
 * @author jason-何伟杰，2020-01-05
 * des:mmkv管理类
 */
object MMKVUtil {

    private var kv: MMKV? = null
    private var savePath: String? = null

    /**在Application可设定保存路径*/
    fun setSavePath(path: String) {
        savePath = path
    }

    //建造者模式
    class Builder {
        var savePath: String? = null
        fun setSavePath(savePath: String?): Builder {
            this.savePath = savePath
            return this
        }

        fun build() {
            return MMKVUtil(this)
        }
    }

    private fun MMKVUtil(builder: Builder) {
        savePath = builder.savePath
        init(savePath + "")
    }

    fun getKv(): MMKV? {
        if (null == kv) {
            init(savePath + "")
        }
        return kv
    }

    fun init(savePath: String) {
        /*使用默认存储路径*/
        if (savePath.isNullOrEmpty()) {
            MMKV.initialize(CoreUtils.getContext())
            kv = MMKV.defaultMMKV()
        } else {
            /*自定义存储路径*/
            MMKV.initialize(savePath)
            kv = MMKV.mmkvWithID(savePath)
        }
    }

//    /**直接保存自定义实体对象*/
//    @JvmStatic
//    fun addObj(key: String, obj: Any) {
//        val gson = Gson()
//        val json = gson.toJson(obj)
//        addStr(key, json)
//    }
//
//    @JvmStatic
//    fun <T> getObj(key: String, cls: Class<T>): T? {
//        val json = getStr(key)
////        GlobalCode.printLog("obj:$json")
//        if (json.isNullOrEmpty()) return null
//        val gson = Gson()
//        var t: T
//        try {
//            t = gson.fromJson<T>(json, cls)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            remove(key)
//            return null
//        }
//        return t
//    }

    /**是否存在*/
    @JvmStatic
    fun hasKey(key: String): Boolean? {
        return getKv()?.containsKey(key)
    }

    /**删除某个key*/
    @JvmStatic
    fun remove(key: String) {
        getKv()?.remove(key)
    }

    /**清除所有存储*/
    @JvmStatic
    fun clear() {
        getKv()?.clearAll()
    }

    @JvmStatic
    fun addStr(key: String, value: String): Boolean? {
        return getKv()?.encode(key, value)
    }

    @JvmStatic
    fun addBool(key: String, flag: Boolean): Boolean? {
        return getKv()?.encode(key, flag)
    }

    @JvmStatic
    fun addInteger(key: String, i: Int): Boolean? {
        return getKv()?.encode(key, i)
    }

    @JvmStatic
    fun addDouble(key: String, v: Double): Boolean? {
        return getKv()?.encode(key, v)
    }

    @JvmStatic
    fun addLong(key: String, l: Long): Boolean? {
        return getKv()?.encode(key, l)
    }

    @JvmStatic
    fun addFloat(key: String, f: Float): Boolean? {
        return getKv()?.encode(key, f)
    }

    @JvmStatic
    fun getStr(key: String): String? {
        return getKv()?.decodeString(key)
    }

    @JvmStatic
    fun getStr(key: String, def: String): String? {
        return getKv()?.decodeString(key, def)
    }

    @JvmStatic
    fun getBool(key: String): Boolean? {
        return getKv()?.decodeBool(key)
    }

    @JvmStatic
    fun getBool(key: String, def: Boolean): Boolean? {
        return getKv()?.decodeBool(key, def)
    }

    @JvmStatic
    fun getInteger(key: String): Int? {
        return getKv()?.decodeInt(key)
    }

    @JvmStatic
    fun getInteger(key: String, def: Int): Int? {
        return getKv()?.decodeInt(key, def)
    }

    @JvmStatic
    fun getDouble(key: String): Double? {
        return getKv()?.decodeDouble(key)
    }

    @JvmStatic
    fun getDouble(key: String, def: Double): Double? {
        return getKv()?.decodeDouble(key, def)
    }

    @JvmStatic
    fun getFloat(key: String): Float? {
        return getKv()?.decodeFloat(key)
    }

    @JvmStatic
    fun getFloat(key: String, def: Float): Float? {
        return getKv()?.decodeFloat(key, def)
    }

    @JvmStatic
    fun getLong(key: String): Long? {
        return getKv()?.decodeLong(key)
    }

    @JvmStatic
    fun getLong(key: String, def: Long): Long? {
        return getKv()?.decodeLong(key, def)
    }
}