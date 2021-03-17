package org.devio.hi.library.aspectj

import android.util.Log
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before

@Aspect
class ActivityAspect {

    /**
     * 注解(before,after,around)、 访问权限(call,execution,set,get) 、返回值的类型(Object,Int,※)、包名.函数名(参数)
     * 例子：@before(execution(* android.app.Activity.on**(..));
     *
     * before after, =JoinPoint
     */
    @Around("execution(* android.app.Activity.setContentView(..))")
    fun setContentView(joinPoint: ProceedingJoinPoint) {
        adviceCode(joinPoint)
    }

    private fun adviceCode(joinPoint: ProceedingJoinPoint) {
        val signature = joinPoint.signature
        val className = signature.declaringType.simpleName
        val methodName = signature.name

        val time = System.currentTimeMillis()
        joinPoint.proceed()

        Log.e(
            "ActivityAspect",
            className + ":" + methodName + " cost=" + (System.currentTimeMillis() - time)
        )
    }

    @Around("execution(@org.devio.hi.library.aspectj.MethodTrace * *(..))")
    fun methodTrace(joinPoint: ProceedingJoinPoint) {
        adviceCode(joinPoint)
    }


//    @Before("execution(androidx.appcompat.widget.AppCompatImageView.setImageDrawable(..))")
//    fun setImageDrawable(joinPoint: JoinPoint) {
//
//    }
}