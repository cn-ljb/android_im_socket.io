package com.ljb.socket.android.protocol.dao.impl

import android.content.ContentValues
import android.database.Cursor
import com.ljb.socket.android.db.DatabaseSqlHelper
import com.ljb.socket.android.model.ChatMessage
import com.ljb.socket.android.protocol.dao.IChatHistoryDaoProtocol
import com.ljb.socket.android.table.ImConversationTable
import com.senyint.ihospital.user.kt.db.table.ImHistoryTable
import dao.ljb.kt.core.BaseDaoProtocol
import io.reactivex.Observable
import net.ljb.kt.utils.NetLog

/**
 * Author:Ljb
 * Time:2018/11/10
 * There is a lot of misery in life
 **/

class ChatHistoryDaoProtocol : BaseDaoProtocol(), IChatHistoryDaoProtocol {

    override fun queryConversation(table: ImConversationTable, conversation: String) = createObservable {
        queryConversationImpl(table, conversation)
    }

    override fun getAllConversation(table: ImConversationTable): Observable<List<ChatMessage>> = createObservable {
        getAllConversationImpl(table)
    }

    override fun insertConversation(table: ImConversationTable, chatMessage: ChatMessage) = createObservable {
        insertConversationImpl(table, chatMessage)
    }

    override fun setVoiceMsgRead(table: ImHistoryTable, chatMessage: ChatMessage) = createObservable {
        setVoiceMsgReadImp(table, chatMessage)
    }

    override fun getChatHistory(table: ImHistoryTable, limitStart: Int, limitEnd: Int) = createObservable {
        getChatHistoryImpl(table, limitStart, limitEnd)
    }

    override fun getImageChatHistory(table: ImHistoryTable) = createObservable {
        getImageChatHistoryImpl(table)
    }


    override fun insertHistory(table: ImHistoryTable, chatMessage: ChatMessage) = createObservable {
        insertHistoryImpl(table, chatMessage)
    }

    private fun insertConversationImpl(table: ImConversationTable, chatMessage: ChatMessage): Boolean {
        var result = false
        var cursor: Cursor? = null
        val contentValue = DatabaseSqlHelper.getChatMessageContentValue(table, chatMessage)
        try {
            cursor = mSqliteDb.rawQuery("select * from ${table.getName()} where ${table.COLUMN_CONVERSATION} = '${chatMessage.conversation}'", null)
            if (cursor != null && cursor.moveToNext()) {
                val update = mSqliteDb.update(table.getName(), contentValue, "${table.COLUMN_CONVERSATION} = '${chatMessage.conversation}'", null)
                result = update > 0
            } else {
                val insert = mSqliteDb.insert(table.getName(), null, contentValue)
                result = insert != -1L
            }
        } catch (e: Exception) {
            NetLog.e(e)
        } finally {
            cursor?.close()
        }
        return result
    }


    private fun insertHistoryImpl(table: ImHistoryTable, chatMessage: ChatMessage): Boolean {
        var result = false
        var qCursor: Cursor? = null
        var maxQCursor: Cursor? = null

        val values: ContentValues = DatabaseSqlHelper.getChatMessageContentValue(table, chatMessage)
        values.put(table.COLUMN_READ, chatMessage.read)
        values.put(table.COLUMN_MSG_STATUS, chatMessage.status)
        try {
            qCursor = mSqliteDb.rawQuery("select * from ${table.getName()}  where ${table.COLUMN_PID} = '${chatMessage.pid}'", null)
            if (qCursor != null && qCursor.count > 0) {
                //已存在，更新
                val num = mSqliteDb.update(table.getName(), values, "${table.COLUMN_PID} = ?", arrayOf(chatMessage.pid))
                result = num > 0
            } else {
                //不存在，插入（不能超过1000条）
                maxQCursor = mSqliteDb.rawQuery("select count(*)  from ${table.getName()}", null)
                if (maxQCursor != null && maxQCursor.moveToNext()) {
                    val count = maxQCursor.getInt(0)
                    if (count > 999) {
                        mSqliteDb.execSQL("delete from ${table.getName()} where ${table.COLUMN_TIME} = (select min(${table.COLUMN_TIME}) from ${table.getName()})")
                    }
                }
                val num = mSqliteDb.insert(table.getName(), null, values)
                result = num != -1L
            }
        } catch (e: Exception) {
            NetLog.e(e)
        } finally {
            qCursor?.close()
            maxQCursor?.close()
        }

        return result
    }


    private fun setVoiceMsgReadImp(table: ImHistoryTable, chatMessage: ChatMessage): Boolean {
        val values = ContentValues()
        values.put(table.COLUMN_READ, chatMessage.read)
        val update = mSqliteDb.update(table.getName(), values, "${table.COLUMN_PID} = ?", arrayOf(chatMessage.pid))
        return update > 0
    }

    private fun getImageChatHistoryImpl(table: ImHistoryTable): List<ChatMessage> {
        val resultList = ArrayList<ChatMessage>()
        var cursor: Cursor? = null
        try {
            cursor = mSqliteDb.query(table.getName(), null, "${table.COLUMN_BODY_TYPE} = ?",
                    arrayOf(ChatMessage.MSG_BODY_TYPE_IMAGE.toString()),
                    null, null, table.COLUMN_TIME)
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val chatMessage: ChatMessage = DatabaseSqlHelper.getChatMessage(cursor, table)
                    chatMessage.read = cursor.getInt(cursor.getColumnIndex(table.COLUMN_READ))
                    chatMessage.status = cursor.getInt(cursor.getColumnIndex(table.COLUMN_MSG_STATUS))
                    resultList.add(chatMessage)
                }
            }
        } catch (e: Exception) {
            NetLog.e(e)
        } finally {
            cursor?.close()
        }
        return resultList
    }

    private fun getChatHistoryImpl(table: ImHistoryTable, limitStart: Int, limitEnd: Int): List<ChatMessage> {
        val resultList = ArrayList<ChatMessage>()
        var cursor: Cursor? = null
        try {
            cursor = mSqliteDb.rawQuery("select * from ${table.getName()} limit $limitStart , $limitEnd", null)
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val chatMessage: ChatMessage = DatabaseSqlHelper.getChatMessage(cursor, table)
                    chatMessage.read = cursor.getInt(cursor.getColumnIndex(table.COLUMN_READ))
                    resultList.add(chatMessage)
                }
            }
        } catch (e: Exception) {
            NetLog.e(e)
        } finally {
            cursor?.close()
        }
        return resultList
    }

    private fun getAllConversationImpl(table: ImConversationTable): List<ChatMessage> {
        val resultList = ArrayList<ChatMessage>()
        var cursor: Cursor? = null
        try {
            cursor = mSqliteDb.rawQuery("select * from ${table.getName()} order by ${table.COLUMN_TIME} desc", null)
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val chatMessage: ChatMessage = DatabaseSqlHelper.getChatMessage(cursor, table)
                    resultList.add(chatMessage)
                }
            }
        } catch (e: Exception) {
            NetLog.e(e)
        } finally {
            cursor?.close()
        }
        return resultList
    }

    private fun queryConversationImpl(table: ImConversationTable, conversation: String): ChatMessage? {
        var result: ChatMessage? = null
        var cursor: Cursor? = null
        try {
            cursor = mSqliteDb.rawQuery("select * from ${table.getName()} where ${table.COLUMN_CONVERSATION}= ?", arrayOf(conversation))
            if (cursor != null && cursor.moveToNext()) {
                result = DatabaseSqlHelper.getChatMessage(cursor, table)
            }
        } catch (e: Exception) {
            NetLog.e(e)
        } finally {
            cursor?.close()
        }
        return result
    }


}