package org.devio.`as`.hi.hi_arouter

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.launcher.ARouter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val textView = findViewById<TextView>(R.id.text_view)
        textView.setOnClickListener {

            ARouter.getInstance().build("/trade/detail/activity")
                .withString("shopId", "1001")
                .withString("saleId", "1002")
                .navigation()
        }
    }
}
