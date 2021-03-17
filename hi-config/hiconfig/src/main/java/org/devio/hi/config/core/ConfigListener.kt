package org.devio.hi.config.core

interface ConfigListener {
    fun onConfigUpdate(configMap: Map<String, Any>)
}