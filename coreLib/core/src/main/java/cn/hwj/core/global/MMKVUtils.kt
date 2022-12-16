package cn.hwj.core.global

import cn.hwj.core.CoreUtils
import com.tencent.mmkv.MMKV

/**
 * @author by jason-何伟杰，2022/12/15
 * des:
 */
class MMKVUtils {

    private fun initKv(path: String? = null, mode: Int = MMKV.SINGLE_PROCESS_MODE) {
        kv = if (path.isNullOrEmpty()) {
            MMKV.initialize(CoreUtils.getContext())
            MMKV.defaultMMKV()
        } else {//自定义根目录
            MMKV.initialize(CoreUtils.getContext(), path)  //设置储存路径
            MMKV.mmkvWithID("config_cache", mode) //设置存储文件名
        }
    }

    companion object {
        private var kv: MMKV? = null

        /*单例*/
        val INSTANCE: MMKVUtils by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            MMKVUtils()
        }

        /*设置存储目录，这样会重设个kv对象，每个kv是对应设置的路径，如果是业务区别目录存储那么要多次调佣该方法切换*/
        fun setSavePath(path: String?, mode: Int = MMKV.SINGLE_PROCESS_MODE): MMKVUtils {
            INSTANCE.initKv(path, mode)
            return INSTANCE
        }

        private fun getKv(): MMKV {
            if (null == kv) INSTANCE.initKv()
            return kv!!
        }

        /**是否存在*/
        fun hasKey(key: String): Boolean {
            return getKv().containsKey(key)
        }

        /**删除某个key*/
        fun removeValue(key: String) {
            getKv().remove(key)
        }

        /**清除所有存储*/
        fun clearCache() {
            getKv().clearAll()
        }

        fun addStr(key: String, value: String?): Boolean {
            return getKv().encode(key, value)
        }

        fun addBool(key: String, flag: Boolean): Boolean {
            return getKv().encode(key, flag)
        }

        fun addInteger(key: String, i: Int): Boolean {
            return getKv().encode(key, i)
        }

        fun addDouble(key: String, d: Double): Boolean {
            return getKv().encode(key, d)
        }

        fun addLong(key: String, l: Long): Boolean {
            return getKv().encode(key, l)
        }

        fun addFloat(key: String, f: Float): Boolean {
            return getKv().encode(key, f)
        }

        fun getStr(key: String, def: String? = null): String? {
            return getKv().decodeString(key, def)
        }

        fun getBool(key: String, def: Boolean = false): Boolean {
            return getKv().decodeBool(key, def)
        }

        fun getInteger(key: String, def: Int = 0): Int {
            return getKv().decodeInt(key, def)
        }

        fun getDouble(key: String, def: Double = 0.0): Double {
            return getKv().decodeDouble(key, def)
        }

        fun getFloat(key: String, def: Float = 0F): Float {
            return getKv().decodeFloat(key, def)
        }

        fun getLong(key: String, def: Long = 0L): Long {
            return getKv().decodeLong(key, def)
        }
    }


}