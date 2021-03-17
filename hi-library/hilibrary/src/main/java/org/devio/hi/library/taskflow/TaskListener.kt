package org.devio.hi.library.taskflow

interface TaskListener {
    fun onStart(task: Task)

    fun onRunning(task: Task)

    fun onFinished(task: Task)
}