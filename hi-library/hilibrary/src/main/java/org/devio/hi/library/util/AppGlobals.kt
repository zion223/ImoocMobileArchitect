package org.devio.hi.library.util

import android.app.Application
import java.lang.Exception

/**
 * 这种方式获取全局的Application 是一种拓展思路。
 *
 *
 * 对于组件化项目,不可能把项目实际的Application下沉到Base,而且各个module也不需要知道Application真实名字
 *
 *
 * 这种一次反射就能获取全局Application对象的方式相比于在Application#OnCreate保存一份的方式显示更加通用了
 */
object AppGlobals {
    private var application: Application? = null
    fun get(): Application? {
        if (application == null) {
            try {
                application = Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication")
                    .invoke(null) as Application
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        return application
    }
}