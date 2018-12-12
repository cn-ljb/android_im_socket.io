package com.ljb.socket.android.table.base

import android.provider.BaseColumns
import dao.ljb.kt.table.BaseTable

/**
 * Author:Ljb
 * Time:2018/11/9
 * There is a lot of misery in life
 **/

abstract class BaseChatMessageTable : BaseTable() {

    val COLUMN_ID = BaseColumns._ID
    val COLUMN_FROM_ID = "fromId"
    val COLUMN_TO_ID = "toId"
    val COLUMN_CONVERSATION = "conversation"
    val COLUMN_TYPE = "type"
    val COLUMN_BODY_TYPE = "bodyType"
    val COLUMN_PID = "pid"
    val COLUMN_TIME = "time"
    val COLUMN_BODY = "body"
    val COLUMN_TOPIC = "topic"
    val COLUMN_CMD = "cmd"
    val COLUMN_DEV = "dev"
    val COLUMN_STATUS = "status"

    /**
     * 消息表字段
     * */
    protected fun getChatMessageColumns(): HashMap<String, String> {
        val tableColumns = HashMap<String, String>()
        tableColumns[COLUMN_ID] = "integer primary key autoincrement"
        tableColumns[COLUMN_FROM_ID] = TYPE_TEXT
        tableColumns[COLUMN_TO_ID] = TYPE_TEXT
        tableColumns[COLUMN_CONVERSATION] = TYPE_TEXT
        tableColumns[COLUMN_TYPE] = TYPE_INTEGER
        tableColumns[COLUMN_BODY_TYPE] = TYPE_INTEGER
        tableColumns[COLUMN_PID] = TYPE_TEXT
        tableColumns[COLUMN_TIME] = TYPE_LONG
        tableColumns[COLUMN_BODY] = TYPE_TEXT
        tableColumns[COLUMN_TOPIC] = TYPE_TEXT
        tableColumns[COLUMN_CMD] = TYPE_INTEGER
        tableColumns[COLUMN_DEV] = TYPE_INTEGER
        tableColumns[COLUMN_STATUS] = TYPE_INTEGER
        return tableColumns
    }
}