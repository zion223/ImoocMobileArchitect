package org.devio.hi.library.restful

import android.text.TextUtils
import androidx.annotation.IntDef
import org.devio.hi.library.restful.annotation.CacheStrategy
import java.lang.Exception
import java.lang.StringBuilder
import java.lang.reflect.Type
import java.net.URLEncoder

open class HiRequest {

    private var cacheStrategyKey: String = ""
    var cacheStrategy: Int = CacheStrategy.NET_ONLY

    @METHOD
    var httpMethod: Int = 0
    var headers: MutableMap<String, String>? = null
    var parameters: MutableMap<String, String>? = null
    var domainUrl: String? = null
    var relativeUrl: String? = null
    var returnType: Type? = null
    var formPost: Boolean = true

    @IntDef(value = [METHOD.GET, METHOD.POST])
    annotation class METHOD {
        companion object {
            const val GET = 0
            const val POST = 1
            const val PUT=2
            const val DELETE=3
        }
    }

    //返回的是请求的完整的url
    /**
     * //scheme-host-port:443
    //https://api.devio.org/v1/ ---relativeUrl: user/login===>https://api.devio.org/v1/user/login

    //可能存在别的域名的场景
    //https://api.devio.org/v2/


    //https://api.devio.org/v1/ ---relativeUrl: /v2/user/login===>https://api.devio.org/v2/user/login
     */
    fun endPointUrl(): String {
        if (relativeUrl == null) {
            throw IllegalStateException("relative url must bot be null ")
        }
        if (!relativeUrl!!.startsWith("/")) {
            return domainUrl + relativeUrl
        }

        val indexOf = domainUrl!!.indexOf("/")
        return domainUrl!!.substring(0, indexOf) + relativeUrl
    }

    fun addHeader(name: String, value: String) {
        if (headers == null) {
            headers = mutableMapOf()
        }
        headers!![name] = value
    }

    fun getCacheKey(): String {
        if (!TextUtils.isEmpty(cacheStrategyKey)) {
            return cacheStrategyKey
        }
        val builder = StringBuilder()
        val endUrl = endPointUrl()
        builder.append(endUrl)
        if (endUrl.indexOf("?") > 0 || endUrl.indexOf("&") > 0) {
            builder.append("&")
        } else {
            builder.append("?")
        }

        if (parameters != null) {
            for ((key, value) in parameters!!) {
                try {
                    val encodeValue = URLEncoder.encode(value, "UTF-8")
                    builder.append(key).append("=").append(encodeValue).append("&")
                } catch (e: Exception) {
                    //ignore
                }
            }
            builder.deleteCharAt(builder.length - 1)
            cacheStrategyKey = builder.toString()
        } else {
            cacheStrategyKey = endUrl
        }

        return cacheStrategyKey
    }

}
