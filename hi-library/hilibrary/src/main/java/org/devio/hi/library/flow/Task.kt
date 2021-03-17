package org.devio.hi.library.flow

import androidx.core.os.TraceCompat
import org.devio.hi.library.flow.TaskRuntime.debuggable
import org.devio.hi.library.flow.TaskRuntime.executeTask
import org.devio.hi.library.flow.TaskRuntime.removeBlockTask
import org.devio.hi.library.flow.TaskRuntime.setStateInfo
import org.devio.hi.library.flow.TaskRuntime.setThreadName
import org.devio.hi.library.flow.TaskRuntime.sTaskComparator
import java.util.*

/**
 * created by timian on 2019/06/11
 */
abstract class Task @JvmOverloads constructor(
    val id: String,  //Id,唯一存在
    val isAsyncTask: Boolean = false,    //是否是异步存在
    val delayMills: Long = 0,//延迟执行的时间
    var priority: Int = 0
) : Runnable, Comparable<Task> {
    @TaskState
    var state = TaskState.IDLE //状态
        protected set
    var executeTime: Long = 0
        protected set

    val behindTasks: MutableList<Task> = ArrayList() //后置任务,那些task依赖了这个task
    val dependTasks: MutableList<Task> = ArrayList() //前置任务，这个task 依赖了那些task

    val dependTaskName: MutableList<String> = ArrayList() //用于log统计
    private val taskListeners: MutableList<TaskListener> = ArrayList() //监听器
    private var logTaskListeners: TaskListener? = TaskRuntimeListener()

    fun addTaskListener(taskListener: TaskListener?) {
        if (taskListener != null && !taskListeners.contains(taskListener)) {
            taskListeners.add(taskListener)
        }
    }

    @Synchronized
    open fun start() {
        if (state != TaskState.IDLE) {
            throw RuntimeException("can no run task $id again!")
        }
        toStart()
        executeTime = System.currentTimeMillis()
        executeTask(this)
    }

    override fun run() {
        TraceCompat.beginSection(id)

        toRunning()
        run(id)
        toFinish()
        notifyBehindTasks()
        recycle()

        TraceCompat.endSection()
    }

    protected abstract fun run(name: String)

    private fun toStart() {
        state = TaskState.START
        setStateInfo(this)
        if (debuggable()) {
            logTaskListeners!!.onStart(this)
        }
        for (listener in taskListeners) {
            listener.onStart(this)
        }
    }

    private fun toRunning() {
        state = TaskState.RUNNING
        setStateInfo(this)
        setThreadName(this, Thread.currentThread().name)
        if (debuggable()) {
            logTaskListeners!!.onRunning(this)
        }
        for (listener in taskListeners) {
            listener.onRunning(this)
        }
    }

    private fun toFinish() {
        state = TaskState.FINISHED
        setStateInfo(this)
        removeBlockTask(id)
        if (debuggable()) {
            logTaskListeners!!.onFinish(this)
        }
        for (listener in taskListeners) {
            listener.onFinish(this)
        }
    }


    /**
     * 后置触发, 和 [Task.dependOn] 方向相反，都可以设置依赖关系
     *
     * @param task
     */
    open fun behind(task: Task) {
        var behindTask = task
        if (behindTask !== this) {
            if (behindTask is Project) {
                behindTask = behindTask.startTask
            }
            behindTasks.add(behindTask)
            behindTask.dependOn(this)
        }
    }

    open fun removeBehind(task: Task) {
        var behindTask = task
        if (behindTask !== this) {
            if (behindTask is Project) {
                behindTask = behindTask.startTask
            }
            behindTasks.remove(behindTask)
            behindTask.removeDependence(this)
        }
    }

    /**
     * 前置条件, 和 [Task.behind] 方向相反，都可以设置依赖关系
     *
     * @param task
     */
    open fun dependOn(task: Task) {
        var dependTask = task
        if (dependTask !== this) {
            if (dependTask is Project) {
                dependTask = dependTask.endTask
            }
            dependTasks.add(dependTask)
            dependTaskName.add(dependTask.id)
            //防止外部所有直接调用dependOn无法构建完整图
            if (!dependTask.behindTasks.contains(this)) {
                dependTask.behindTasks.add(this)
            }
        }
    }

    open fun removeDependence(task: Task) {
        var dependTask = task
        if (dependTask !== this) {
            if (dependTask is Project) {
                dependTask = dependTask.endTask
            }
            dependTasks.remove(dependTask)
            // TODO: 2020/10/10  
            dependTaskName.add(dependTask.id)
            if (dependTask.behindTasks.contains(this)) {
                dependTask.behindTasks.remove(this)
            }
        }
    }

    override fun compareTo(other: Task): Int {
        return Utils.compareTask(this, other)
    }

    /**
     * 通知后置者自己已经完成了
     */
    private fun notifyBehindTasks() {
        if (behindTasks.isNotEmpty()) {
            if (behindTasks.size > 1) {
                Collections.sort(behindTasks, sTaskComparator)
            }

            //遍历记下来的任务，通知它们说存在的前置已经完成
            for (task in behindTasks) {
                task.dependTaskFinish(this)
            }
        }
    }

    /**
     * 依赖的任务已经完成
     * 比如 B -> A (B 依赖 A), A 完成之后调用该方法通知 B "A依赖已经完成了"
     * 当且仅当 B 的所有依赖都已经完成了, B 开始执行
     *
     * @param dependTask
     */
    private fun dependTaskFinish(dependTask: Task?) {
        if (dependTasks.isEmpty()) {
            return
        }
        dependTasks.remove(dependTask)

        //所有前置任务都已经完成了
        if (dependTasks.isEmpty()) {
            start()
        }
    }

    private fun recycle() {
        dependTasks.clear()
        behindTasks.clear()
        dependTaskName.clear()
        taskListeners.clear()
        logTaskListeners = null
    }
}