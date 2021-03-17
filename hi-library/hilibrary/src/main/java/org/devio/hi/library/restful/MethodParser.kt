package org.devio.hi.library.restful

import org.devio.hi.library.cache.Cache
import org.devio.hi.library.restful.annotation.*
import java.lang.reflect.*

class MethodParser(
    private val baseUrl: String,
    method: Method
) {
    private var replaceRelativeUrl: String? = null
    private var cacheStrategy: Int = CacheStrategy.NET_ONLY
    private var domainUrl: String? = null
    private var formPost: Boolean = true
    private var httpMethod: Int = -1
    private lateinit var relativeUrl: String
    private lateinit var returnType: Type
    private var headers: MutableMap<String, String> = mutableMapOf()
    private var parameters: MutableMap<String, String> = mutableMapOf()

    init {
        //parse method annotations such as get,headers,post baseUrl
        parseMethodAnnotations(method)

        //parse method generic return type
        parseMethodReturnType(method)

        //parse method parameters such as path,filed
        //parseMethodParameters(method, args)
    }


    /**
     * interface ApiService {
     *  @Headers("auth-token:token", "accountId:123456")
     *  @BaseUrl("https://api.devio.org/as/")
     *  @POST("/cities/{province}")
     *  @GET("/cities")
     * fun listCities(@Path("province") province: Int,@Filed("page") page: Int): HiCall<JsonObject>
     * }
     */
    private fun parseMethodReturnType(method: Method) {
        if (method.returnType != HiCall::class.java) {
            throw IllegalStateException(
                String.format(
                    "method %s must be type of HiCall.class",
                    method.name
                )
            )
        }
        val genericReturnType = method.genericReturnType
        if (genericReturnType is ParameterizedType) {
            val actualTypeArguments = genericReturnType.actualTypeArguments
            require(actualTypeArguments.size == 1) { "method %s can only has one generic return type" }
            val argument = actualTypeArguments[0]
            require(validateGenericType(argument)) {
                String.format("method %s generic return type must not be an unknown type. " + method.name)
            }
            returnType = argument
        } else {
            throw  IllegalStateException(
                String.format(
                    "method %s must has one gerneric return type",
                    method.name
                )
            )
        }
    }

    private fun parseMethodParameters(method: Method, args: Array<Any>) {
        //每次调用api接口时  应该吧上一次解析到的参数清理掉，因为methodParser存在复用
        parameters.clear()

        //@Path("province") province: Int,@Filed("page") page: Int
        val parameterAnnotations = method.parameterAnnotations
        val equals = parameterAnnotations.size == args.size
        require(equals) {
            String.format(
                "arguments annotations count %s dont match expect count %s",
                parameterAnnotations.size,
                args.size
            )
        }

        //args
        for (index in args.indices) {
            val annotations = parameterAnnotations[index]
            require(annotations.size <= 1) { "filed can only has one annotation :index =$index" }

            val value = args[index]
            require(isPrimitive(value)) { "8 basic types are supported for now,index=$index" }

            val annotation = annotations[0]
            if (annotation is Filed) {
                val key = annotation.value
                val value = args[index]
                parameters[key] = value.toString()
            } else if (annotation is Path) {
                val replaceName = annotation.value
                val replacement = value.toString()
                if (replaceName != null && replaceName != null) {
                    //relativeUrl = home/{categroyId}
                    replaceRelativeUrl = relativeUrl.replace("{$replaceName}", replacement)
                }
            } else if (annotation is CacheStrategy) {
                cacheStrategy = value as Int
            } else {
                throw  IllegalStateException("cannot handle parameter annotation :" + annotation.javaClass.toString())
            }
        }


    }

    private fun parseMethodAnnotations(method: Method) {

        val annotations = method.annotations;
        for (annotation in annotations) {
            if (annotation is GET) {
                relativeUrl = annotation.value
                httpMethod = HiRequest.METHOD.GET
            } else if (annotation is POST) {
                relativeUrl = annotation.value
                httpMethod = HiRequest.METHOD.POST
                formPost = annotation.formPost
            } else if (annotation is PUT) {
                formPost = annotation.formPost
                httpMethod = HiRequest.METHOD.PUT
                relativeUrl = annotation.value
            } else if (annotation is DELETE) {
                httpMethod = HiRequest.METHOD.DELETE
                relativeUrl = annotation.value
            } else if (annotation is Headers) {
                val headersArray = annotation.value
                //@Headers("auth-token:token", "accountId:123456")
                for (header in headersArray) {
                    val colon = header.indexOf(":")
                    check(!(colon == 0 || colon == -1)) {
                        String.format(
                            "@headers value must be in the form [name:value] ,but found [%s]",
                            header
                        )
                    }
                    val name = header.substring(0, colon)
                    val value = header.substring(colon + 1).trim()
                    headers[name] = value
                }
            } else if (annotation is BaseUrl) {
                domainUrl = annotation.value
            } else if (annotation is CacheStrategy) {
                cacheStrategy = annotation.value
            } else {
                throw IllegalStateException("cannot handle method annotation:" + annotation.javaClass.toString())
            }
        }

        require((httpMethod == HiRequest.METHOD.GET)
                || (httpMethod == HiRequest.METHOD.POST
                || (httpMethod == HiRequest.METHOD.PUT)
                || (httpMethod == HiRequest.METHOD.DELETE))) {
            String.format("method %s must has one of GET,POST,PUT,DELETE ", method.name)
        }

            if (domainUrl == null) {
                domainUrl = baseUrl
            }

    }


    private fun validateGenericType(type: Type): Boolean {
        /**
         *wrong
         *  fun test():HiCall<Any>
         *  fun test():HiCall<List<*>>
         *  fun test():HiCall<ApiInterface>
         *expect
         *  fun test():HiCall<User>
         */
        //如果指定的泛型是集合类型的，那还检查集合的泛型参数
        if (type is GenericArrayType) {
            return validateGenericType(type.genericComponentType)
        }
        //如果指定的泛型是一个接口 也不行
        if (type is TypeVariable<*>) {
            return false
        }
        //如果指定的泛型是一个通配符 ？extends Number 也不行
        if (type is WildcardType) {
            return false
        }

        return true
    }

    private fun isPrimitive(value: Any): Boolean {
        //String
        if (value.javaClass == String::class.java) {
            return true
        }
        try {
            //int byte short long boolean char double float
            val field = value.javaClass.getField("TYPE")
            val clazz = field[null] as Class<*>
            if (clazz.isPrimitive) {
                return true
            }
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
        return false
    }

    fun newRequest(method: Method, args: Array<out Any>?): HiRequest {
        val arguments: Array<Any> = args as Array<Any>? ?: arrayOf()
        parseMethodParameters(method, arguments)

        val request = HiRequest()
        request.domainUrl = domainUrl
        request.returnType = returnType
        request.relativeUrl = replaceRelativeUrl ?: relativeUrl
        request.parameters = parameters
        request.headers = headers
        request.httpMethod = httpMethod
        request.formPost = formPost
        request.cacheStrategy = cacheStrategy
        return request
    }


    companion object {
        fun parse(baseUrl: String, method: Method): MethodParser {
            return MethodParser(baseUrl, method)
        }
    }
}