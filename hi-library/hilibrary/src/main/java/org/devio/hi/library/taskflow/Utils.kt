package org.devio.hi.library.taskflow

object Utils {
    //比较两个任务的先后执行顺序
    //优先级越高的越先执行
    fun compareTask(task1: Task, task2: Task): Int {
        if (task1.priority < task2.priority) {
            return 1
        }
        if (task1.priority > task2.priority) {
            return -1
        }
        return 0
    }
}