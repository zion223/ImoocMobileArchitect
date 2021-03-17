package org.devio.hi.library.app.demo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import org.devio.hi.library.app.R
import org.devio.hi.imooc.log.*

class HiLogDemoActivity : AppCompatActivity() {
    var viewPrinter: HiViewPrinter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hi_log_demo)
        viewPrinter = HiViewPrinter(this)
        findViewById<View>(R.id.btn_log).setOnClickListener {
            printLog()
        }
        viewPrinter!!.hiViewPrinterProvider.showFloatingView()
    }

    private fun printLog() {
        HiLogManager.getInstance().addPrinter(viewPrinter)
        HiLog.log(object : HiLogConfig() {
            override fun includeThread(): Boolean {
                return false
            }
            override fun getGlobalTag(): String {
                return "HiLogDemo"
            }

            override fun stackTraceDepth(): Int {
                return 0
            }
        }, HiLogType.E, "---", "5566")
        HiLog.v("9900")
    }
}
