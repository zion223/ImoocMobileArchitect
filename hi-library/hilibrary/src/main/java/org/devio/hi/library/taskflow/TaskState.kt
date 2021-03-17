package org.devio.hi.library.taskflow

import androidx.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Retention(RetentionPolicy.SOURCE)
@IntDef(
    TaskState.IDLE,
    TaskState.RUNNING,
    TaskState.FINISHED,
    TaskState.START
)
annotation class TaskState {
    companion object {
        const val IDLE = 0 //静止
        const val START = 1 //启动,可能需要等待调度，
        const val RUNNING = 2 //运行
        const val FINISHED = 3 //运行结束
    }
}