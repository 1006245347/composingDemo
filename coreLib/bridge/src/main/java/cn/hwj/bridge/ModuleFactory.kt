package cn.hwj.bridge

/**
 * @author by jason-何伟杰，2022/12/14
 * des:统一管理module的通信,禁止外部创建 ModuleFactory对象
 */
class ModuleFactory private constructor() {

    //登录组件的对外接口
    private lateinit var loginService:ILoginService

    //搜索组件的对外接口
    private var searchService:ISearchService?=null

    companion object{
        //单例设计
        val instance: ModuleFactory by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ModuleFactory()
        }
    }

    fun setLoginService(service: ILoginService){
        this.loginService=service
    }

    fun getLoginService():ILoginService {
        return loginService
    }

    fun setSearchService(service: ISearchService?){
        this.searchService=service
    }

    fun getSearchService():ISearchService?{
        if (null == searchService) {
            searchService=EmptyModuleService()
        }
        return searchService
    }
}