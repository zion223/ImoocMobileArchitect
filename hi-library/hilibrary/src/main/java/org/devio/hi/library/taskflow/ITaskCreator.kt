package org.devio.hi.library.taskflow

interface ITaskCreator {
    fun createTask(taskName: String): Task
}