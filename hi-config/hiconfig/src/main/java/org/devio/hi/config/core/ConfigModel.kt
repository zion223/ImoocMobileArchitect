package org.devio.hi.config.core

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class ExtraModel(
    val extra: Extra?
) : Serializable

@Keep
data class Extra(val hiConfig: Config?) : Serializable

@Keep
data class Config(
    val id: String
    , val namespace: String
    , val version: String
    , val createTime: String
    , val jsonUrl: String
    , val originalUrl: String,
    var content: Map<String, Any>?
) : Serializable