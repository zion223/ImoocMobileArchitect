package org.devio.hi.library.taskflow

import android.text.TextUtils
import android.util.Log
import org.devio.hi.library.BuildConfig
import org.devio.hi.library.executor.HiExecutor
import org.devio.hi.library.flow.TaskRuntimeListener
import org.devio.hi.library.util.MainHandler
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.isNotEmpty
import kotlin.collections.linkedSetOf
import kotlin.collections.mutableListOf
import kotlin.collections.set

/**
 * taskflow 运行时的任务调度器
 *
 * 1. 根据task的属性以不同的策略（线程，同步，延迟）调度任务
 * 2. 校验 依赖树中是否存在环形依赖校验
 * 3. 校验依赖树中是否存在taskId相同的任务
 * 4. 统计所有 task的 运行时信息（线程，状态，开始执行时间，耗时时间，是否是阻塞任务），用于log输出
 */
internal object TaskRuntime {
    //通过addBlockTask (String name)指定启动阶段 需要阻塞完成的任务，只有当blockTasksId当中的任务都执行完了
    //才会释放application的阻塞，才会拉起launchActivity
    private val blockTasksId: MutableList<String> = mutableListOf()

    //如果blockTasksId 集合中的任务还没有完成，那么在主线程中执行的非阻塞任务 会被添加到waitingTasks集合里面去
    //目的是为了优先保证 阻塞任务的优先完成 ，尽可能早的拉起launchActivity
    private val waitingTasks: MutableList<Task> = mutableListOf()

    //记录下 启动阶段所有任务的运行时信息key 是 taskId
    private val taskRuntimeInfo: MutableMap<String, TaskRuntimeInfo> = HashMap()

    val taskComparator = Comparator<Task> { task1, task2 -> Utils.compareTask(task1, task2) }

    @JvmStatic
    fun addBlockTask(id: String) {
        if (!TextUtils.isEmpty(id)) {
            blockTasksId.add(id)
        }
    }

    @JvmStatic
    fun addBlockTasks(vararg ids: String) {
        if (ids.isNotEmpty()) {
            for (id in ids) {
                addBlockTask(id)
            }
        }
    }

    @JvmStatic
    fun removeBlockTask(id: String) {
        blockTasksId.remove(id)
    }

    @JvmStatic
    fun hasBlockTasks(): Boolean {
        return blockTasksId.iterator().hasNext()
    }

    @JvmStatic
    fun hasWaitingTasks(): Boolean {
        return waitingTasks.iterator().hasNext()
    }

    @JvmStatic
    fun setThreadName(task: Task, threadName: String?) {
        val taskRuntimeInfo = getTaskRuntimeInfo(task.id)
        taskRuntimeInfo?.threadName = threadName
    }

    @JvmStatic
    fun setStateInfo(task: Task) {
        val taskRuntimeInfo = getTaskRuntimeInfo(task.id)
        taskRuntimeInfo?.setStateTime(task.state, System.currentTimeMillis())
    }

    @JvmStatic
    fun getTaskRuntimeInfo(id: String): TaskRuntimeInfo? {
        return taskRuntimeInfo[id]
    }

    //根据task 的属性以不同的策略 调度 task
    @JvmStatic
    fun executeTask(task: Task) {
        if (task.isAsyncTask) {
            HiExecutor.execute(runnable = task)
        } else {
            //else 里面的 都是在主线程 执行的
            //延迟任务 ，但是如果这个延迟任务 它存在着后置任务  A(延迟任务)-->B--->C（Block task）
            if (task.delayMills > 0 && !hasBlockBehindTask(task)) {
                MainHandler.postDelay(task.delayMills, task)
                return
            }

            if (!hasBlockTasks() || getTaskRuntimeInfo(task.id)?.isBlockTask == true) {
                task.run()
            } else {
                addWaitingTask(task)
            }
        }
    }

    //跑等待队列的任务
    @JvmStatic
    fun runWaitingTasks() {
        if (hasWaitingTasks()) {
            if (waitingTasks.size > 1) {
                Collections.sort(waitingTasks, taskComparator)
            }
            if (hasBlockTasks()) {
                val head = waitingTasks.removeAt(0)
                head.run()
            } else {
                for (waitingTask in waitingTasks) {
                    MainHandler.postDelay(waitingTask.delayMills, waitingTask)
                }
                waitingTasks.clear()
            }
        }
    }

    //把一个主线程上需要执行的任务，但又不影响launchActivity的启动，添加到等待队列
    private fun addWaitingTask(task: Task) {
        if (!waitingTasks.contains(task)) {
            waitingTasks.add(task)
        }
    }


    //检测一个延迟任务 是否存在着 后置的 阻塞任务（就是等他们都执行完了，才会释放application的阻塞，才会拉起launchActivity）
    private fun hasBlockBehindTask(task: Task): Boolean {
        if (task is Project.CriticalTask) {
            return false
        }

        val behindTasks = task.behindTasks
        for (behindTask in behindTasks) {
            //需要判断一个task 是不是阻塞任务 ，blockTaskIds
            val behindTaskInfo = getTaskRuntimeInfo(behindTask.id)
            return if (behindTaskInfo != null && behindTaskInfo.isBlockTask) {
                true
            } else {
                hasBlockBehindTask(behindTask)
            }
        }
        return false
    }

    //校验 依赖树中是否存在环形依赖校验,  依赖树中是否存在taskId相同的任务  初始化task 对应taskRuntimeInfo
    //遍历依赖树 完成启动前的检查 和 初始化
    @JvmStatic
    fun traversalDependencyTreeAndInit(task: Task) {
        val traversalVisitor = linkedSetOf<Task>()
        traversalVisitor.add(task)
        innerTraversalDependencyTreeAndInit(task, traversalVisitor)

        val iterator = blockTasksId.iterator()
        while (iterator.hasNext()) {
            val taskId = iterator.next()
            //检查这个阻塞任务 是否存在依赖树中
            if (!taskRuntimeInfo.containsKey(taskId)) {
                throw java.lang.RuntimeException("block task ${task.id} not in dependency tree.")
            } else {
                //提升 阻塞任务task 前置依赖任务的优先级
                traversalDependencyPriority(getTaskRuntimeInfo(taskId)?.task)
            }
        }
    }

    //那么此时 我们只需要向上遍历，提升 task 前置依赖任务的优先级即可
    private fun traversalDependencyPriority(task: Task?) {
        if (task == null) return
        task.priority = Int.MAX_VALUE
        for (dependTask in task.dependTasks) {
            traversalDependencyPriority(dependTask)
        }
    }

    private fun innerTraversalDependencyTreeAndInit(
        task: Task,
        traversalVisitor: LinkedHashSet<Task>
    ) {

        //初始化 taskRuntimeInfo  并校验是否存在相同的任务名称 task.ID
        var taskRuntimeInfo = getTaskRuntimeInfo(task.id)
        if (taskRuntimeInfo == null) {
            taskRuntimeInfo = TaskRuntimeInfo(task)
            if (blockTasksId.contains(task.id)) {
                taskRuntimeInfo.isBlockTask = true
            }
            this.taskRuntimeInfo[task.id] = taskRuntimeInfo
        } else {
            if (!taskRuntimeInfo.isSameTask(task)) {
                throw RuntimeException("not allow to contain the same id ${task.id}")
            }
        }

        //校验环形依赖
        for (behindTask in task.behindTasks) {
            if (!traversalVisitor.contains(behindTask)) {
                traversalVisitor.add(behindTask)
            } else {
                throw  RuntimeException("not allow loopback dependency ,task id =${task.id}")
            }

            //start --> task1 -->task2-->task3-->task4-->task5-->end
            //对 task3 后面的依赖任务路径上的task 做环形依赖检查 初始化runtimeInfo 信息
            if (BuildConfig.DEBUG && behindTask.behindTasks.isEmpty()) {
                //behindTask =end
                val iterator = traversalVisitor.iterator()
                val builder: StringBuilder = StringBuilder()
                while (iterator.hasNext()) {
                    builder.append(iterator.next().id)
                    builder.append(" --> ")
                }
                val log = builder.toString()
                Log.e(TaskRuntimeListener.TAG, log.substring(0, log.length - 5))//减5是为了去除最后一个 -->
            }
            innerTraversalDependencyTreeAndInit(behindTask, traversalVisitor)
            traversalVisitor.remove(behindTask)
        }
    }
}