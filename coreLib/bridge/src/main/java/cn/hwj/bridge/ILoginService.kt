package cn.hwj.bridge

/**
 * @author by jason-何伟杰，2022/12/14
 * des:登录组件对外暴露的数据通信 Api
 */
interface ILoginService {

    fun isLogin():Boolean

    fun getLoginToken():String?
}