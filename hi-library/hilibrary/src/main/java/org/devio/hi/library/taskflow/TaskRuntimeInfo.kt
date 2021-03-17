package org.devio.hi.library.taskflow

import android.util.SparseArray

/**
 * 用以记录 每一个task 实例的 运行时的信息的 封装
 *
 */
class TaskRuntimeInfo(val task: Task) {
    val stateTime = SparseArray<Long>()
    var isBlockTask = false
    var threadName: String? = null

    fun setStateTime(@TaskState state: Int, time: Long) {
        stateTime.put(state, time)
    }

    fun isSameTask(task: Task?): Boolean {
        return task != null && this.task == task
    }

    override fun toString(): String {
        return "TaskRuntimeInfo{" +
                "stateTime=" + stateTime +
                ", isBlockTask=" + isBlockTask +
                ", task=" + task +
                ", threadName='" + threadName + '\'' +
                '}'
    }
}