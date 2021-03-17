package org.devio.hi.config.core

interface JsonParser {
    fun <T> fromJson(json: String, clazz: Class<T>): T?
}