package cn.hwj.search

import cn.hwj.bridge.ISearchService

/**
 * @author by jason-何伟杰，2022/12/14
 * des:搜素模块对外提供数据通信api
 */
class SearchServiceApi : ISearchService {
    override fun getResult(): String? {
        return "ask url1/2/3 for result"
    }
}