package com.ljb.socket.android.table

import com.ljb.socket.android.table.base.BaseChatMessageTable

/**
 * Author:Ljb
 * Time:2018/11/9
 * There is a lot of misery in life
 **/

class ImHistoryTable(private val conversation: String) : BaseChatMessageTable() {

    val COLUMN_READ = "read"
    val COLUMN_MSG_STATUS = "status"


    override fun createColumns(): Map<String, String> {
        val tableColumns = getChatMessageColumns()
        tableColumns[COLUMN_READ] = TYPE_INTEGER
        tableColumns[COLUMN_MSG_STATUS] = TYPE_INTEGER
        return tableColumns
    }

    override fun createTableName(): String {
        return "im_history_$conversation"
    }

}