package org.devio.hi.library.flow

interface ITaskCreator {
    fun createTask(taskName: String): Task
}