package org.devio.hi.config.app

import android.app.Application
import com.google.gson.Gson
import org.devio.hi.config.HiConfig
import org.devio.hi.config.core.JsonParser
import org.devio.hi.library.log.HiLogConfig
import org.devio.hi.library.log.HiLogManager

class MApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        HiLogManager.init(object : HiLogConfig() {
            override fun injectJsonParser(): JsonParser {
                return JsonParser { src: Any? ->
                    Gson().toJson(src)
                }
            }
        })
        HiConfig.instance.init(object : JsonParser {
            override fun <T> fromJson(json: String, clazz: Class<T>): T? {
                return Gson().fromJson(json, clazz)
            }
        }, context = this)
    }
}