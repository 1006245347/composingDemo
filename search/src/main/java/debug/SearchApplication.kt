package debug

import cn.hwj.core.global.CoreApplicationProvider
import cn.hwj.core.global.ModuleInitDelegate
import cn.hwj.search.ModuleSearch

class SearchApplication: CoreApplicationProvider() {

    init {
        ModuleInitDelegate.register(ModuleSearch())
    }
}