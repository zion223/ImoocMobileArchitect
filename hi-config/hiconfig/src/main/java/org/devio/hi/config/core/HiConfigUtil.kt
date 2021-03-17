package org.devio.hi.config.core

import android.os.Handler
import android.os.Looper
import java.io.Closeable
import java.io.IOException

object HiConfigUtil {
    fun runUI(block: () -> Unit) {
        val handler = Handler(Looper.getMainLooper())
        handler.post(block)
    }

    fun close(closeable: Closeable?) {
        if (closeable != null) {
            try {
                closeable.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}