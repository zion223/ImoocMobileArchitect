package org.devio.hi.library.restful.annotation

/**
 * @POST("/cities/{province}")
 *fun test(@Path("province") int provinceId)
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class POST(val value: String, val formPost: Boolean = true)