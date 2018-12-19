package com.ljb.socket.android.protocol.dao

import com.ljb.socket.android.model.ChatMessage
import com.ljb.socket.android.table.ImConversationTable
import com.ljb.socket.android.table.ImHistoryTable
import dao.ljb.kt.core.IDaoInterface
import io.reactivex.Observable

/**
 * Author:Ljb
 * Time:2018/11/10
 * There is a lot of misery in life
 **/
interface IChatHistoryDaoProtocol : IDaoInterface {

    fun getChatHistory(table: ImHistoryTable, limitStart: Int, limitEnd: Int): Observable<List<ChatMessage>>

    fun getImageChatHistory(table: ImHistoryTable): Observable<List<ChatMessage>>

    fun setVoiceMsgRead(table: ImHistoryTable, chatMessage: ChatMessage): Observable<Boolean>

    fun insertHistory(table: ImHistoryTable, chatMessage: ChatMessage): Observable<Boolean>

    fun insertConversation(table: ImConversationTable, chatMessage: ChatMessage): Observable<Boolean>

    fun getAllConversation(table: ImConversationTable): Observable<List<ChatMessage>>

    fun queryConversation(mConversationTable: ImConversationTable, conversation: String): Observable<ChatMessage?>
}
