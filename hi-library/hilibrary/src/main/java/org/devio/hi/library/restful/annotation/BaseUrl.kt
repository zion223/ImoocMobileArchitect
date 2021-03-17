package org.devio.hi.library.restful.annotation


/**
 * @BaseUrl("https://api.devio.org/as/")
 *fun test(@Filed("province") int provinceId)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class BaseUrl(val value: String)