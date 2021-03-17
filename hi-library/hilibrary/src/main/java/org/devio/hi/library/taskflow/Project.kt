package org.devio.hi.library.taskflow


/**
 * 任务组，管理一组任务的依赖关系，先后执行顺序
 */
class Project private constructor(id: String) : Task(id) {
    lateinit var endTask: Task     //任务组中 所有任务的结束节点
    lateinit var startTask: Task  //任务组的开始节点

    override fun start() {
        startTask.start()
    }

    override fun run(id: String) {
        //不需要处理的
    }

    override fun behind(behindTask: Task) {
        //当咱们给一个任务组添加后置任务的时候，那么这个任务应该添加到组当中谁的后面？？？
        //把新来的后置任务 添加到 任务组的结束节点后面去
        //这样的话，任务组里面所有的任务都结束了，这个后置任务才会执行
        endTask.behind(behindTask)

    }

    override fun dependOn(dependTask: Task) {
        startTask.dependOn(dependTask)
    }

    override fun removeDependence(dependTask: Task) {
        startTask.removeDependence(dependTask)
    }

    override fun removeBehind(behindTask: Task) {
        endTask.removeBehind(behindTask)
    }

    class Builder(projectName: String, iTaskCreator: ITaskCreator) {
        private val mTaskFactory = TaskFactory(iTaskCreator)
        private val mStartTask: Task = CriticalTask(projectName + "_start")
        private val mEndTask: Task = CriticalTask(projectName + "_end")
        private val mProject: Project = Project(projectName)
        private var mPriority = 0//默认为该任务组中 所有任务优先级的 最高的

        private var mCurrentTaskShouldDependOnStartTask = false
        //本次添加进来的这个task 是否把start 节点当做前置依赖
        //那如果这个task 它存在与其他task的依赖关系，那么就不能直接添加到start 节点的后面了
        //而需要通过dependOn来指定任务的依赖关系

        private var mCurrentAddTask: Task? = null

        fun add(id: String): Builder {
            val task = mTaskFactory.getTask(id)
            if (task.priority > mPriority) {
                mPriority = task.priority
            }
            return add(task)
        }

        private fun add(task: Task): Builder {
            if (mCurrentTaskShouldDependOnStartTask && mCurrentAddTask != null) {
                mStartTask.behind(mCurrentAddTask!!)
            }
            mCurrentAddTask = task
            mCurrentTaskShouldDependOnStartTask = true
            mCurrentAddTask!!.behind(mEndTask)

            return this
        }

        fun dependOn(id: String): Builder {
            return dependOn(mTaskFactory.getTask(id))
        }

        private fun dependOn(task: Task): Builder {
            // 确定 刚才我们所添加进来的mCurrentAddTask  和 task 的依赖关系----mCurrentAddTask 依赖于task
            task.behind(mCurrentAddTask!!)

            // start --task10 --mCurrentAddTask（task 11）--end
            mEndTask.removeDependence(task)

            mCurrentTaskShouldDependOnStartTask = false
            return this
        }

        fun build(): Project {
            if (mCurrentAddTask == null) {
                mStartTask.behind(mEndTask)
            } else {
                if (mCurrentTaskShouldDependOnStartTask) {
                    mStartTask.behind(mCurrentAddTask!!)
                }
            }
            mStartTask.priority = mPriority
            mEndTask.priority = mPriority
            mProject.startTask = mStartTask
            mProject.endTask = mEndTask

            return mProject
        }
    }

    private class TaskFactory(private val iTaskCreator: ITaskCreator) {
        // 利用iTaskCreator创建task 实例，并管理
        private val mCacheTasks: MutableMap<String, Task> = HashMap()

        fun getTask(id: String): Task {
            var task = mCacheTasks.get(id)
            if (task != null) {
                return task
            }

            task = iTaskCreator.createTask(id)
            mCacheTasks[id] = task
            return task
        }
    }

    internal class CriticalTask internal constructor(id: String) : Task(id) {
        override fun run(id: String) {
            //nothing to do
        }
    }

}