package org.devio.hi.ui.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import org.devio.hi.ui.app.demo.banner.HiBannerDemoActivity
import org.devio.hi.ui.app.demo.refresh.HiRefreshDemoActivity
import org.devio.hi.ui.app.demo.tab.HiTabBottomDemoActivity
import org.devio.hi.ui.app.demo.tab.HiTabTopDemoActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_tab_bottom -> {
                startActivity(Intent(this, HiTabBottomDemoActivity::class.java))
            }
            R.id.tv_hi_refresh -> {
                startActivity(Intent(this, HiRefreshDemoActivity::class.java))
            }
            R.id.tv_hi_banner -> {
                startActivity(Intent(this, HiBannerDemoActivity::class.java))
            }
            R.id.tv_hi_taptop -> {
                startActivity(Intent(this, HiTabTopDemoActivity::class.java))
            }
        }
    }
}
