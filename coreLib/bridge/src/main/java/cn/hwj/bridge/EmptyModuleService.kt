package cn.hwj.bridge

/**
 * @author by jason-何伟杰，2022/12/14
 * des:设计个空服务，module可以不对外提供通信方式
 */
class EmptyModuleService : ISearchService, ILoginService {
    override fun getResult(): String? {
        return null
    }

    override fun isLogin(): Boolean {
        return false
    }

    override fun getLoginToken(): String? {
        return null
    }
}