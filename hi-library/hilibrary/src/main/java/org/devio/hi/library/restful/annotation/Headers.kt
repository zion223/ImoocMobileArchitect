package org.devio.hi.library.restful.annotation

/**
 * @Headers({"connection:keep-alive","auth-token:token"})
 *fun test(@Filed("province") int provinceId)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Headers(vararg val value: String)