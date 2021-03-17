package org.devio.hi.library.flow

import android.os.Looper

internal object Utils {
    /**
     * 比较两个 task
     * [Task.priority] 值高的，优先级高
     * [Task.executeTime] 添加到队列的时间最早，优先级越高
     *
     * @param task
     * @param o
     * @return
     */
    fun compareTask(task: Task, o: Task): Int {
        if (task.priority < o.priority) {
            return 1
        }
        if (task.priority > o.priority) {
            return -1
        }
        if (task.executeTime < o.executeTime) {
            return -1
        }
        return if (task.executeTime > o.executeTime) {
            1
        } else 0
    }

    fun assertMainThread() {
        if (Thread.currentThread() !== Looper.getMainLooper().thread) {
            throw RuntimeException("TaskFlowManager#start should be invoke on MainThread!")
        }
    }
}