package org.devio.hi.library.fps

import android.view.Choreographer
import org.devio.hi.library.log.HiLog
import java.util.concurrent.TimeUnit

internal class FrameMonitor : Choreographer.FrameCallback {
    private val choreographer = Choreographer.getInstance()
    private var frameStartTime: Long = 0//这个是记录 上一针到达的时间戳
    private var frameCount = 0//1s 内确切绘制了多少帧

    private var listeners = arrayListOf<FpsMonitor.FpsCallback>()
    override fun doFrame(frameTimeNanos: Long) {
        val currentTimeMills = TimeUnit.NANOSECONDS.toMillis(frameTimeNanos)
        if (frameStartTime > 0) {
            //计算两针之间的 时间差
            // 500ms  100ms
            val timeSpan = currentTimeMills - frameStartTime
            //fps 每秒多少帧  frame per second
            frameCount++
            if (timeSpan > 1000) {
                val fps = frameCount * 1000 / timeSpan.toDouble()
                HiLog.e("FrameMonitor", fps)
                for (listener in listeners) {
                    listener.onFrame(fps)
                }
                frameCount = 0
                frameStartTime = currentTimeMills
            }
        } else {
            frameStartTime = currentTimeMills
        }
        start()
    }

    fun start() {
        choreographer.postFrameCallback(this)
    }

    fun stop() {
        frameStartTime = 0
        listeners.clear()
        choreographer.removeFrameCallback(this)
    }

    fun addListener(l: FpsMonitor.FpsCallback) {
        listeners.add(l)
    }
}