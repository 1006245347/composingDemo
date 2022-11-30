package cn.hwj.login

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        initAty()
    }

    private fun initAty() {
        val tvInfo = findViewById<TextView>(R.id.tvInfo)
        tvInfo.setOnClickListener {
            clickEvent()
        }
    }

    private fun clickEvent() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}