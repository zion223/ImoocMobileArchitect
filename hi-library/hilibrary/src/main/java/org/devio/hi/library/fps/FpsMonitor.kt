package org.devio.hi.library.fps

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.TextView
import kotlinx.android.synthetic.main.fps_view.view.*
import org.devio.hi.library.R
import org.devio.hi.library.log.HiLog
import org.devio.hi.library.util.ActivityManager
import org.devio.hi.library.util.AppGlobals
import java.text.DecimalFormat

object FpsMonitor {
    private val fpsViewer = FpsViewer()
    fun toggle() {
        fpsViewer.toggle()
    }

    fun listener(callback: FpsCallback) {
        fpsViewer.addListener(callback)
    }

    interface FpsCallback {
        fun onFrame(fps: Double)
    }

    private class FpsViewer {
        private var params = WindowManager.LayoutParams()
        private var isPlaying = false
        private val application: Application = AppGlobals.get()!!
        private var fpsView =
            LayoutInflater.from(application).inflate(R.layout.fps_view, null, false) as TextView

        private val decimal = DecimalFormat("#.0 fps")
        private var windowManager: WindowManager? = null

        private val frameMonitor = FrameMonitor()

        init {
            windowManager = application.getSystemService(Context.WINDOW_SERVICE) as WindowManager

            params.width = WindowManager.LayoutParams.WRAP_CONTENT
            params.height = WindowManager.LayoutParams.WRAP_CONTENT

            params.flags =
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL

            params.format = PixelFormat.TRANSLUCENT
            params.gravity = Gravity.RIGHT or Gravity.TOP

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                params.type = WindowManager.LayoutParams.TYPE_TOAST
            }

            frameMonitor.addListener(object : FpsCallback {
                override fun onFrame(fps: Double) {
                    fpsView.text = decimal.format(fps)
                }
            })

            ActivityManager.instance.addFrontBackCallback(object :
                ActivityManager.FrontBackCallback {
                override fun onChanged(front: Boolean) {
                    if (front) {
                        play()
                    } else {
                        stop();
                    }
                }

            })
        }

        private fun stop() {
            frameMonitor.stop()
            if (isPlaying) {
                isPlaying = false
                windowManager!!.removeView(fpsView)
            }
        }

        private fun play() {
            if (!hasOverlayPermission()) {
                startOverlaySettingActivity()
                HiLog.e("app has no overlay permission")
                return
            }

            frameMonitor.start()
            if (!isPlaying) {
                isPlaying = true
                windowManager!!.addView(fpsView, params)
            }
        }

        private fun startOverlaySettingActivity() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                application.startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + application.packageName)
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        }

        private fun hasOverlayPermission(): Boolean {
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(
                application
            )
        }

        fun toggle() {
            if (isPlaying) {
                stop()
            } else {
                play()
            }
        }

        fun addListener(callback: FpsCallback) {
            frameMonitor.addListener(callback)
        }
    }
}