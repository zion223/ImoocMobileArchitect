package org.devio.hi.imooc.util

import android.app.Activity
import java.lang.ref.WeakReference

/**
 * Describe:
 * <p>Activity任务栈管理类</p>
 *
 * @author zhangruiping
 * @Date 2021/5/8
 *
 */
class ActivityManager private constructor() {

    private val activityList = ArrayList<WeakReference<Activity>>()
    private val frontBackCallback = ArrayList<FrontBackCallback>()


    interface FrontBackCallback {
        fun onChanged()
    }

    companion object {
        // 单例模式
        val instance: ActivityManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ActivityManager()
        }
    }
}
