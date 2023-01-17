package cn.hwj.search.menu

import android.os.Bundle
import cn.hwj.route.RoutePath
import com.didi.drouter.annotation.Router

/**
 * @author by jason-何伟杰，2023/1/13
 * des: 两种方式实现 联动列表
 */
@Router(path = RoutePath.SEARCH_ACTIVITY_MENU)
class MenuActivity : BaseMenuActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}