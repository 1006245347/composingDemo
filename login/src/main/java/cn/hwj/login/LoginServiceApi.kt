package cn.hwj.login

import cn.hwj.bridge.ILoginService

/**
 * @author by jason-何伟杰，2022/12/14
 * des:对外组件通信实现类
 */
class LoginServiceApi : ILoginService {
    override fun isLogin(): Boolean {
        return "s".length==2
    }

    override fun getLoginToken(): String? {
       return "kkkk"
    }
}