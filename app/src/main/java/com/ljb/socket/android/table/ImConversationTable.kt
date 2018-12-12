package com.ljb.socket.android.table

import com.ljb.socket.android.common.Constant
import com.ljb.socket.android.table.base.BaseChatMessageTable
import com.ljb.socket.android.utils.SPUtils

/**
 * Author:Ljb
 * Time:2018/11/9
 * There is a lot of misery in life
 **/
class ImConversationTable : BaseChatMessageTable() {

    override fun createTableName(): String {
        val uid = SPUtils.getString(Constant.SPKey.KEY_UID)
        return "im_conversation_$uid"
    }

    override fun createColumns(): Map<String, String> {
        return getChatMessageColumns()
    }
}