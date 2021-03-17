package org.devio.hi.library.flow

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import org.devio.hi.library.flow.Project.CriticalTask
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * created by timian on 2019/06/12
 */
object TaskRuntime {
    //调试信息
    private var sDebuggable = false

    //线程池
    private val sPool = InnerThreadPool()

    //设置锚点任务，当且仅当所有锚点任务都完成时, application 不在阻塞 UIThread
    private val sBlockTaskIds: MutableList<String> = mutableListOf()

    //如果存在锚点任务，则同步任务在所有锚点任务都完成前，在 UIThread 上运行
    //ps: 后续解除锚点之后，所有UI线程上的 Task 都通过 handle 发送执行，不保证业务逻辑的同步。
    private val sWaitingTasks: MutableList<Task> = mutableListOf()

    private val sHandler = Handler(Looper.getMainLooper())

    //所有 task 运行时信息
    private val sTaskRuntimeInfo: MutableMap<String, TaskRuntimeInfo> = HashMap()

    //Task 比较逻辑
    val sTaskComparator: Comparator<Task> = Comparator { o1, o2 -> Utils.compareTask(o1, o2) }

    private val sTraversalVisitor: MutableSet<Task> = HashSet()

    @JvmStatic
    fun debuggable(): Boolean {
        return sDebuggable
    }

    @JvmStatic
    fun openDebug(debug: Boolean) {
        sDebuggable = debug
    }

    @JvmStatic
    fun addBlockTask(id: String) {
        if (!TextUtils.isEmpty(id)) {
            sBlockTaskIds.add(id)
        }
    }

    @JvmStatic
    fun addBlockTasks(vararg ids: String) {
        if (ids.isNotEmpty()) {
            for (id in ids) {
                sBlockTaskIds.add(id)
            }
        }
    }

    @JvmStatic
    fun removeBlockTask(id: String?) {
        sBlockTaskIds.remove(id)
    }

    @JvmStatic
    fun hasBlockTasks(): Boolean {
        return sBlockTaskIds.iterator().hasNext()
    }

    @JvmStatic
    val blockTasks: MutableList<String> get() = sBlockTaskIds

    private fun addWaitingTask(task: Task?) {
        if (task != null && !sWaitingTasks.contains(task)) {
            sWaitingTasks.add(task)
        }
    }

    @JvmStatic
    fun tryRunWaitingTasks() {
        if (sWaitingTasks.isNotEmpty()) {
            if (sWaitingTasks.size > 1) {
                Collections.sort(sWaitingTasks, sTaskComparator)
            }
            val task = sWaitingTasks.removeAt(0)
            if (hasBlockTasks()) {
                task.run()
            } else {
                sHandler.postDelayed(DelayRunnable(task), task.delayMills)
                for (blockTask in sWaitingTasks) {
                    sHandler.postDelayed(blockTask, blockTask.delayMills)
                }
                sWaitingTasks.clear()
            }
        }
    }

    @JvmStatic
    fun hasWaitingTasks(): Boolean {
        return sWaitingTasks.iterator().hasNext()
    }

    private fun hasTaskRuntimeInfo(taskId: String): Boolean {
        return sTaskRuntimeInfo[taskId] != null
    }

    @JvmStatic
    fun getTaskRuntimeInfo(taskId: String): TaskRuntimeInfo {
        return sTaskRuntimeInfo[taskId]!!
    }

    @JvmStatic
    fun setThreadName(
        task: Task,
        threadName: String?
    ) {
        val taskRuntimeInfo = sTaskRuntimeInfo[task.id]
        if (taskRuntimeInfo != null) {
            taskRuntimeInfo.threadName = threadName
        }
    }

    @JvmStatic
    fun setStateInfo(task: Task) {
        val taskRuntimeInfo = sTaskRuntimeInfo[task.id]
        taskRuntimeInfo?.setStateTime(task.state, System.currentTimeMillis())
    }

    @JvmStatic
    fun executeTask(task: Task) {
        if (task.isAsyncTask) {
            sPool.executeTask(task)
        } else {
            if (task.delayMills > 0 && !hasBlockBehindTask(task)) {
                //延迟任务的 链路上如果存在 有 block任务 则延迟时间无效
                sHandler.postDelayed(DelayRunnable(task), task.delayMills)
                return
            }
            if (!hasBlockTasks()) {
                task.run()
            } else {
                addWaitingTask(task)
            }
        }
    }

    private fun hasBlockBehindTask(task: Task): Boolean {
        if (task is CriticalTask) {
            return false
        }
        val behindTasks = task.behindTasks
        for (behindTask in behindTasks) {
            val behindTaskInfo = getTaskRuntimeInfo(behindTask.id)
            return if (behindTaskInfo.isBlockTask) {
                true
            } else hasBlockBehindTask(behindTask)
        }
        return false
    }

    /**
     * 遍历依赖树并完成启动前的初始化
     *
     *
     * 1.获取依赖树最大深度
     * 2.遍历初始化运行时数据并打印log
     * 3.如果锚点不存在，则移除
     * 4.提升锚点链的优先级
     *
     * @param task
     */
    @JvmStatic
    fun traversalDependencyTreeAndInit(task: Task) {
        //获取依赖树最大深度
        val maxDepth = checkIfExitsLoopbackDependency(task, sTraversalVisitor)
        sTraversalVisitor.clear()
        val pathTasks = arrayOfNulls<Task>(maxDepth)
        //遍历初始化运行时数据并打印log
        traversalDependencyPath(task, pathTasks, 0)

        //如果锚点不存在，则移除。存在则提升锚点链的优先级
        val iterator = sBlockTaskIds.iterator()
        while (iterator.hasNext()) {
            val taskId = iterator.next()
            if (!hasTaskRuntimeInfo(taskId)) {
                iterator.remove()
                if (debuggable()) {
                    Log.e(TaskRuntimeListener.TAG, "block task \"$taskId\" no found !")
                }
            } else {
                val info = getTaskRuntimeInfo(taskId)
                traversalMaxTaskPriority(info.task)
            }
        }
    }

    /**
     * 递归向上设置优先级
     *
     * @param task
     */
    private fun traversalMaxTaskPriority(task: Task?) {
        if (task == null) {
            return
        }
        task.priority = Int.MAX_VALUE
        for (dependence in task.dependTasks) {
            traversalMaxTaskPriority(dependence)
        }
    }

    /**
     * 遍历依赖树
     * 1. 初始化 sTaskRuntimeInfo
     * 2. 判断锚点是否存在依赖树中
     *
     * @param task
     * @param pathTasks
     * @param pathLen
     */
    private fun traversalDependencyPath(
        task: Task,
        pathTasks: Array<Task?>,
        pathLen: Int
    ) {
        var pathLen = pathLen
        pathTasks[pathLen++] = task
        //依赖路径到尽头了
        if (task.behindTasks.isEmpty()) {
            val stringBuilder = StringBuilder()
            for (i in 0 until pathLen) {
                val pathItem = pathTasks[i]
                if (pathItem != null) {
                    if (hasTaskRuntimeInfo(pathItem.id)) {
                        val taskRuntimeInfo = getTaskRuntimeInfo(pathItem.id)
                        //不允许框架层存在两个相同id的task
                        if (!taskRuntimeInfo.isSameTask(pathItem)) {
                            throw RuntimeException("Multiple different tasks are not allowed to contain the same id (" + pathItem.id + ")!")
                        }
                    } else {
                        //如果没有初始化则初始化runtimeInfo
                        val taskRuntimeInfo = TaskRuntimeInfo(pathItem)
                        if (sBlockTaskIds.contains(pathItem.id)) {
                            taskRuntimeInfo.isBlockTask = true
                        }
                        sTaskRuntimeInfo[pathItem.id] = taskRuntimeInfo
                    }
                    if (debuggable()) {
                        stringBuilder.append((if (i == 0) "" else " --> ") + pathItem.id)
                    }
                }
            }
            if (debuggable()) {
                Log.e(TaskRuntimeListener.TAG, stringBuilder.toString())
            }
        } else {
            for (behindTask in task.behindTasks) {
                traversalDependencyPath(behindTask, pathTasks, pathLen)
            }
        }
    }

    /**
     * 获取依赖树的最大深度
     *
     * @param task
     * @return
     */
    private fun checkIfExitsLoopbackDependency(
        task: Task,
        sTraversalVisitor: MutableSet<Task>
    ): Int {
        //判断依赖路径是否存在异常，不允许存在回环的依赖
        var maxDepth = 0
        if (!sTraversalVisitor.contains(task)) {
            sTraversalVisitor.add(task)
        } else {
            throw RuntimeException("Do not allow dependency graphs to have a loopback！Related task'id is " + task.id + "!")
        }
        for (behindTask in task.behindTasks) {
            val newTasks: MutableSet<Task> = HashSet()
            newTasks.addAll(sTraversalVisitor)
            val depth = checkIfExitsLoopbackDependency(behindTask, newTasks)
            if (depth >= maxDepth) {
                maxDepth = depth
            }
        }
        maxDepth++
        return maxDepth
    }

    private class DelayRunnable(private val delayTask: Task) :
        Runnable {
        override fun run() {
            if (debuggable()) {
                Log.e(TaskRuntimeListener.TAG, "DelayRunnable:" + delayTask.id)
            }
            delayTask.run()
        }
    }

    internal class InnerThreadPool {
        private val asyncThreadExecutor: ExecutorService
        private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
        private val CORE_POOL_SIZE = Math.max(4, Math.min(CPU_COUNT - 1, 8))
        private val MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1
        private val KEEP_ALIVE_SECONDS = 30L
        private val sThreadFactory: ThreadFactory =
            object : ThreadFactory {
                private val mCount = AtomicInteger(1)

                override fun newThread(r: Runnable): Thread {
                    return Thread(r, "taskflow-Thread #" + mCount.getAndIncrement())
                }
            }
        private val sPoolWorkQueue: BlockingQueue<Runnable> =
            PriorityBlockingQueue(128)

        fun executeTask(runnable: Runnable?) {
            asyncThreadExecutor.execute(runnable)
        }

        init {
            val threadPoolExecutor =
                ThreadPoolExecutor(
                    CORE_POOL_SIZE,
                    MAXIMUM_POOL_SIZE,
                    KEEP_ALIVE_SECONDS,
                    TimeUnit.SECONDS,
                    sPoolWorkQueue,
                    sThreadFactory
                )
            threadPoolExecutor.allowCoreThreadTimeOut(true)
            asyncThreadExecutor = threadPoolExecutor
        }
    }
}