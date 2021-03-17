package org.devio.hi.library.crash

//import org.devio.`as`.proj.libbreakpad.NativeCrashHandler
import org.devio.hi.library.util.AppGlobals
import java.io.File

object CrashMgr {
    private const val CRASH_DIR_JAVA = "java_crash"
    private const val CRASH_DIR_NATIVE = "native_crash"
    fun init() {
        val javaCrashDir = getJavaCrashDir()
        val nativeCrashDir = getNativeCrashDir()

        CrashHandler.init(javaCrashDir.absolutePath)
        //NativeCrashHandler.init(nativeCrashDir.absolutePath)
    }

    private fun getJavaCrashDir(): File {
        val javaCrashFile = File(AppGlobals.get()!!.cacheDir, CRASH_DIR_JAVA)
        if (!javaCrashFile.exists()) {
            javaCrashFile.mkdirs()
        }
        return javaCrashFile
    }


    private fun getNativeCrashDir(): File {
        val nativeCrashFile = File(AppGlobals.get()!!.cacheDir, CRASH_DIR_NATIVE)
        if (!nativeCrashFile.exists()) {
            nativeCrashFile.mkdirs()
        }
        return nativeCrashFile
    }

    fun crashFiles(): Array<File> {
        return getJavaCrashDir().listFiles() + getNativeCrashDir().listFiles()
    }

}