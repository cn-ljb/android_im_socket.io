package dao.ljb.kt.core

/**
 * Author:Ljb
 * Time:2018/12/7
 * There is a lot of misery in life
 **/
interface IProtocolTransform {

    fun <T> transformProtocol(clazz: Class<T>): T
}
