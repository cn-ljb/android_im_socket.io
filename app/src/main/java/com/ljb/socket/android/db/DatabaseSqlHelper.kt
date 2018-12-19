package com.ljb.socket.android.db

import android.content.ContentValues
import android.database.Cursor
import android.text.TextUtils
import com.ljb.socket.android.model.ChatMessage
import com.ljb.socket.android.model.UserBean
import com.ljb.socket.android.table.ContactTable
import com.ljb.socket.android.table.base.BaseChatMessageTable
import com.ljb.socket.android.table.ImNewNumTable
import dao.ljb.kt.table.BaseTable

/**
 * Author:Ljb
 * Time:2018/12/10
 * There is a lot of misery in life
 **/
object DatabaseSqlHelper {


    /**
     * 生成建表的sql语句的字段部分
     * @param table
     * @return sql
     */
    fun getCreateTableSql(table: BaseTable): String {
        val tabColumns = table.getColumns()
        val iterator = tabColumns.entries.iterator()
        val columnsSql = StringBuilder().apply {
            append(" (")
            while (iterator.hasNext()) {
                val entry = iterator.next()
                val key = entry.key
                val value = entry.value
                append(" ").append(key).append(" ").append(value).append(", ")
            }
            delete(this.length - ", ".length, this.length)
            append(");")
        }.toString()
        return "create table if not exists ${table.getName()}$columnsSql"
    }

    fun getContact(table: ContactTable, cursor: Cursor): UserBean {
        val uid = cursor.getString(cursor.getColumnIndex(table.COLUMN_UID))
        val name = cursor.getString(cursor.getColumnIndex(table.COLUMN_NAME))
        val headImg = cursor.getString(cursor.getColumnIndex(table.COLUMN_HEAD_IMG))
        return UserBean(uid, name, headImg)
    }

    fun getContactContentValue(table: ContactTable, contact: UserBean): ContentValues {
        val values = ContentValues()
        values.put(table.COLUMN_UID, contact.uid)
        values.put(table.COLUMN_NAME, contact.name)
        values.put(table.COLUMN_HEAD_IMG, contact.headUrl)
        return values
    }


    fun getChatMessageContentValue(table: BaseChatMessageTable, chatMessage: ChatMessage): ContentValues {
        val values = ContentValues()
        values.put(table.COLUMN_FROM_ID, chatMessage.fromId)
        values.put(table.COLUMN_TO_ID, chatMessage.toId)
        values.put(table.COLUMN_CONVERSATION, chatMessage.conversation)
        values.put(table.COLUMN_TYPE, chatMessage.type)
        values.put(table.COLUMN_BODY_TYPE, chatMessage.bodyType)
        values.put(table.COLUMN_PID, chatMessage.pid)
        values.put(table.COLUMN_TIME, chatMessage.time)
        values.put(table.COLUMN_BODY, chatMessage.body)
        values.put(table.COLUMN_DEV, chatMessage.dev)
        values.put(table.COLUMN_TOPIC, chatMessage.topic)
        values.put(table.COLUMN_CMD, chatMessage.cmd)
        values.put(table.COLUMN_STATUS, chatMessage.status)
        return values
    }

    fun getChatMessage(cursor: Cursor, table: BaseChatMessageTable): ChatMessage {
        val chatMessage = ChatMessage()
        chatMessage.fromId = cursor.getString(cursor.getColumnIndex(table.COLUMN_FROM_ID))
        chatMessage.toId = cursor.getString(cursor.getColumnIndex(table.COLUMN_TO_ID))
        chatMessage.conversation = cursor.getString(cursor.getColumnIndex(table.COLUMN_CONVERSATION))
        chatMessage.type = cursor.getInt(cursor.getColumnIndex(table.COLUMN_TYPE))
        chatMessage.bodyType = cursor.getInt(cursor.getColumnIndex(table.COLUMN_BODY_TYPE))
        chatMessage.pid = cursor.getString(cursor.getColumnIndex(table.COLUMN_PID))
        chatMessage.time = cursor.getLong(cursor.getColumnIndex(table.COLUMN_TIME))
        chatMessage.body = cursor.getString(cursor.getColumnIndex(table.COLUMN_BODY))
        chatMessage.dev = cursor.getInt(cursor.getColumnIndex(table.COLUMN_DEV))
        chatMessage.topic = cursor.getString(cursor.getColumnIndex(table.COLUMN_TOPIC))
        chatMessage.cmd = cursor.getInt(cursor.getColumnIndex(table.COLUMN_CMD))
        chatMessage.status = cursor.getInt(cursor.getColumnIndex(table.COLUMN_STATUS))
        return chatMessage
    }


    fun getNewNumWhereSql(table: ImNewNumTable, conversation: String): String {
        var where = ""
        if (!TextUtils.isEmpty(conversation)) {
            if (TextUtils.isEmpty(where)) {
                where = "${table.COLUMN_CONVERSATION}='$conversation'"
            } else {
                where += " and ${table.COLUMN_CONVERSATION}='$conversation'"
            }
        }
        return where
    }

}
