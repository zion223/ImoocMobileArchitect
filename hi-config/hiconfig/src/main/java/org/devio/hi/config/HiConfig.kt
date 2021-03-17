package org.devio.hi.config

import android.content.Context
import org.devio.hi.config.core.ConfigListener
import org.devio.hi.config.core.HiConfigDelegate
import org.devio.hi.config.core.IConfig
import org.devio.hi.config.core.JsonParser

/**
 * 配置中心SDK
 */
class HiConfig private constructor() : IConfig {
    private var delegate: HiConfigDelegate? = null

    /**
     * 首先需要调用初始化方法
     */
    fun init(parser: JsonParser, context: Context) {
        delegate = HiConfigDelegate(parser, context)
    }

    override fun feed(data: String) {
        delegate?.feed(data)
    }

    override fun getStringConfig(name: String): String? {
        return delegate?.getStringConfig(name)
    }

    override fun <T> getObjectConfig(name: String, clazz: Class<T>): T? {
        return delegate?.getObjectConfig(name, clazz)
    }

    override fun getVersion(): String? {
        return delegate?.getVersion()
    }

    override fun addListener(listener: ConfigListener) {
        delegate?.addListener(listener)
    }

    override fun removeListener(listener: ConfigListener) {
        delegate?.removeListener(listener)
    }

    companion object {
        @JvmStatic
        var instance: HiConfig = Provider.holder
            private set
    }

    private object Provider {
        val holder = HiConfig()
    }
}