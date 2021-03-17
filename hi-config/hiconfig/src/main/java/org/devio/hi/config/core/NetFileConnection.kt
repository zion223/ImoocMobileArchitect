package org.devio.hi.config.core

import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * 从CDN上读取配置
 */
class NetFileConnection {
    private var httpURLConnection: HttpURLConnection? = null
    fun syncRequest(url: String): String? {
        val uri = URL(url)
        var response: String? = null
        httpURLConnection = uri.openConnection() as HttpURLConnection
        httpURLConnection?.apply {
            connectTimeout = 5000
            readTimeout = 5000
            useCaches = false
            doInput = true
            requestMethod = "GET"
        }
        if (httpURLConnection?.responseCode == 200) {
            response = getResponse()
        }
        return response
    }

    private fun getResponse(): String? {
        return if (httpURLConnection == null) {
            null
        } else {
            var inputStream: InputStream? = null
            var bos: ByteArrayOutputStream? = null
            try {
                inputStream = httpURLConnection!!.inputStream
                bos = ByteArrayOutputStream()
                val buffer = ByteArray(2048)
                var length: Int
                while (inputStream.read(buffer).also { length = it } != -1) {
                    bos.write(buffer, 0, length)
                }
                String(bos.toByteArray())
            } catch (e: IOException) {
                throw e
            } finally {
                HiConfigUtil.close(inputStream)
                HiConfigUtil.close(bos)
            }
        }
    }
}