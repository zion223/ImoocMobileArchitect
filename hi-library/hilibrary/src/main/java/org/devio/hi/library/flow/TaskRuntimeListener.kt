package org.devio.hi.library.flow

import android.util.Log
import org.devio.hi.library.flow.TaskRuntime.debuggable
import org.devio.hi.library.flow.TaskRuntime.getTaskRuntimeInfo

class TaskRuntimeListener : TaskListener {
    override fun onStart(task: Task) {
        if (debuggable()) {
            Log.e(TAG, task.id + START_METHOD)
        }
    }

    override fun onRunning(task: Task) {
        if (debuggable()) {
            Log.e(TAG, task.id + RUNNING_METHOD)
        }
    }

    override fun onFinish(task: Task) {
        logTaskRuntimeInfoString(task)
    }

    companion object {
        const val TAG = "TaskFlow"
        const val START_METHOD = " -- onStart -- "
        const val RUNNING_METHOD = " -- onRunning -- "
        const val FINISH_METHOD = " -- onFinish -- "
        const val LINE_STRING_FORMAT = "| %s : %s "
        const val MS_UNIT = "ms"
        const val HALF_LINE_STRING = "======================="
        const val DEPENDENCIES = "依赖任务"
        const val THREAD_INFO = "线程信息"
        const val START_TIME = "开始时刻"
        const val START_UNTIL_RUNNING = "等待运行耗时"
        const val RUNNING_CONSUME = "运行任务耗时"
        const val FINISH_TIME = "结束时刻"
        const val IS_WAIT = "是否是锚点任务"
        const val WRAPPED = "\n"

        private fun logTaskRuntimeInfoString(task: Task) {
            val taskRuntimeInfo = getTaskRuntimeInfo(task.id)
            val map = taskRuntimeInfo.stateTime
            val startTime = map[TaskState.START]
            val runningTime = map[TaskState.RUNNING]
            val finishedTime = map[TaskState.FINISHED]
            val builder = StringBuilder()
            builder.append(WRAPPED)
            builder.append(TAG)
            builder.append(WRAPPED)
            buildTaskInfoEdge(builder, taskRuntimeInfo)
            addTaskInfoLineString(
                builder,
                DEPENDENCIES,
                getDependenceInfo(taskRuntimeInfo),
                false
            )
            addTaskInfoLineString(
                builder,
                IS_WAIT,
                java.lang.String.valueOf(taskRuntimeInfo.isBlockTask),
                false
            )
            addTaskInfoLineString(
                builder,
                THREAD_INFO,
                taskRuntimeInfo.threadName,
                false
            )
            addTaskInfoLineString(
                builder,
                START_TIME,
                startTime.toString(),
                true
            )
            addTaskInfoLineString(
                builder,
                START_UNTIL_RUNNING,
                (runningTime - startTime).toString(),
                true
            )
            addTaskInfoLineString(
                builder,
                RUNNING_CONSUME,
                (finishedTime - runningTime).toString(),
                true
            )
            addTaskInfoLineString(
                builder,
                FINISH_TIME,
                finishedTime.toString(),
                false
            )
            buildTaskInfoEdge(builder, null)
            builder.append(WRAPPED)
            builder.append(WRAPPED)
            if (debuggable()) {
                Log.e(TAG, builder.toString())
            }
        }

        private fun addTaskInfoLineString(
            stringBuilder: StringBuilder?,
            key: String,
            time: String?,
            addUnit: Boolean
        ) {
            if (stringBuilder == null) {
                return
            }
            stringBuilder.append(WRAPPED)
            stringBuilder.append(
                String.format(
                    LINE_STRING_FORMAT,
                    key,
                    time
                )
            )
            if (addUnit) {
                stringBuilder.append(MS_UNIT)
            }
        }

        private fun buildTaskInfoEdge(
            stringBuilder: StringBuilder?,
            taskRuntimeInfo: TaskRuntimeInfo?
        ) {
            if (stringBuilder == null) {
                return
            }
            if (taskRuntimeInfo == null) {
                stringBuilder.append(WRAPPED)
                stringBuilder.append(HALF_LINE_STRING)
                stringBuilder.append(HALF_LINE_STRING)
                stringBuilder.append(HALF_LINE_STRING)
                stringBuilder.append(WRAPPED)
                return
            }
            stringBuilder.append(WRAPPED)
            stringBuilder.append(HALF_LINE_STRING)
            stringBuilder.append(if (taskRuntimeInfo.task is Project) " project (" else " task (" + taskRuntimeInfo.task.id + " ) " + FINISH_METHOD)
            stringBuilder.append(HALF_LINE_STRING)
        }

        private fun getDependenceInfo(taskRuntimeInfo: TaskRuntimeInfo): String {
            val stringBuilder = StringBuilder()
            for (s in taskRuntimeInfo.task.dependTaskName) {
                stringBuilder.append("$s ")
            }
            return stringBuilder.toString()
        }
    }
}