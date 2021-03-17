package org.devio.hi.library.taskflow

import android.os.Looper
import androidx.annotation.MainThread
import java.lang.Exception

/**
 * 对taskRuntime的包装，对外暴露的类，用于启动启动任务
 */
object TaskFlowManager {

    fun addBlockTask(taskId: String): TaskFlowManager {
        TaskRuntime.addBlockTask(taskId)
        return this
    }

    fun addBlockTasks(vararg taskIds: String): TaskFlowManager {
        TaskRuntime.addBlockTasks(*taskIds)
        return this
    }

    //project 任务组，也有可能是独立的一个task
    @MainThread
    fun start(task: Task) {
        assert(Thread.currentThread() == Looper.getMainLooper().thread) {
            "start method must be invoke on MainThread"
        }
        val startTask = if (task is Project) task.startTask else task
        TaskRuntime.traversalDependencyTreeAndInit(startTask)
        startTask.start()
        while (TaskRuntime.hasBlockTasks()) {
            try {
                Thread.sleep(10)
            } catch (ex: Exception) {

            }
            //主线程唤醒之后，存在着等待队列的任务
            //那么让等待队列中的任务执行
            while (TaskRuntime.hasBlockTasks()) {
                TaskRuntime.runWaitingTasks()
            }
        }
    }
}