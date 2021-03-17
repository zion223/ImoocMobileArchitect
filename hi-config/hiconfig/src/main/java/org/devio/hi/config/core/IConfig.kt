package org.devio.hi.config.core

interface IConfig {
    fun feed(data: String)
    fun getStringConfig(name: String): String?
    fun <T> getObjectConfig(name: String, clazz: Class<T>): T?
    fun getVersion(): String?
    fun addListener(listener: ConfigListener)
    fun removeListener(listener: ConfigListener)
}
