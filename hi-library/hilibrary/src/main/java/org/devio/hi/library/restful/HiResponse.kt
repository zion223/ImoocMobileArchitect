package org.devio.hi.library.restful

/**
 * 响应报文
 */
open class HiResponse<T> {

    companion object {
        const val SUCCESS: Int = 0
        const val CACHE_SUCCESS: Int = 304
        const val RC_HAS_ERROR = 5000                   //有错误
        const val RC_ACCOUNT_INVALID = 5001            //账号不存在
        const val RC_PWD_INVALID = 5002               //密码错误
        const val RC_NEED_LOGIN = 5003               //请先登录
        const val RC_NOT_PURCHASED = 5004           //未购买本课程，或用户ID有误
        const val RC_CHECK_SERVER_ERROR = 5005     //校验服务报错
        const val RC_USER_NAME_EXISTS = 5006      //此用户名被占用
        const val RC_HTML_INVALID = 8001         //请输入HTML
        const val RC_CONFIG_INVALID = 8002      //请输入配置

        const val RC_USER_FORBID = 6001       //用户身份非法，如有疑问可进入课程官方群联系管理员
        const val RC_AUTH_TOKEN_EXPIRED = 4030    //访问Token过期，请重新设置
        const val RC_AUTH_TOKEN_INVALID = 4031   //访问Token不正确，请重新设置
    }

    var rawData: String? = null//原始数据
    var code = 0//业务状态码
    var data: T? = null//业务数据
    var errorData: Map<String, String>? = null //错误状态下的数据

    var msg: String? = null//错误信息


    fun successful(): Boolean {
        return code == SUCCESS || code == CACHE_SUCCESS
    }
}