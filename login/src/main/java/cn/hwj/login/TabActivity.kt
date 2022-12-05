package cn.hwj.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.hwj.route.RoutePath
import com.didi.drouter.annotation.Router

/**
 * @author by jason-何伟杰，2022/12/2
 * des: 标签主页
 */
@Router(path = RoutePath.LOGIN_ACTIVITY_TAG)
class TabActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab)
    }
}