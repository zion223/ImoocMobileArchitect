package org.devio.hi.library.app

import android.app.Application
import com.google.gson.Gson
import com.google.gson.JsonParser
import org.devio.hi.imooc.log.*


class MApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 初始化HiLogManager
        HiLogManager.init(
            object : HiLogConfig() {
                override fun injectJsonParse(): JsonParse? {
                    return JsonParse { src -> Gson().toJson(src) }
                }

                override fun getGlobalTag(): String {
                    return "MApplication"
                }

                override fun enable(): Boolean {
                    return true
                }

                override fun includeThread(): Boolean {
                    return true
                }

                override fun stackTraceDepth(): Int {
                    return 5
                }
            },
            HiConsolePrinter()
            //HiFilePrinter.getInstance(applicationContext.cacheDir.absolutePath, 0)
        )
    }
}