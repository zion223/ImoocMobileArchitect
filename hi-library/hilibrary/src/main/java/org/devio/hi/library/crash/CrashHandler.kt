package org.devio.hi.library.crash

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.os.Process
import android.os.StatFs
import android.text.format.Formatter
import org.devio.hi.library.BuildConfig
import org.devio.hi.library.log.HiLog
import org.devio.hi.library.util.ActivityManager
import org.devio.hi.library.util.AppGlobals
import java.io.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

internal object CrashHandler {
    var CRASH_DIR = "crash_dir"
    fun init(crashDir: String) {
        Thread.setDefaultUncaughtExceptionHandler(CaughtExceptionHandler())
        this.CRASH_DIR = crashDir
    }

    private class CaughtExceptionHandler : Thread.UncaughtExceptionHandler {
        private val context = AppGlobals.get()!!
        private val formatter = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA)
        private val LAUNCH_TIME = formatter.format(Date())
        private val defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        override fun uncaughtException(t: Thread, e: Throwable) {
            if (!handleException(e) && defaultExceptionHandler != null) {
                defaultExceptionHandler.uncaughtException(t, e)
            }
            restartApp()
        }

        private fun restartApp() {
            val intent: Intent? =
                context.packageManager?.getLaunchIntentForPackage(context.packageName)
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(intent)

            Process.killProcess(Process.myPid())
            exitProcess(10)
        }


        private fun handleException(e: Throwable?): Boolean {
            if (e == null) return false
            val log = collectDeviceInfo(e)
            if (BuildConfig.DEBUG) {
                HiLog.e(log)
            }

            saveCrashInfo2File(log)
            return true
        }

        private fun saveCrashInfo2File(log: String) {
            val crashDir = File(CRASH_DIR)
            if (!crashDir.exists()) {
                crashDir.mkdirs()
            }
            val crashFile = File(crashDir, formatter.format(Date()) + "-crash.txt")
            crashFile.createNewFile()
            val fos = FileOutputStream(crashFile)

            try {
                fos.write(log.toByteArray())
                fos.flush()
            } catch (ex: Exception) {
                ex.printStackTrace()
            } finally {
                fos.close()
            }
        }


        /**
         * 设备类型、OS本版、线程名、前后台、使用时长、App版本、升级渠道

        CPU架构、内存信息、存储信息、permission权限
         */
        private fun collectDeviceInfo(e: Throwable): String {
            val sb = StringBuilder()
            sb.append("brand=${Build.BRAND}\n")// huawei,xiaomi
            sb.append("rom=${Build.MODEL}\n") //sm-G9550
            sb.append("os=${Build.VERSION.RELEASE}\n")//9.0
            sb.append("sdk=${Build.VERSION.SDK_INT}\n")//28
            sb.append("launch_time=${LAUNCH_TIME}\n")//启动APP的时间
            sb.append("crash_time=${formatter.format(Date())}\n")//crash发生的时间
            sb.append("forground=${ActivityManager.instance.front}\n")//应用处于前后台
            sb.append("thread=${Thread.currentThread().name}\n")//异常线程名
            sb.append("cpu_arch=${Build.CPU_ABI}\n")//armv7 armv8

            //app 信息
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            sb.append("version_code=${packageInfo.versionCode}\n")
            sb.append("version_name=${packageInfo.versionName}\n")
            sb.append("package_name=${packageInfo.packageName}\n")
            sb.append("requested_permission=${Arrays.toString(packageInfo.requestedPermissions)}\n")//已申请到那些权限


            //统计一波 存储空间的信息，
            val memInfo = android.app.ActivityManager.MemoryInfo()
            val ams =
                context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
            ams.getMemoryInfo(memInfo)

            sb.append("availMem=${Formatter.formatFileSize(context, memInfo.availMem)}\n")//可用内存
            sb.append("totalMem=${Formatter.formatFileSize(context, memInfo.totalMem)}\n")//设备总内存

            val file = Environment.getExternalStorageDirectory()
            val statFs = StatFs(file.path)
            val availableSize = statFs.availableBlocks * statFs.blockSize
            sb.append(
                "availStorage=${Formatter.formatFileSize(
                    context,
                    availableSize.toLong()
                )}\n"
            )//存储空间


            val write: Writer = StringWriter()
            val printWriter = PrintWriter(write)
            e.printStackTrace(printWriter)
            var cause = e.cause
            while (cause != null) {
                cause.printStackTrace(printWriter)
                cause = cause.cause
            }

            printWriter.close()
            sb.append(write.toString())
            return sb.toString()
        }
    }

    fun crashFiles(): Array<File> {
        return File(
            AppGlobals.get()?.cacheDir,
            CRASH_DIR
        ).listFiles()
    }
}