package cn.hwj.search

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.hwj.route.RoutePath
import com.didi.drouter.annotation.Router

@Router(path = RoutePath.SEARCH_ACTIVITY_LIST)
class ListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.setLayerType(View.LAYER_TYPE_HARDWARE, setThemePaint(1))
        setContentView(R.layout.activity_list)
        val iv=findViewById<ImageView>(R.id.iv)
        iv.setBackgroundResource(R.mipmap.ic_launcher_round)
        val tvInfo=findViewById<TextView>(R.id.tvInfo)
        tvInfo.text = "ssssssssssssssssssssssssssssss"
    }

    //0-正常、1-黑白
    private fun setThemePaint(type: Int): Paint {
        val paint = Paint()
        val cm = ColorMatrix()
        when (type) {
            0 -> cm.setSaturation(1.0f)
            1 -> cm.setSaturation(0.0f)
        }
        paint.colorFilter = ColorMatrixColorFilter(cm)
        return paint
    }
}