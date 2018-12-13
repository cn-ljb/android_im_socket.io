package com.ljb.socket.android.protocol.dao

import com.ljb.socket.android.protocol.dao.impl.ContactListProtocol
import com.ljb.socket.android.protocol.dao.impl.ChatHistoryDaoProtocol
import com.ljb.socket.android.protocol.dao.impl.InitDaoProtocol
import com.ljb.socket.android.protocol.dao.impl.NewNumProtocol
import dao.ljb.kt.core.IProtocolTransform

/**
 * Author:Ljb
 * Time:2018/12/7
 * There is a lot of misery in life
 **/
object ProtocolConfig : IProtocolTransform {

    @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
    override fun <T> transformProtocol(clazz: Class<T>) = when (clazz) {
        IInitDaoProtocol::class.java -> InitDaoProtocol()
        IContactListProtocol::class.java -> ContactListProtocol()
        IChatHistoryDaoProtocol::class.java -> ChatHistoryDaoProtocol()
        INewNumDaoProtocol::class.java -> NewNumProtocol()
        else -> throw IllegalStateException("not found dao interface object  : ${clazz.name}")
    } as T

}
