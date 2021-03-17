package org.devio.hi.library.util

import android.content.Context
import org.devio.hi.library.executor.HiExecutor
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

object HiFileUtil {
    /**
     * 将文件从assets目录中的文件复制到/data/data/包名/files/ 目录中
     * @param fileName 要copy的文件名，如xx.xx
     */
    fun copyAssetsFile2FilesDir(context: Context, fileName: String, listener: (() -> Unit)?) {
        HiExecutor.execute(runnable = Runnable {
            doCopy(context, fileName)
            listener?.invoke()
        })
    }

    private fun doCopy(context: Context, fileName: String) {
        var inputStream: InputStream? = null
        var fos: FileOutputStream? = null
        try {
            val file = File(context.filesDir.absolutePath + File.separator.toString() + fileName)
            if (file.exists()) return
            inputStream = context.assets.open(fileName)
            fos = FileOutputStream(file)
            var len = -1
            val buffer = ByteArray(1024)
            while (inputStream.read(buffer).also { len = it } != -1) {
                fos.write(buffer, 0, len)
            }
            fos.flush() //刷新缓存区

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
            fos?.close()
        }
    }
}