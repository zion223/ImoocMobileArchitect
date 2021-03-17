package org.devio.hi.config.core

import android.content.Context
import org.devio.hi.library.executor.HiExecutor

class HiConfigDelegate(
    private val parser: JsonParser,
    context: Context
) : IConfig {
    private var config: Config? = null
    private var configMap: Map<String, Any>? = null
    private val cacheManager: CacheManager = CacheManager(context)
    private val listeners: ArrayList<ConfigListener> = arrayListOf()

    init {
        HiExecutor.execute(runnable = Runnable {
            config = cacheManager.restore<Config>()
            configMap = config?.content
            if (configMap != null) {
                configNotify()
            }
        })
    }

    override fun feed(data: String) {
        val model = parser.fromJson(data, ExtraModel::class.java)
        model?.extra?.apply {
            updateConfig(hiConfig)
        }
    }

    private fun updateConfig(hiConfig: Config?) {
        if (hiConfig == null) return
        if (compareVersion(hiConfig, config)) {
            this.config = hiConfig
            sync()
        }
    }

    private fun sync() {
        config?.jsonUrl?.let {
            HiExecutor.execute(runnable = Runnable {
                val res = NetFileConnection().syncRequest(it)
                res?.let {
                    val result = parser.fromJson(res, Map::class.java)
                    if (!result.isNullOrEmpty()) {
                        configMap = result as Map<String, Any>
                        config?.content = configMap
                        configNotify()
                        cacheManager.cache(config)
                    }
                }
            })
        }
    }

    private fun compareVersion(config1: Config, config2: Config?): Boolean {
        if (config2 == null) return true
        return config1.version > config2.version
    }

    override fun getStringConfig(name: String): String? {
        try {
            return configMap?.get(name) as String?
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
        return null

    }

    override fun <T> getObjectConfig(name: String, clazz: Class<T>): T? {
        try {
            return configMap?.get(name) as T?
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
        return null
    }

    override fun getVersion(): String? {
        return config?.version
    }

    private fun configNotify() {
        if (configMap == null) return
        listeners.forEach {
            HiConfigUtil.runUI {
                it.onConfigUpdate(configMap!!)
            }
        }
    }

    override fun addListener(listener: ConfigListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    override fun removeListener(listener: ConfigListener) {
        listeners.remove(listener)
    }
}