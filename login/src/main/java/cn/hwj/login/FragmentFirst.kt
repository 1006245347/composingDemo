package cn.hwj.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import cn.hwj.core.global.printV
import com.didi.drouter.annotation.Router
import com.didi.drouter.api.Extend

@Router(path = "/fragment/first/.*")
class FragmentFirst : Fragment() {

    private lateinit var launcher: ActivityResultLauncher<Intent>
    private var path: String? = null

    init {
        printV("FragmentFirst constructor>>>")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        path = arguments?.getString(Extend.REQUEST_BUILD_URI)
        launcher = registerForActivityResult(object : ActivityResultContract<Intent, String>() {
            override fun createIntent(context: Context, input: Intent): Intent {
                return input
            }

            override fun parseResult(resultCode: Int, intent: Intent?): String {
                return if (null != intent) {
                    intent.getStringExtra("phone") + ""
                } else {
                    ""
                }
            }
        }) { printV("fragmentFirstCallback>>>") }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_first, container, false)
        val tvInfo = view.findViewById<TextView>(R.id.tvInfo)
        tvInfo.text = path
        return view
    }

}